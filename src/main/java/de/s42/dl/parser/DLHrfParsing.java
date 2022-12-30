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

	/**
	 * Defines the minimal initial capacity size of assignable lists (aligned to ArrayList.DEFAULT_CAPACITY) - should
	 * allow java to reuse the lists better
	 */
	public final static int MIN_ASSIGNABLE_CAPACITY = 10;

	private final Deque<DLInstance> instances = new ArrayDeque<>();
	private final DLCore core;
	private final DLModule module;

	private DLInstance currentInstance;
	private DLInstance lastInstance;
	private DefaultDLType currentType;
	private DefaultDLEnum currentEnum;

	private final Deque<DLInstance> currentAttributeAssignmentInstances = new ArrayDeque<>();
	protected DefaultDLAttribute dlInstanceAssignAttribute;
	private final Deque<List> currentAttributeAssignmentList = new ArrayDeque<>();

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
		return DLHrfExpressionParser.resolveReference(core, module, refId, context);
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

		return DLHrfExpressionParser.resolveExpression(core, module, ctx.expression());
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
	public void enterAssert(AssertContext ctx)
	{
		assert ctx != null;

		try {
			if (!core.isAllowUseAsserts()) {
				throw new InvalidCore(createErrorMessage(module, "May not assert in core", ctx));
			}

			// Resolve the given test expression
			Object result = DLHrfExpressionParser.resolveExpression(core, module, ctx.assertTest().expression());

			// If it did not returned a boolean true -> Throw an exception
			if (!(result instanceof Boolean)
				|| !((Boolean) result)) {

				String message;

				if (ctx.assertMessage() != null) {
					message = "" + DLHrfExpressionParser.resolveExpression(core, module, ctx.assertMessage().expression());
				} else {
					message = "Assert '" + ctx.assertTest().getText() + "' is not true";
				}

				throw new DLHrfParsingException(message, module, ctx);
			}
		} catch (RuntimeException | DLException ex) {
			throw new DLHrfParsingException(
				"Error asserting - " + ex.getMessage(),
				module,
				ctx,
				ex
			);
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
			throw new DLHrfParsingException(
				"Error defining pragma - " + ex.getMessage(),
				module,
				ctx,
				ex
			);
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

			// Keyword extern found optionally defined the external annotation
			if (ctx.KEYWORD_EXTERN() != null) {

				String annotationName = ctx.annotationDefinitionName().getText();

				try {

					// Allow multiple extren declarations
					if (!core.hasAnnotationFactory(annotationName)) {

						// Assume the annotation factory is the given name
						DLAnnotationFactory annotationFactory = ((Class<DLAnnotationFactory>) Class.forName(annotationName)).getConstructor().newInstance();

						// map the annotation as defined
						core.defineAnnotationFactory(annotationFactory, annotationName);

						// Define aliases from type definition
						if (ctx.aliases() != null) {
							for (AliasNameContext aliasCtx : ctx.aliases().aliasName()) {
								core.defineAliasForAnnotationFactory(aliasCtx.identifier().getText(), annotationName);
							}
						}
					}

				} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
					throw new InvalidAnnotation(
						createErrorMessage(
							module,
							"Class not found for annotation '" + annotationName + "' - " + ex.getMessage(),
							ctx),
						ex);
				}

			} else {
				throw new InvalidAnnotation(
					createErrorMessage(
						module,
						"Annotations can not be defined internally yet",
						ctx));
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
				} else {
					currentType = null;
				}
			} // Define an extern type
			else if (ctx.KEYWORD_EXTERN() != null) {

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

				// Allows to have multiple statements of external declaration
				if (!core.hasType(typeName)) {

					// Define type from extern definition
					try {
						currentType = (DefaultDLType) core.createType(Class.forName(typeName));

						// map the new type
						core.defineType(currentType);

						// define alias for the given typeName to also map its java class name
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
				} else {
					currentType = null;
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
						throw new InvalidAnnotation(
							createErrorMessage(
								module,
								"Error binding annotation '" + annotationName + "' to type '" + currentType.getName() + "'",
								ex,
								aCtx),
							ex);
					}
				});

				// Extends - set parents
				if (ctx.parentTypeName() != null) {

					for (ParentTypeNameContext pCtx : ctx.parentTypeName()) {

						String parentTypeName = pCtx.getText();

						DLType parentType = core.getType(parentTypeName).orElseThrow(() -> {
							return new UndefinedType(
								createErrorMessage(
									module,
									"Parent type '" + parentTypeName + "' is not defined",
									pCtx)
							);
						});

						if (parentType.isFinal()) {
							throw new InvalidType(
								createErrorMessage(
									module,
									"Parent type " + parentType.getCanonicalName() + " is final and can not be derived from in " + currentType.getCanonicalName(),
									pCtx)
							);
						}

						currentType.addParent(parentType);
					}
				}

				// Contains - set contained types
				if (ctx.containsTypeName() != null) {

					for (ContainsTypeNameContext pCtx : ctx.containsTypeName()) {

						String containsTypeName = pCtx.getText();

						DLType containsType = core.getType(containsTypeName).orElseThrow(() -> {
							return new UndefinedType(
								createErrorMessage(
									module,
									"Contains type '" + containsTypeName + "' is not defined",
									pCtx)
							);
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
		if (currentType != null) {
			try {
				ValidationResult result = new ValidationResult();
				if (!currentType.validate(result)) {
					throw new InvalidType(
						createErrorMessage(
							module,
							"Type '" + currentType.getCanonicalName() + "' is not valid - " + result.toMessage(),
							ctx)
					);
				}

				module.addDefinedType(currentType);

			} catch (InvalidType ex) {
				throw new RuntimeException(ex);
			}
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

			DLType type = core.getType(typeName, genericTypes).orElseThrow(() -> {
				return new InvalidType(createErrorMessage(module, "Error retrieving type '" + typeName + "'", ctx.typeAttributeDefinitionType().typeIdentifier()));
			});

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
				else if (ctx.typeAttributeDefinitionDefault().expression() != null) {

					defaultValue = DLHrfExpressionParser.resolveExpression(core, module, ctx.typeAttributeDefinitionDefault().expression());

					if (type != null) {

						if (type.isSimpleType() && !type.isAbstract()) {
							defaultValue = type.read(defaultValue);
						}

						if (defaultValue instanceof DLInstance) {
							DLType refType = ((DLInstance) defaultValue).getType();

							if (refType == null) {
								throw new InvalidType(createErrorMessage(module,
									"Type of reference $" + ctx.typeAttributeDefinitionDefault().getText()
									+ " is not matching it should be " + type.getName(), ctx.typeAttributeDefinitionDefault()));
							}

							if (!type.isAssignableFrom(refType)) {
								throw new InvalidType(createErrorMessage(module,
									"Type of reference $" + ctx.typeAttributeDefinitionDefault().getText()
									+ " is not matching it is " + refType.getName()
									+ " but should be " + type.getName(), ctx.typeAttributeDefinitionDefault()));
							}
						}
					}
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

		} catch (RuntimeException | DLException ex) {
			throw new DLHrfParsingException(
				"Error defining attribute - " + ex.getMessage(),
				module,
				ctx,
				ex
			);
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
	public void enterAttributeAssignment(AttributeAssignmentContext ctx)
	{
		currentAttributeAssignmentInstances.push(currentInstance);
		currentAttributeAssignmentList.push(new ArrayList<>(Math.max(MIN_ASSIGNABLE_CAPACITY, ctx.attributeAssignable().size())));
	}

	@Override
	public void exitAttributeAssignable(AttributeAssignableContext ctx)
	{
		List assignables = currentAttributeAssignmentList.peek();

		try {

			// Add the resolved expression
			if (ctx.expression() != null) {
				try {
					assignables.add(DLHrfExpressionParser.resolveExpression(core, module, ctx.expression()));
				} catch (RuntimeException ex) {
					throw new InvalidValue(
						createErrorMessage(
							module,
							"Expression '" + ctx.expression().getText() + "' could not be evaluated",
							ex,
							ctx.expression()
						), ex
					);
				}
			} // Or add the just defined instance
			else if (ctx.instanceDefinition() != null) {
				assignables.add(lastInstance);
			} // Should not happen if the grammar has no regressions
			else {
				throw new DLHrfParsingException(
					"Error assignable is of unknown type",
					module,
					ctx
				);
			}
		} catch (RuntimeException | DLException ex) {
			throw new DLHrfParsingException(
				"Error assigning attribute",
				module,
				ctx,
				ex
			);
		}
	}

	@Override
	public void exitAttributeAssignment(AttributeAssignmentContext ctx)
	{
		// Used for tracking nested attribute instance assignments in instance definition
		currentAttributeAssignmentInstances.pop();

		try {
			// Get attribute name
			String attributeName = ctx.attributeName().getText();

			// Prevent double definitions of same attribute name
			if (currentInstance.hasAttribute(attributeName)) {
				throw new InvalidAttribute(
					createErrorMessage(
						module,
						"Attribute '" + attributeName + "' is already defined in instance " + currentInstance,
						ctx.attributeName()
					)
				);
			}

			// Fetch assignables
			Object[] attributeAssignables = currentAttributeAssignmentList.pop().toArray();

			// Type of the attribute or null if it has no explicit type (dynamic types)
			DLType attributeType = currentInstance
				.getAttribute(attributeName)
				.map((attribute) -> {
					return attribute.getType();
				})
				.orElse(null);

			// Ensure check if instance allows dynamic attributes
			if (!currentInstance.getType().isAllowDynamicAttributes()
				&& attributeType == null) {
				throw new InvalidAttribute(
					createErrorMessage(
						module,
						"Instance '" + currentInstance + "' does not contain attribute '" + attributeName + "' and does not allow dynamic attributes",
						ctx
					));
			}

			// With explicitly given type
			if (ctx.attributeType() != null) {

				// Get the type name of the typed attribute
				String givenAttributeTypeName = ctx.attributeType().getText();

				// Get type of the typed attribute
				DLType givenAttributeType = core.getType(givenAttributeTypeName).orElseThrow(() -> {
					return new InvalidType(
						createErrorMessage(
							module,
							"Type '" + givenAttributeTypeName + "' not contained",
							ctx.attributeType()
						));
				});

				// Make sure the target type is assignable from given type
				if (attributeType != null && !attributeType.isAssignableFrom(givenAttributeType)) {
					throw new InvalidType(
						createErrorMessage(
							module,
							"Type '" + attributeType + "' is not assignable from '" + givenAttributeType + "'",
							ctx.attributeType()
						));
				}

				// Convert the attribute into the given type
				attributeType = givenAttributeType;
			}

			// Assign the attribute value with read if given a valid type
			if (attributeType != null) {

				// If the assignables are exactly one DLInstance -> 
				//Check if this instances types isDerived from attributes types and then assign it
				if (attributeAssignables.length == 1 && (attributeAssignables[0] instanceof DLInstance)) {

					DLInstance instance = (DLInstance) attributeAssignables[0];

					// Special handling for array type to allow single assignments
					if (attributeType instanceof ArrayDLType) {

						ArrayDLType arrayType = (ArrayDLType) attributeType;

						if (!arrayType.isComponenTypeAssignableOf(instance.getType())) {
							throw new InvalidValue(
								createErrorMessage(
									module,
									"Type of array attribute component '" + arrayType.getComponentType().orElse(null) + " is not assignable of '" + instance.getType() + "'",
									ctx));
						}

						currentInstance.set(attributeName, arrayType.read(instance));
					} // Normal single value instance assignments
					else {

						if (!attributeType.isAssignableFrom(instance.getType())) {
							throw new InvalidType(
								createErrorMessage(
									module,
									"Type of attribute '" + attributeType + " is not assignable of '" + instance.getType() + "'",
									ctx));
						}

						currentInstance.set(attributeName, instance);
					}

				} // Otherwise assign the value after converting the assignabled using the types read method
				else if (attributeType.canRead()) {
					try {
						currentInstance.set(attributeName, attributeType.read(attributeAssignables));
					} catch (RuntimeException | DLException ex) {
						throw new InvalidValue(
							createErrorMessage(
								module,
								"Error reading assignables",
								ex,
								ctx),
							ex);
					}
				} else {

					// Try if the given value is of javatype of the dl type
					if (attributeAssignables.length == 1
						&& attributeType.getJavaDataType().isAssignableFrom(attributeAssignables[0].getClass())) {
						currentInstance.set(attributeName, attributeAssignables[0]);
					} // Otherwise throw an error
					else {
						throw new InvalidType(
							createErrorMessage(
								module,
								"Error assigning '" + attributeName + "' its type '" + attributeType + "' can not read directly " + Arrays.toString(attributeAssignables),
								ctx));
					}
				}
			} // Otherwise assign either the unpacked value if it is an array of length 1 or the array if not given a reading type
			else {
				currentInstance.set(
					attributeName,
					(attributeAssignables.length == 1) ? attributeAssignables[0] : attributeAssignables
				);
			}
		} catch (RuntimeException | DLException ex) {
			throw new DLHrfParsingException(
				"Error defining attribute",
				module,
				ctx,
				ex
			);
		}
	}

	/**
	 * This method will parse the given data string as DL HRF return a freshly created module with the given moduleId as
	 * name
	 *
	 * @param core
	 * @param moduleId
	 * @param data
	 *
	 * @return
	 *
	 * @throws DLException
	 */
	public static DLModule parse(DLCore core, String moduleId, String data) throws DLException
	{
		DLModule module = core.createModule(moduleId);

		DLHrfParsing parsing = new DLHrfParsing(core, module);

		// Setup lexer
		DLLexer lexer = new DLLexer(CharStreams.fromString(data));
		lexer.removeErrorListeners();
		lexer.addErrorListener(new DLHrfParsingErrorHandler(parsing, module));

		// Setup parser
		DLParser parser = new DLParser(new CommonTokenStream(lexer));
		parser.removeErrorListeners();
		parser.addErrorListener(new DLHrfParsingErrorHandler(parsing, module));

		// Parse module
		try {
			DataContext root = parser.data();
			ParseTreeWalker walker = new ParseTreeWalker();

			walker.walk(parsing, root);
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
