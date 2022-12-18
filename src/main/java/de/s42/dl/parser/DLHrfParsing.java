// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 * 
 * Copyright 2022 Studio 42 GmbH ( https://www.s42m.de ).
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
//</editor-fold>
package de.s42.dl.parser;

import de.s42.base.files.FilesHelper;
import de.s42.dl.parser.expression.DLHrfExpressionParser;
import de.s42.dl.*;
import de.s42.dl.exceptions.*;
import de.s42.dl.attributes.DefaultDLAttribute;
import de.s42.dl.types.DefaultDLEnum;
import de.s42.dl.types.DefaultDLType;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.util.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import static de.s42.dl.parser.DLHrfParsingErrorHandler.*;
import de.s42.dl.parser.DLParser.*;
import de.s42.dl.types.base.ArrayDLType;
import de.s42.dl.validation.ValidationResult;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Benjamin Schiller
 */
public class DLHrfParsing extends DLParserBaseListener
{

	private final static Logger log = LogManager.getLogger(DLHrfParsing.class.getName());

	private final Deque<DLInstance> instances = new ArrayDeque<>();
	private final DLCore core;
	private final DLModule module;

	private DLInstance currentInstance;
	private DLInstance lastInstance;
	private DefaultDLType currentType;
	private DefaultDLEnum currentEnum;

	// @todo https://github.com/studio42gmbh/dl/issues/18 this is a preliminary prototypal implementation of dlinstance attribute assignments!
	private final Deque<AttributeAssignmentContext> attributeAssignableContextQueue = new ArrayDeque<>();
	private final Deque<String> attributeAssignableKeyQueue = new ArrayDeque<>();
	private final Deque<DLInstance> currentAttributeAssignmentInstances = new ArrayDeque<>();
	protected DefaultDLAttribute dlInstanceAssignAttribute;

	protected interface AnnotationMapper
	{

		public void accept(String annotationName, Object[] parameters, AnnotationContext ctx) throws DLException;
	}

	public DLHrfParsing(DLCore core, DLModule module)
	{
		this.core = core;
		this.module = module;
		currentInstance = module;
		instances.push(currentInstance);
	}

	protected Object resolveReference(String refId, ParserRuleContext context) throws InvalidValue
	{
		Optional ref = module.resolveReference(core, refId);

		if (ref.isEmpty()) {
			throw new InvalidValue(createErrorMessage(module, "Reference $" + refId + " is not defined in module", context));
		}

		return ref.orElseThrow();
	}

	protected DLType fetchTypeIdentifier(InstanceTypeContext ctx) throws DLException
	{
		if (ctx == null) {
			return null;
		}

		String typeName = ctx.typeIdentifier().identifier().getText();

		// Map generics
		List<DLType> genericTypes = fetchGenericParameters(ctx.genericParameters());

		Optional<DLType> optType = core.getType(typeName, genericTypes);

		if (optType.isEmpty()) {
			throw new InvalidType(createErrorMessage(module, "Type '" + typeName + "' is not defined", ctx));
		}

		return optType.orElseThrow();
	}

	protected Object[] fetchAssignables(AttributeAssignmentContext ctx) throws DLException
	{
		if (ctx.attributeAssignable() == null || ctx.attributeAssignable().isEmpty()) {
			return null;
		}

		Object[] assignables = new Object[ctx.attributeAssignable().size()];
		int i = 0;

		for (AttributeAssignableContext assignable : ctx.attributeAssignable()) {

			if (assignable.STRING_LITERAL() != null) {
				assignables[i] = assignable.getText();
			} else if (assignable.SYMBOL() != null) {
				assignables[i] = assignable.getText();
			} else if (assignable.INTEGER_LITERAL() != null) {
				// https://github.com/studio42gmbh/dl/issues/26
				String t = assignable.INTEGER_LITERAL().getText();
				if (t.startsWith("0") && t.length() > 1) {
					if (t.startsWith("0x")) {
						assignables[i] = Long.parseLong(t.substring(2), 16);
					} else if (t.startsWith("0b")) {
						assignables[i] = Long.parseLong(t.substring(2), 2);
					} else {
						assignables[i] = Long.parseLong(t.substring(1), 8);
					}
				} else {
					assignables[i] = Long.parseLong(t);
				}
			} else if (assignable.FLOAT_LITERAL() != null) {
				assignables[i] = Double.parseDouble(assignable.FLOAT_LITERAL().getText());
			} else if (assignable.BOOLEAN_LITERAL() != null) {
				if ("true".equals(assignable.BOOLEAN_LITERAL().getText())) {
					assignables[i] = true;
				} else {
					assignables[i] = false;
				}
			} else if (assignable.REF() != null) {
				try {
					assignables[i] = resolveReference(assignable.getText(), ctx);
				} catch (RuntimeException ex) {
					throw new InvalidValue(createErrorMessage(
						module,
						"Error retrieving rdef " + ex.getMessage(),
						ctx), ex);
				}
			} else if (assignable.expression() != null) {
				//log.debug("Expression", assignable.expression().getText());
				try {
					assignables[i] = DLHrfExpressionParser.resolveExpression(core, module, assignable.expression());
				} catch (RuntimeException ex) {
					throw new InvalidValue(createErrorMessage(
						module,
						"Error parsing DL expression " + ex.getMessage(),
						assignable.expression()), ex);
				}
			}

			i++;
		}

		return assignables;
	}

	protected Object[] fetchStaticParameters(String annotationName, StaticParametersContext ctx) throws InvalidValue, DLException
	{
		DLAnnotationFactory annotationFactory = core.getAnnotationFactory(annotationName).orElseThrow(() -> {
			return new InvalidAnnotation(createErrorMessage(module, "Annotation factory '" + annotationName + "' is not defined", ctx));
		});

		if (ctx == null || ctx.staticParameter() == null || ctx.staticParameter().isEmpty()) {

			/*if (!annotationFactory.isValidFlatParameters(new Object[0])) {
				throw new InvalidAnnotation(createErrorMessage(module, "Flat parameters are not a valid for annotation '" + annotationName + "'", ctx));
			}*/
			return new Object[0];
		}

		int size = ctx.staticParameter().size();

		// Get named parameters if present - all parameters have to be named then
		if (size > 0 && isNamedStaticParameter(ctx.staticParameter().get(0))) {

			Map<String, Object> namedParameters = new HashMap<>();

			for (int i = 0; i < size; ++i) {

				Pair<String, Object> namedParameter = fetchNamedStaticParameter(ctx.staticParameter().get(i));

				if (!annotationFactory.isValidNamedParameter(namedParameter.a, namedParameter.b)) {
					throw new InvalidAnnotation(createErrorMessage(module, "Parameter " + namedParameter.a + " is not a valid named parameter in annotation '" + annotationName + "' - value " + namedParameter.b, ctx.staticParameter(i)));
				}

				namedParameters.put(namedParameter.a, namedParameter.b);
			}

			return annotationFactory.toFlatParameters(namedParameters);
		}

		// Fill unnamed parameters in order
		Object[] parameters = new Object[size];

		for (int i = 0; i < size; ++i) {
			parameters[i] = fetchStaticParameter(ctx.staticParameter().get(i));
		}

		if (!annotationFactory.isValidFlatParameters(parameters)) {
			throw new InvalidAnnotation(createErrorMessage(module, "Flat parameters are not a valid for annotation '" + annotationName + "'", ctx));
		}

		return parameters;
	}

	protected boolean isNamedStaticParameter(StaticParameterContext ctx) throws InvalidValue
	{
		return ctx.staticParameterName() != null;
	}

	protected Object[] fetchStaticParameters(StaticParametersContext ctx) throws InvalidValue
	{
		if (ctx == null || ctx.staticParameter() == null || ctx.staticParameter().isEmpty()) {
			return new Object[0];
		}

		int size = ctx.staticParameter().size();

		Object[] parameters = new Object[size];

		for (int i = 0; i < size; ++i) {
			parameters[i] = fetchStaticParameter(ctx.staticParameter().get(i));
		}

		return parameters;
	}

	protected Pair<String, Object> fetchNamedStaticParameter(StaticParameterContext ctx) throws InvalidValue
	{
		String name = ctx.staticParameterName().identifier().getText();
		Object value = fetchStaticParameter(ctx);

		return new Pair<>(name, value);
	}

	protected Object fetchStaticParameter(StaticParameterContext ctx) throws InvalidValue
	{
		if (ctx.STRING_LITERAL() != null) {
			return ctx.STRING_LITERAL().getText();
		} else if (ctx.SYMBOL() != null) {
			return ctx.SYMBOL().getText();
		} else if (ctx.INTEGER_LITERAL() != null) {
			return Long.parseLong(ctx.INTEGER_LITERAL().getText());
		} else if (ctx.FLOAT_LITERAL() != null) {
			return Double.parseDouble(ctx.FLOAT_LITERAL().getText());
		} else if (ctx.BOOLEAN_LITERAL() != null) {
			if ("true".equals(ctx.BOOLEAN_LITERAL().getText())) {
				return true;
			} else {
				return false;
			}
		}

		throw new InvalidValue(createErrorMessage(module, "Unknown parameter type", ctx));
	}

	public List<DLType> fetchGenericParameters(GenericParametersContext ctx) throws DLException
	{
		List<DLType> genericTypes = new ArrayList<>();

		// parse generic types
		if (ctx != null) {

			for (GenericParameterContext genericParameter : ctx.genericParameter()) {

				String genericTypeName = genericParameter.getText();

				if (!core.hasType(genericTypeName)) {
					throw new UndefinedType(createErrorMessage(module, "Attribute generic type '" + genericTypeName + "' is not defined", genericParameter));
				}

				genericTypes.add(core.getType(genericTypeName).get());
			}
		}

		return genericTypes;
	}

	public void mapAnnotations(List<AnnotationContext> annotations, AnnotationMapper mapper) throws DLException
	{
		assert annotations != null;
		assert mapper != null;

		for (AnnotationContext ctx : annotations) {

			String annotationName = ctx.annotationName().getText();

			if (!core.hasAnnotationFactory(annotationName)) {
				throw new UndefinedAnnotation(createErrorMessage(module, "Annotation factory '" + annotationName + "' is not defined", ctx));
			}

			Object[] parameters = fetchStaticParameters(annotationName, ctx.staticParameters());

			mapper.accept(annotationName, parameters, ctx);
		}
	}

	@Override
	public void enterPragma(PragmaContext ctx)
	{
		try {

			// Define a pragma
			if (ctx.KEYWORD_EXTERN() != null) {

				if (!core.isAllowDefinePragmas()) {
					throw new InvalidCore(createErrorMessage(module, "May not define pragmas in core", ctx));
				}

				if (ctx.staticParameters() != null) {
					throw new InvalidPragma(createErrorMessage(module, "May not give parameters when defining a pragma", ctx));
				}

				String pragmaIdentifier = ctx.pragmaName().getText();

				// Instantiate new pragma
				DLPragma pragma = (DLPragma) Class.forName(pragmaIdentifier, true, core.getClassLoader()).getConstructor().newInstance();

				// Define new pragma
				core.definePragma(pragma);

				// Map annotations
				mapAnnotations(ctx.annotation(), (annotationName, parameters, aCtx) -> {
					try {
						core.createAnnotation(annotationName, pragma, parameters);
					} catch (DLException ex) {
						throw new InvalidAnnotation(
							createErrorMessage(module,
								"Error binding annotation '"
								+ annotationName
								+ "' to pragma '"
								+ currentEnum.getName() + "'", ex, aCtx.annotationName()), ex);
					}
				});

				// Map aliases
				if (ctx.aliases() != null) {
					for (AliasNameContext aliasCtx : ctx.aliases().aliasName()) {
						core.defineAliasForPragma(aliasCtx.identifier().getText(), pragma);
					}
				}

			} // Use a pragma
			else {

				if (!core.isAllowUsePragmas()) {
					throw new InvalidCore(createErrorMessage(module, "May not use pragmas in core", ctx));
				}

				if (ctx.aliases() != null) {
					throw new InvalidPragma(createErrorMessage(module, "May not define alias when using a pragma", ctx));
				}

				if (!ctx.annotation().isEmpty()) {
					throw new InvalidPragma(createErrorMessage(module, "May not bind annotations when using a pragma", ctx));
				}

				String pragmaIdentifier = ctx.pragmaName().getText();

				Object[] parameters = fetchStaticParameters(ctx.staticParameters());

				try {
					core.doPragma(pragmaIdentifier, parameters);
				} catch (InvalidPragma ex) {
					throw new InvalidPragma(createErrorMessage(module, "Error in pragma execution '" + pragmaIdentifier + "' - " + ex.getMessage(), ctx), ex);
				}
			}

		} catch (DLException | ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | RuntimeException | InvocationTargetException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void enterAlias(AliasContext ctx)
	{
		try {
			String aliasRedefinitionName = ctx.aliasRedefinition().getText();
			String aliasDefinitionName = ctx.aliasDefinition().getText();

			// Alias for types
			if (core.hasType(aliasDefinitionName)) {

				if (!core.isAllowDefineTypes()) {
					throw new InvalidCore(createErrorMessage(module, "May not define types in core", ctx));
				}

				// Alias redefinition type may not be defined already
				if (core.hasType(aliasRedefinitionName)) {
					throw new InvalidType(createErrorMessage(module, "Error alias redef type '" + aliasRedefinitionName + "' is already defined", ctx));
				}

				core.defineAliasForType(aliasRedefinitionName, core.getType(aliasDefinitionName).orElseThrow());
			} // Alias for annotations
			else if (core.hasAnnotationFactory(aliasDefinitionName)) {

				if (!core.isAllowDefineAnnotationFactories()) {
					throw new InvalidCore(createErrorMessage(module, "May not define annotations in core", ctx));
				}

				// Alias redefinition type may not be defined already
				if (core.hasAnnotationFactory(aliasRedefinitionName)) {
					throw new InvalidAnnotation(createErrorMessage(module, "Error alias redef annotation '" + aliasRedefinitionName + "' is already defined", ctx));
				}

				core.defineAliasForAnnotationFactory(aliasRedefinitionName, aliasDefinitionName);
			} // Alias for pragmas
			else if (core.hasPragma(aliasDefinitionName)) {

				if (!core.isAllowDefinePragmas()) {
					throw new InvalidCore(createErrorMessage(module, "May not define pragmas in core", ctx));
				}

				// Alias redefinition type may not be defined already
				if (core.hasPragma(aliasRedefinitionName)) {
					throw new InvalidPragma(createErrorMessage(module, "Error alias redef pragma '" + aliasRedefinitionName + "' is already defined", ctx));
				}

				core.defineAliasForPragma(aliasRedefinitionName, core.getPragma(aliasDefinitionName).orElseThrow());
			} // Neither type, annotation or pragma definition found
			else {
				throw new InvalidCore(createErrorMessage(module, "Error alias def type, annotation or pragma '" + aliasDefinitionName + "' is not defined", ctx));
			}

		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	@SuppressWarnings("UseSpecificCatch")
	public void enterRequire(RequireContext ctx)
	{
		try {

			if (!core.isAllowRequire()) {
				throw new InvalidCore("Not allowed to require in core");
			}

			try {
				DLModule requiredModule = core.parse(ctx.requireModule().getText());
				module.addChild(requiredModule);
			} catch (Throwable ex) {
				throw new InvalidModule(createErrorMessage(module, "Error requiring module '" + ctx.requireModule().getText() + "'", ex, ctx.requireModule()), ex);
			}
		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void enterEnumDefinition(EnumDefinitionContext ctx)
	{
		try {
			// Define an external enum from java
			if (ctx.KEYWORD_EXTERN() != null) {

				String enumName = ctx.enumName().getText();

				if (core.hasEnum(enumName)) {
					throw new InvalidType(createErrorMessage(module, "Extern enum '" + enumName + "' is already defined", ctx));
				}

				try {

					DLEnum enumImpl = core.createEnum((Class<Enum>) Class.forName(enumName));

					// Map the enum as defined
					core.defineType(enumImpl);

					// Define alias for the given annotationName
					if (!enumImpl.getName().equals(enumName)) {
						core.defineAliasForType(enumName, enumImpl);
					}

					// Define aliases from enum definition
					if (ctx.aliases() != null) {
						for (AliasNameContext aliasCtx : ctx.aliases().aliasName()) {
							core.defineAliasForType(aliasCtx.identifier().getText(), enumImpl);
						}
					}

				} catch (ClassNotFoundException ex) {
					throw new InvalidType(createErrorMessage(module, "Class not found for enum '" + enumName + "' - " + ex.getMessage(), ctx), ex);
				}
			} // Define a new enum
			else {

				currentEnum = (DefaultDLEnum) core.createEnum();
				currentEnum.setName(ctx.enumName().getText());
				core.defineType(currentEnum);

				// Map annotations
				mapAnnotations(ctx.annotation(), (annotationName, parameters, aCtx) -> {
					try {
						core.createAnnotation(annotationName, currentEnum, parameters);
					} catch (DLException ex) {
						throw new InvalidAnnotation(
							createErrorMessage(module,
								"Error binding annotation '"
								+ annotationName
								+ "' to enum '"
								+ currentEnum.getName() + "'", ex, aCtx.annotationName()), ex);
					}
				});

				// Define aliases from enum definition
				if (ctx.aliases() != null) {
					for (AliasNameContext aliasCtx : ctx.aliases().aliasName()) {
						core.defineAliasForType(aliasCtx.identifier().getText(), currentEnum);
					}
				}

				// Map values
				for (EnumValueDefinitionContext aCtx : ctx.enumBody().enumValueDefinition()) {

					if (aCtx.symbolOrString() != null) {

						currentEnum.addValue(aCtx.symbolOrString().getText());
					}
				}
			}
		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void enterInstanceDefinition(InstanceDefinitionContext ctx)
	{
		try {
			DLType type = null;

			if (ctx.instanceType() != null) {
				type = fetchTypeIdentifier(ctx.instanceType());
			} else {
				throw new InvalidInstance("Missing type identifier");
			}

			if (type.isAbstract()) {
				throw new InvalidInstance(createErrorMessage(module, "Error creating instance - type " + type.getCanonicalName() + " is abstract", ctx.instanceType().typeIdentifier()));
			}

			// https://github.com/studio42gmbh/dl/issues/23 dont allow simple types to be generated as instances -> simple types have to be assigned?
			/*if (type.isSimpleType()) {
				throw new InvalidType(createErrorMessage("Error creating instance as type " + type.getCanonicalName() + " is simple", ctx.instanceType().typeIdentifier()));
			}*/
			String identifier = null;

			if (ctx.instanceName() != null) {
				identifier = ctx.instanceName().getText();
			}

			DLInstance instance = core.createInstance(type, identifier);

			// Map annotations
			mapAnnotations(ctx.annotation(), (annotationName, parameters, aCtx) -> {
				try {
					core.createAnnotation(annotationName, instance, parameters);
				} catch (DLException ex) {
					throw new InvalidAnnotation(
						createErrorMessage(module,
							"Error binding annotation '"
							+ annotationName
							+ "' to instance '"
							+ instance.getName() + "'", ex, aCtx.annotationName()), ex);
				}
			});

			//core.getLog().debug("Created instance", name, "in module", module.getName());
			try {
				// make sure not to add sub instances which are assigned in attribute assignments
				if (currentAttributeAssignmentInstances.peek() != currentInstance) {

					//log.debug("enterInstanceDefinition addChild " + currentInstance.getName() + " "  + instance.getName());
					currentInstance.addChild(instance);
				}
			} catch (DLException | RuntimeException ex) {
				throw new InvalidInstance(createErrorMessage(module, "Error adding child", ex, ctx), ex);
			}

			instances.push(currentInstance);
			currentInstance = instance;
		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void exitInstanceDefinition(InstanceDefinitionContext ctx)
	{
		try {
			// @todo https://github.com/studio42gmbh/dl/issues/18 DLHrfParsing support multi nested instances in attribute assignment - currently just 1 stack is allowed
			if (currentInstance.getType() != null) {

				//log.debug("currentInstance validate " + currentInstance.getName());
				ValidationResult result = new ValidationResult();
				if (!currentInstance.validate(result)) {
					throw new InvalidInstance(createErrorMessage(module, "Error validating instance - " + result.toMessage(), ctx));
				}
			}

			lastInstance = currentInstance;
			currentInstance = instances.pop();
		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void enterAnnotationDefinition(AnnotationDefinitionContext ctx)
	{
		try {

			if (!core.isAllowDefineAnnotationFactories()) {
				throw new InvalidCore("Not allowed to define annotations in core");
			}

			//keyword extern found
			if (ctx.KEYWORD_EXTERN() != null) {

				String annotationName = ctx.annotationDefinitionName().getText();

				if (core.hasAnnotationFactory(annotationName)) {
					throw new InvalidAnnotation(createErrorMessage(module, "Annotation '" + annotationName + "' is already defined", ctx));
				}

				try {

					DLAnnotationFactory annotationFactory = ((Class<DLAnnotationFactory>) Class.forName(annotationName)).getConstructor().newInstance();

					// map the annotation as defined
					core.defineAnnotationFactory(annotationFactory, annotationName);

					// Define aliases from type definition
					if (ctx.aliases() != null) {
						for (AliasNameContext aliasCtx : ctx.aliases().aliasName()) {
							core.defineAliasForAnnotationFactory(aliasCtx.identifier().getText(), annotationName);
						}
					}

				} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
					throw new InvalidAnnotation(createErrorMessage(module, "Class not found for annotation '" + annotationName + "' - " + ex.getMessage(), ctx), ex);
				}

			} else {
				throw new InvalidAnnotation(createErrorMessage(module, "Annotations can not be defined internally yet", ctx));
			}

			// @improvement define annotations internal - something like combining others with boolean like contracts?
		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void enterTypeDefinition(TypeDefinitionContext ctx)
	{
		try {

			if (!core.isAllowDefineTypes()) {
				throw new InvalidCore(createErrorMessage(module, "Not allowed to define types in core", ctx));
			}

			String typeName = ctx.typeDefinitionName().getText();

			// Declare a type
			if (ctx.KEYWORD_DECLARE() != null) {

				// Extension is not allowed for extern types
				if (ctx.KEYWORD_EXTENDS() != null) {
					throw new InvalidType(createErrorMessage(module, "Declared type '" + typeName + "' may not extend other types", ctx));
				}

				// Containment is not allowed for extern types
				if (ctx.KEYWORD_CONTAINS() != null) {
					throw new InvalidType(createErrorMessage(module, "Declared type '" + typeName + "' may not contain other types", ctx));
				}

				// Annotations are not allowed on extern types
				if (!ctx.annotation().isEmpty()) {
					throw new InvalidAnnotation(createErrorMessage(module, "Declared type '" + typeName + "' may not have annotations", ctx));
				}

				// Annotations are not allowed on extern types
				if (ctx.typeBody() != null) {
					throw new InvalidType(createErrorMessage(module, "Declared type '" + typeName + "' may not have a body", ctx));
				}

				if (!core.hasType(typeName)) {

					currentType = (DefaultDLType) core.declareType(typeName);
				}
			} // Define an extern type
			else if (ctx.KEYWORD_EXTERN() != null) {

				// Dont allow external on types that are already present
				if (core.hasType(typeName)) {
					throw new InvalidType(createErrorMessage(module, "Type '" + typeName + "' is already defined", ctx));
				}

				// Extension is not allowed for extern types
				if (ctx.KEYWORD_EXTENDS() != null) {
					throw new InvalidType(createErrorMessage(module, "Extern type '" + typeName + "' may not extend other types", ctx));
				}

				// Containment is not allowed for extern types
				if (ctx.KEYWORD_CONTAINS() != null) {
					throw new InvalidType(createErrorMessage(module, "Extern type '" + typeName + "' may not contain other types", ctx));
				}

				// Annotations are not allowed on extern types
				if (!ctx.annotation().isEmpty()) {
					throw new InvalidAnnotation(createErrorMessage(module, "Extern type '" + typeName + "' may not have annotations", ctx));
				}

				// Annotations are not allowed on extern types
				if (ctx.typeBody() != null) {
					throw new InvalidType(createErrorMessage(module, "Extern type '" + typeName + "' may not have a body", ctx));
				}

				// Define type from extern definition
				try {
					currentType = (DefaultDLType) core.createType(Class.forName(typeName));

					// map the new type
					core.defineType(currentType);

					// define alias for the given typeName
					if (!currentType.getName().equals(typeName)) {
						core.defineAliasForType(typeName, currentType);
					}

					// Define aliases from type definition
					if (ctx.aliases() != null) {
						for (AliasNameContext aliasCtx : ctx.aliases().aliasName()) {
							core.defineAliasForType(aliasCtx.identifier().getText(), currentType);
						}
					}

				} catch (ClassNotFoundException ex) {
					throw new InvalidType(createErrorMessage(module, "Class not found for type '" + typeName + "' - " + ex.getMessage(), ctx), ex);
				}
			} // Define a type
			else {

				Optional<DLType> optDefinedType = core.getType(typeName);

				// If type is present -> it has to be a declaration -> otherwise ex
				if (optDefinedType.isPresent()) {

					DLType definedType = optDefinedType.orElseThrow();

					if (!definedType.isDeclaration()) {
						throw new InvalidType(createErrorMessage(module, "Type '" + typeName + "' is already defined", ctx));
					}

					currentType = (DefaultDLType) definedType;
				} // Otherwise define a new type
				else {
					currentType = (DefaultDLType) core.defineType(core.createType(typeName));
				}

				// Make type abstract
				if (ctx.KEYWORD_ABSTRACT() != null) {
					currentType.setAbstract(true);
				} // Make type final
				else if (ctx.KEYWORD_FINAL() != null) {
					currentType.setFinal(true);
				}

				// Map annotations
				mapAnnotations(ctx.annotation(), (annotationName, parameters, aCtx) -> {
					try {
						core.createAnnotation(annotationName, currentType, parameters);
					} catch (DLException ex) {
						throw new InvalidAnnotation(createErrorMessage(module, "Error binding annotation '" + annotationName + "' to type '" + currentType.getName() + "'", ex, aCtx), ex);
					}
				});

				// Extends - set parents
				if (ctx.parentTypeName() != null) {

					for (ParentTypeNameContext pCtx : ctx.parentTypeName()) {

						String parentTypeName = pCtx.getText();

						DLType parentType = core.getType(parentTypeName).orElseThrow(() -> {
							return new UndefinedType(createErrorMessage(module, "Parent type '" + parentTypeName + "' is not defined", pCtx));
						});

						if (parentType.isFinal()) {
							throw new InvalidType(createErrorMessage(module, "Parent type " + parentType.getCanonicalName() + " is final and can not be derived from in " + currentType.getCanonicalName(), pCtx));
						}

						currentType.addParent(parentType);
					}
				}

				// Contains - set contained types
				if (ctx.containsTypeName() != null) {

					for (ContainsTypeNameContext pCtx : ctx.containsTypeName()) {

						String containsTypeName = pCtx.getText();

						DLType containsType = core.getType(containsTypeName).orElseThrow(() -> {
							return new UndefinedType(createErrorMessage(module, "Contains type '" + containsTypeName + "' is not defined", pCtx));
						});

						currentType.addContainedType(containsType);
					}
				}

				// Define aliases from type definition
				if (ctx.aliases() != null) {
					for (AliasNameContext aliasCtx : ctx.aliases().aliasName()) {
						core.defineAliasForType(aliasCtx.identifier().getText(), currentType);
					}
				}
			}
		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void exitTypeDefinition(TypeDefinitionContext ctx)
	{
		try {
			ValidationResult result = new ValidationResult();
			if (!currentType.validate(result)) {
				throw new InvalidType(createErrorMessage(module, "Type '" + currentType.getCanonicalName() + "' is not valid - " + result.toMessage(), ctx));
			}

		} catch (InvalidType ex) {
			throw new RuntimeException(ex);
		}
	}

	// @todo https://github.com/studio42gmbh/dl/issues/28 DLHrfParsing Allow multiple value and expression assignment as default values in type attribute definition
	@Override
	public void enterTypeAttributeDefinition(TypeAttributeDefinitionContext ctx)
	{
		try {
			String typeName = ctx.typeAttributeDefinitionType().typeIdentifier().getText();

			if (!core.hasType(typeName)) {
				throw new UndefinedType(createErrorMessage(module, "Attribute type '" + typeName + "' is not defined", ctx));
			}

			List<DLType> genericTypes = fetchGenericParameters(ctx.typeAttributeDefinitionType().genericParameters());

			DLType type;

			try {
				type = core.getType(typeName, genericTypes).get();
			} catch (InvalidType ex) {
				throw new InvalidType(createErrorMessage(module, "Error retrieving type '" + typeName + "'", ex, ctx.typeAttributeDefinitionType().typeIdentifier()), ex);
			}

			String name = ctx.typeAttributeDefinitionName().getText();

			DefaultDLAttribute attribute;
			try {
				attribute = (DefaultDLAttribute) core.createAttribute(name, type, currentType);
			} catch (InvalidType ex) {
				throw new InvalidType(createErrorMessage(module, "Error add attribute '" + name + "' to type '" + typeName + "'", ex, ctx.typeAttributeDefinitionType().typeIdentifier()), ex);
			}

			// Parse default value
			Object defaultValue = null;
			if (ctx.typeAttributeDefinitionDefault() != null) {

				//DLInstance localCurrentInstance = currentInstance;
				if (ctx.typeAttributeDefinitionDefault().instanceDefinition() != null) {
					dlInstanceAssignAttribute = attribute;
				} // Resolve and validate type of reference
				else if (ctx.typeAttributeDefinitionDefault().REF() != null) {

					Object ref = resolveReference(ctx.typeAttributeDefinitionDefault().getText(), ctx.typeAttributeDefinitionDefault());

					if (ref instanceof DLInstance) {
						DLType refType = ((DLInstance) ref).getType();

						if (type != null && refType == null) {
							throw new InvalidType(createErrorMessage(module,
								"Type of reference $" + ctx.typeAttributeDefinitionDefault().getText()
								+ " is not matching it should be " + type.getName(), ctx.typeAttributeDefinitionDefault()));
						}

						if (type != null && !type.isAssignableFrom(refType)) {
							throw new InvalidType(createErrorMessage(module,
								"Type of reference $" + ctx.typeAttributeDefinitionDefault().getText()
								+ " is not matching it is " + refType.getName()
								+ " but should be " + type.getName(), ctx.typeAttributeDefinitionDefault()));
						}
					}

					defaultValue = ref;
				} else {
					try {
						defaultValue = type.read(ctx.typeAttributeDefinitionDefault().getText());
					} catch (AssertionError | Exception ex) {
						throw new InvalidValue(createErrorMessage(module, "Error reading default value for attribute '"
							+ name + "'", ex, ctx.typeAttributeDefinitionDefault()), ex);
					}
				}
			}
			attribute.setDefaultValue(defaultValue);

			// Map annotations
			mapAnnotations(ctx.annotation(), (annotationName, parameters, aCtx) -> {
				try {
					core.createAnnotation(annotationName, attribute, parameters);
				} catch (DLException ex) {
					throw new InvalidAnnotation(createErrorMessage(module, "Error binding annotation '"
						+ annotationName + "' to attribute '" + attribute.getName() + "'", ex, aCtx.annotationName()), ex);
				}
			});

			if (!attribute.getType().validate(new ValidationResult())) {
				throw new InvalidType(createErrorMessage(module, "Attribute type '" + attribute.getType().getCanonicalName() + "' is not valid", ctx));
			}

		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void exitTypeAttributeDefinition(TypeAttributeDefinitionContext ctx)
	{
		if (dlInstanceAssignAttribute != null) {
			dlInstanceAssignAttribute.setDefaultValue(lastInstance);
			dlInstanceAssignAttribute = null;
		}
	}

	// @todo https://github.com/studio42gmbh/dl/issues/17 DLHrfParsing improve, refactor and cleanup enterAttributeAssignment
	@Override
	@SuppressWarnings("null")
	public void enterAttributeAssignment(AttributeAssignmentContext ctx)
	{
		try {
			currentAttributeAssignmentInstances.push(currentInstance);

			DefaultDLType instanceType = (DefaultDLType) currentInstance.getType();

			AttributeAssignableContext assignable = null;

			if (!ctx.attributeAssignable().isEmpty()) {
				assignable = ctx.attributeAssignable().get(0);
			}

			Object[] assignables = fetchAssignables(ctx);

			// Type key -> explicit type or type collision if instance defines it
			if (ctx.attributeType() != null) {
				String typeName = ctx.attributeType().typeIdentifier().getText();
				String key = ctx.attributeName().getText();

				if (currentInstance.hasAttribute(key)) {
					throw new InvalidAttribute(createErrorMessage(module, "Instance " + currentInstance.getName()
						+ " already has the attribute " + key, ctx.attributeName()));
				}

				List<DLType> genericTypes = fetchGenericParameters(ctx.attributeType().genericParameters());

				Optional<DLType> optType = core.getType(typeName, genericTypes);

				if (optType.isEmpty()) {
					throw new UndefinedType(createErrorMessage(module, "Type '" + typeName + "' is not defined", ctx.attributeType()));
				}

				DLType type = optType.orElseThrow();

				// Check if type contradicts
				if (instanceType != null) {

					if (instanceType.hasAttribute(key)) {
						if (type != currentInstance.getType().getAttribute(key).orElseThrow().getType()) {
							throw new InvalidType(createErrorMessage(module, "Defined attribute type and given instance type do not match "
								+ type.getClass().getName() + " <> "
								+ currentInstance.getType().getAttribute(key).orElseThrow().getType().getClass().getName(), ctx));
						}
					} else if (!instanceType.isAllowDynamicAttributes()) {
						throw new InvalidType(createErrorMessage(module, "Instance type " + instanceType.getClass().getName() + " does not allow dynamic attributes - " + key, ctx));
					}
				}

				if (assignables != null && assignables.length == 1 && assignable.instanceDefinition() != null) {

					attributeAssignableContextQueue.push(ctx);
					attributeAssignableKeyQueue.push(key);

					//log.debug("enterAttributeAssignment.instanceDefinition2 {} {}", key,  ctx.attributeSymbol().getText());
				} // Resolve and validate type of reference
				else if (assignables != null && assignables.length == 1 && assignable.REF() != null) {

					Object ref = resolveReference(assignable.getText(), assignable);

					if (ref instanceof DLInstance) {

						DLType refType = ((DLInstance) ref).getType();

						if (instanceType.hasAttribute(key)) {
							if (!instanceType.getAttribute(key).orElseThrow().getType().isAssignableFrom(refType)) {
								throw new InvalidType(createErrorMessage(module,
									"Type of reference $" + assignable.getText()
									+ " is not matching it is " + refType.getName()
									+ " but should be " + instanceType.getAttribute(key).orElseThrow().getType().getCanonicalName(), ctx));
							}
						} else if (!instanceType.isAllowDynamicAttributes()) {
							throw new InvalidValue(createErrorMessage(module, "Instance type " + instanceType + " does not allow dynamic attributes - " + key, ctx));
						}
					}

					currentInstance.set(key, ref);
				} else {

					try {
						currentInstance.set(key, type.read(assignables));
					} catch (InvalidType ex2) {
						throw new InvalidType(createErrorMessage(module, "InvalidType", ex2, ctx), ex2);
					} catch (AssertionError | Exception ex) {
						throw new InvalidValue(createErrorMessage(module, "Error reading value", ex, ctx), ex);
					}
				}
			} // Just key -> infer from instance or auto type (string, float, int, boolean)
			else {

				String key = ctx.attributeName().getText();

				if (currentInstance.hasAttribute(key)) {
					throw new InvalidAttribute(createErrorMessage(module, "Instance " + currentInstance.getName()
						+ " already has the attribute " + key, ctx.attributeName()));
				}

				//instance defines attribute type -> explicit type
				if (instanceType != null) {

					if (instanceType.hasAttribute(key)) {

						if (assignables != null && assignables.length == 1 && assignable.instanceDefinition() != null) {
							attributeAssignableContextQueue.push(ctx);
							attributeAssignableKeyQueue.push(key);

							//log.debug("enterAttributeAssignment.instanceDefinition {} {}", key,  ctx.attributeSymbol().getText());
						} else if (assignables != null
							&& assignables.length == 1
							&& assignable.REF() != null
							&& // @todo https://github.com/studio42gmbh/dl/issues/17 DL this way of preventing arrays to be matched has to be optimized
							!(currentInstance.getType().getAttribute(key).orElseThrow().getType() instanceof ArrayDLType)) {

							Object ref = resolveReference(assignable.getText(), assignable);

							if (ref instanceof DLInstance) {

								DLType refType = ((DLInstance) ref).getType();

								if (!refType.isDerivedTypeOf(currentInstance.getType().getAttribute(key).orElseThrow().getType())) {
									throw new InvalidType(createErrorMessage(module, "Type of reference $" + assignable.getText() + " is not matching it is " + refType.getCanonicalName() + " but should be " + currentInstance.getType().getAttribute(key).orElseThrow().getType().getCanonicalName(), ctx));
								}
							}

							currentInstance.set(key, ref);
						} else {
							try {
								//currentInstance.set(key, currentInstance.getType().getAttribute(key).getType().read(assignable.getText()));
								currentInstance.set(key, currentInstance.getType().getAttribute(key).orElseThrow().getType().read(assignables));

							} catch (InvalidType ex2) {
								throw new InvalidType(createErrorMessage(module, "InvalidType", ex2, ctx), ex2);
							} catch (AssertionError | Exception ex) {
								throw new InvalidValue(createErrorMessage(module, "Instance value " + key + " could not be set", ex, assignable), ex);
							}
						}

						return;
					} else if (!instanceType.isAllowDynamicAttributes()) {
						throw new InvalidValue(createErrorMessage(module, "Instance type " + instanceType.getName() + " does not allow dynamic attributes - " + key, ctx));
					}
				}

				// Auto type -> also for dynamic attributes on an instance
				if (assignables != null && assignables.length == 1) {
					currentInstance.set(key, assignables[0]);
				} else {
					currentInstance.set(key, assignables);
				}
			}
		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void exitAttributeAssignment(AttributeAssignmentContext ctx)
	{
		try {
			DLInstance attributeInstance = currentAttributeAssignmentInstances.pop();

			/*if (attributeAssignableContextQueue.size() > 0) {
				log.debug("exitAttributeAssignment " + attributeInstance.getName() + " " + ctx.attributeSymbol().getText() + " " + attributeAssignableContextQueue.peek().attributeSymbol().getText());
			}*/
			if (!attributeAssignableContextQueue.isEmpty() && attributeAssignableContextQueue.peek() == ctx) {

				//log.debug("exitAttributeAssignment {} {} {}", lastInstance.getType(), lastInstance.getType().getName(), lastInstance);
				attributeAssignableContextQueue.poll();
				String attributeAssignableKey = attributeAssignableKeyQueue.poll();

				//log.debug("exitAttributeAssignment assign " + attributeInstance.getName() + " " + attributeAssignableKey + " " + lastInstance.getName());
				attributeInstance.set(attributeAssignableKey, lastInstance);

				AttributeAssignableContext assignable = ctx.attributeAssignable().get(0);

				if (!lastInstance.getType().isDerivedTypeOf(attributeInstance.getType().getAttribute(attributeAssignableKey).orElseThrow().getType())) {
					throw new InvalidType(createErrorMessage(module, "Type of instance assignment " + assignable.getText() + " is not matching it is " + lastInstance.getType().getCanonicalName() + " but should be " + currentInstance.getType().getAttribute(attributeAssignableKey).orElseThrow().getType().getCanonicalName(), ctx));
				}
			}
		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static DLModule parse(DLCore core, String moduleId, String data) throws DLException
	{
		DLModule module = core.createModule(moduleId);

		DLHrfParsing parsing = new DLHrfParsing(core, module);

		// Setup lexer
		DLLexer lexer = new DLLexer(CharStreams.fromString(data));
		lexer.removeErrorListeners();
		lexer.addErrorListener(new DLHrfParsingErrorHandler(parsing, module));

		TokenStream tokens = new CommonTokenStream(lexer);

		/*
		// @test Iterate tokens from lexer
		while (true) {
			Token token = tokens.LT(1);
			System.out.println("TOKEN: " + token);
			if (token.getType() == DLLexer.EOF) {
				break;
			}
			tokens.consume();
		}
		tokens.seek(0);
		 */
		// Setup parser
		DLParser parser = new DLParser(tokens);
		parser.removeErrorListeners();
		parser.addErrorListener(new DLHrfParsingErrorHandler(parsing, module));

		// Parse module
		try {
			DataContext root = parser.data();
			ParseTreeWalker walker = new ParseTreeWalker();

			walker.walk(parsing, root);
		} catch (ReservedKeyword ex) {
			StringBuilder message = new StringBuilder();
			message
				.append("Keyword '")
				.append(ex.getKeyword())
				.append("' is reserved")
				.append(FilesHelper.createMavenNetbeansFileConsoleLink("\t ",
					module.getShortName(), module.getName(),
					ex.getLine(), ex.getPosition() + 1, false));
			throw new ReservedKeyword(message.toString(), ex);

		} catch (RuntimeException ex) {

			// Unwrap DLException for nicer stack trace
			if (ex.getCause() instanceof DLException) {
				throw (DLException) ex.getCause();
			}

			// Otherwise just forward ex
			throw ex;
		}

		return module;
	}
}
