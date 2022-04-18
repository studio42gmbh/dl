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
import de.s42.dl.exceptions.UndefinedAnnotation;
import de.s42.dl.exceptions.UndefinedType;
import de.s42.dl.exceptions.InvalidEnumValue;
import de.s42.dl.exceptions.InvalidType;
import de.s42.dl.exceptions.InvalidValue;
import de.s42.dl.exceptions.InvalidAttribute;
import de.s42.dl.exceptions.InvalidModule;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.*;
import de.s42.dl.attributes.DefaultDLAttribute;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidCore;
import de.s42.dl.exceptions.InvalidPragma;
import de.s42.dl.instances.DefaultDLInstance;
import de.s42.dl.instances.DefaultDLModule;
import de.s42.dl.parser.DLParser.AttributeAssignableContext;
import de.s42.dl.parser.DLParser.AttributeAssignmentContext;
import de.s42.dl.parser.DLParser.IdentifierContext;
import de.s42.dl.parser.DLParser.TypeAttributeDefinitionContext;
import de.s42.dl.parser.DLParser.TypeDefinitionContext;
import de.s42.dl.types.ArrayDLType;
import de.s42.dl.types.DefaultDLEnum;
import de.s42.dl.types.DefaultDLType;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.util.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 *
 * @author Benjamin Schiller
 */
public class DLHrfParsing extends DLParserBaseListener
{

	private final static Logger log = LogManager.getLogger(DLHrfParsing.class.getName());

	private final Deque<DLInstance> instances = new ArrayDeque<>();
	private DLInstance currentInstance;
	private DLInstance lastInstance;
	private final DefaultDLModule module;
	private DefaultDLType currentType;
	private DefaultDLEnum currentEnum;
	private final DLCore core;

	// @todo DL this is a preliminary prototypal implementation of dlinstance attribute assignments!
	private final Deque<AttributeAssignmentContext> attributeAssignableContextQueue = new ArrayDeque<>();
	private final Deque<String> attributeAssignableKeyQueue = new ArrayDeque<>();
	private final Deque<DLInstance> currentAttributeAssignmentInstances = new ArrayDeque<>();

	protected DefaultDLAttribute dlInstanceAssignAttribute;

	public DLHrfParsing(DLCore core, DefaultDLModule module)
	{
		this.core = core;
		this.module = module;
		currentInstance = module;
		instances.push(currentInstance);
	}

	protected String createErrorMessage(String reason, ParserRuleContext context) throws RuntimeException
	{
		// https://github.com/apache/netbeans/blob/c084119009d2e0f736f225d706bc1827af283501/java/maven/src/org/netbeans/modules/maven/output/GlobalOutputProcessor.java
		//"Das sollte gehen @ GPClient, C:\\home\\f12\\development\\gods_playground\\gp-client\\data\\renderdoc.config.dl, line 5, column 6");

		StringBuilder message = new StringBuilder();
		message
			.append(reason)
			.append("\n")
			.append(FilesHelper.createMavenNetbeansFileConsoleLink("\t ",
				module.getShortName(), module.getName(),
				context.start.getLine(), context.start.getCharPositionInLine() + 1, false));

		return message.toString();
	}

	protected String createErrorMessage(String reason, Throwable cause, ParserRuleContext context) throws RuntimeException
	{
		StringBuilder message = new StringBuilder();

		message
			.append(reason);
		if (cause.getMessage() != null) {
			message
				.append(" - ")
				// @improvement DL why is the exception throwing if i leave " in there?
				.append(cause.getMessage().replace("\"", ""));
		}

		message
			.append("\n")
			.append(FilesHelper.createMavenNetbeansFileConsoleLink("\t ",
				module.getShortName(), module.getName(),
				context.start.getLine(), context.start.getCharPositionInLine() + 1, false));

		return message.toString();
	}

	protected Object resolveReference(String refId, ParserRuleContext context) throws InvalidValue
	{
		Object ref = module.resolveReference(core, refId);

		if (ref == null) {
			throw new InvalidValue(createErrorMessage("Reference $" + refId + " is not defined in module", context));
		}

		return ref;
	}

	protected static class SynonymableIdentifier
	{

		public String identifier;
		public String[] aliases;
	}

	protected DLType fetchTypeIdentifier(DLParser.TypeIdentifierContext ctx) throws InvalidType
	{
		if (ctx == null) {
			return null;
		}

		String typeName = ctx.identifier().getText();

		if (!core.hasType(typeName)) {
			throw new InvalidType(createErrorMessage("Type '" + typeName + "' is not defined", ctx));
		}

		return core.getType(typeName).get();
	}

	protected Object[] fetchAssignables(AttributeAssignmentContext ctx) throws InvalidValue
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
				assignables[i] = Long.parseLong(assignable.INTEGER_LITERAL().getText());
			} else if (assignable.FLOAT_LITERAL() != null) {
				assignables[i] = Double.parseDouble(assignable.FLOAT_LITERAL().getText());
			} else if (assignable.BOOLEAN_LITERAL() != null) {
				if ("true".equals(assignable.BOOLEAN_LITERAL().getText())) {
					assignables[i] = true;
				} else {
					assignables[i] = false;
				}
			} else if (assignable.REF() != null) {
				assignables[i] = resolveReference(assignable.getText(), ctx);
			}

			i++;
		}

		return assignables;
	}

	protected Object[] fetchStaticParameters(DLParser.StaticParametersContext ctx) throws InvalidValue
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

	protected Object fetchStaticParameter(DLParser.StaticParameterContext ctx) throws InvalidValue
	{
		if (ctx.STRING_LITERAL() != null) {
			return ctx.getText();
		} else if (ctx.SYMBOL() != null) {
			return ctx.getText();
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

		throw new InvalidValue(createErrorMessage("Unknown parameter type", ctx));
	}

	@Override
	public void enterPragma(DLParser.PragmaContext ctx)
	{
		try {
			String pragmaIdentifier = ctx.pragmaName().getText();

			Object[] parameters = fetchStaticParameters(ctx.staticParameters());

			core.doPragma(pragmaIdentifier, parameters);
		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void enterAlias(DLParser.AliasContext ctx)
	{
		try {
			String aliasRedefinitionName = ctx.aliasRedefinition().getText();
			String aliasDefinitionName = ctx.aliasDefinition().getText();

			// alias for types
			if (core.hasType(aliasDefinitionName)) {
				
				if (!core.isAllowDefineTypes()) {
					throw new InvalidCore(createErrorMessage("May not define types in core", ctx));
				}

				// alias redefinition type may not be defined already
				if (core.hasType(aliasRedefinitionName)) {
					throw new InvalidType(createErrorMessage("Error alias redef type '" + aliasRedefinitionName + "' is already defined", ctx));
				}

				core.defineAliasForType(aliasRedefinitionName, core.getType(aliasDefinitionName).orElseThrow());
			} // alias for annotations
			else if (core.hasAnnotation(aliasDefinitionName)) {

				if (!core.isAllowDefineAnnotations()) {
					throw new InvalidCore(createErrorMessage("May not define annotations in core", ctx));
				}

				// alias redefinition type may not be defined already
				if (core.hasAnnotation(aliasRedefinitionName)) {
					throw new InvalidAnnotation(createErrorMessage("Error alias redef annotation '" + aliasRedefinitionName + "' is already defined", ctx));
				}

				core.defineAliasForAnnotation(aliasRedefinitionName, core.getAnnotation(aliasDefinitionName).orElseThrow());
			} // alias for annotations
			else if (core.hasPragma(aliasDefinitionName)) {

				if (!core.isAllowDefinePragmas()) {
					throw new InvalidCore(createErrorMessage("May not define pragmas in core", ctx));
				}

				// alias redefinition type may not be defined already
				if (core.hasPragma(aliasRedefinitionName)) {
					throw new InvalidPragma(createErrorMessage("Error alias redef pragma '" + aliasRedefinitionName + "' is already defined", ctx));
				}

				core.defineAliasForPragma(aliasRedefinitionName, core.getPragma(aliasDefinitionName).orElseThrow());
			} // neither type, annotation or pragma definition found
			else {
				throw new InvalidCore(createErrorMessage("Error alias def type, annotation or pragma '" + aliasDefinitionName + "' is not defined", ctx));
			}

		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void enterRequire(DLParser.RequireContext ctx)
	{
		try {

			if (!core.isAllowRequire()) {
				throw new InvalidCore("Not allowed to require in core");
			}

			try {
				DLModule requiredModule = core.parse(ctx.requireModule().getText());
				module.addRequiredModule(requiredModule);
			} catch (Throwable ex) {
				throw new InvalidModule(createErrorMessage("Error requiring module '" + ctx.requireModule().getText() + "'", ex, ctx.requireModule()), ex);
			}
		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void enterEnumDefinition(DLParser.EnumDefinitionContext ctx)
	{
		try {
			if (ctx.KEYWORD_EXTERN() != null) {

				String enumName = ctx.enumName().getText();

				if (core.hasEnum(enumName)) {
					throw new InvalidType(createErrorMessage("Extern enum '" + enumName + "' is already defined", ctx));
				}

				try {

					DLEnum enumImpl = core.createEnum((Class<Enum>) Class.forName(enumName));

					// map the enum as defined
					core.defineType(enumImpl);

					// define alias for the given annotationName
					if (!enumImpl.getName().equals(enumName)) {
						core.defineAliasForType(enumName, enumImpl);
					}
				} catch (ClassNotFoundException ex) {
					throw new InvalidType(createErrorMessage("Class not found for enum '" + enumName + "' - " + ex.getMessage(), ctx), ex);
				}
			} //define a enum
			else {

				currentEnum = (DefaultDLEnum) core.createEnum();
				currentEnum.setName(ctx.enumName().getText());
				core.defineType(currentEnum);

				//map annotations
				for (DLParser.AnnotationContext aCtx : ctx.annotation()) {

					String annotationTypeName = aCtx.annotationName().getText();

					if (!core.hasAnnotation(annotationTypeName)) {
						throw new UndefinedAnnotation(createErrorMessage("Annotation '" + annotationTypeName + "' is not defined", aCtx.annotationName()));
					}

					Object[] parameters = fetchStaticParameters(aCtx.staticParameters());

					DLAnnotation annotation = core.getAnnotation(annotationTypeName).get();
					annotation.bindToEnum(core, currentEnum, parameters);
					currentEnum.addAnnotation(annotation, parameters);
				}

				//map values
				for (DLParser.EnumValueDefinitionContext aCtx : ctx.enumBody().enumValueDefinition()) {

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
	public void enterInstanceDefinition(DLParser.InstanceDefinitionContext ctx)
	{
		try {
			DLType type = null;

			if (ctx.instanceType() != null) {
				type = fetchTypeIdentifier(ctx.instanceType().typeIdentifier());
			} else {
				throw new InvalidInstance("Missing type identifier");
			}

			if (type.isAbstract()) {
				throw new InvalidInstance(createErrorMessage("Error creating instance type " + type.getCanonicalName() + " is abstract", ctx.instanceType().typeIdentifier()));
			}

			String identifier = null;

			if (ctx.instanceName() != null) {
				identifier = ctx.instanceName().getText();
			}

			DefaultDLInstance instance = (DefaultDLInstance) core.createInstance(type, identifier);

			//map annotations
			for (DLParser.AnnotationContext aCtx : ctx.annotation()) {

				String annotationTypeName = aCtx.annotationName().getText();

				if (!core.hasAnnotation(annotationTypeName)) {
					throw new UndefinedAnnotation(createErrorMessage("Annotation '" + annotationTypeName + "' is not defined", ctx));
				}

				DLAnnotation annotation = core.getAnnotation(annotationTypeName).get();

				Object[] parameters = fetchStaticParameters(aCtx.staticParameters());

				try {
					annotation.bindToInstance(core, module, instance, parameters);
					instance.addAnnotation(annotation, parameters);
				} catch (DLException ex) {
					throw new InvalidAnnotation(
						createErrorMessage(
							"Error binding annotation '"
							+ annotation.getName()
							+ "' to type '"
							+ currentType.getName() + "'", ex, aCtx.annotationName()), ex);
				}
			}

			//core.getLog().debug("Created instance", name, "in module", module.getName());
			try {
				// make sure not to add sub instances which are assigned in attribute assignments
				if (currentAttributeAssignmentInstances.peek() != currentInstance) {

					//log.debug("enterInstanceDefinition addChild " + currentInstance.getName() + " "  + instance.getName());
					currentInstance.addChild(instance);
				}
			} catch (DLException | RuntimeException ex) {
				throw new InvalidInstance(createErrorMessage("Error adding child", ex, ctx), ex);
			}

			instances.push(currentInstance);
			currentInstance = instance;
		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void exitInstanceDefinition(DLParser.InstanceDefinitionContext ctx)
	{
		try {
			try {
				// @todo DL support multi nested instances - currently just 1 stack is allowed
				if (currentInstance.getType() != null) {

					//log.debug("currentInstance validate " + currentInstance.getName());
					currentInstance.validate();
				}
			} catch (InvalidInstance ex) {
				throw new InvalidInstance(createErrorMessage("Error validating instance", ex, ctx), ex);
			}

			lastInstance = currentInstance;
			currentInstance = instances.pop();
		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void enterAnnotationDefinition(DLParser.AnnotationDefinitionContext ctx)
	{
		try {

			if (!core.isAllowDefineAnnotations()) {
				throw new InvalidCore("Not allowed to define annotations in core");
			}

			//keyword extern found
			if (ctx.KEYWORD_EXTERN() != null) {

				String annotationName = ctx.annotationDefinitionName().getText();

				if (core.hasAnnotation(annotationName)) {
					throw new InvalidAnnotation(createErrorMessage("Extern annotation '" + annotationName + "' is already defined", ctx));
				}

				try {

					DLAnnotation annotation = core.createAnnotation((Class<DLAnnotation>) Class.forName(annotationName));

					// map the annotation as defined
					core.defineAnnotation(annotation);

					// define alias for the given annotationName
					if (!annotation.getName().equals(annotationName)) {
						core.defineAliasForAnnotation(annotationName, annotation);
					}
				} catch (ClassNotFoundException ex) {
					throw new InvalidAnnotation(createErrorMessage("Class not found for annotation '" + annotationName + "' - " + ex.getMessage(), ctx), ex);
				}

			} else {
				throw new InvalidAnnotation(createErrorMessage("Annotations can not be defined internally", ctx));
			}

			// @improvement define annotations internal - something like combining othres with boolean like contracts?
		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void enterTypeDefinition(DLParser.TypeDefinitionContext ctx)
	{
		try {

			if (!core.isAllowDefineTypes()) {
				throw new InvalidCore(createErrorMessage("Not allowed to define types in core", ctx));
			}

			String typeName = ctx.typeDefinitionName().getText();

			if (ctx.KEYWORD_EXTERN() != null) {

				// dont allow external on types that are already present
				if (core.hasType(typeName)) {
					throw new InvalidType(createErrorMessage("Type '" + typeName + "' is already defined", ctx));
				}

				// define type from extern definition
				try {
					currentType = (DefaultDLType) core.createType(Class.forName(typeName));

					// map the new type
					core.defineType(currentType);

					// define alias for the given typeName
					if (!currentType.getName().equals(typeName)) {
						core.defineAliasForType(typeName, currentType);
					}

				} catch (ClassNotFoundException ex) {
					throw new InvalidType(createErrorMessage("Class not found for type '" + typeName + "' - " + ex.getMessage(), ctx), ex);
				}

				// abstract is not allowed for extern types
				if (ctx.KEYWORD_ABSTRACT() != null) {
					throw new InvalidType(createErrorMessage("Extern type '" + typeName + "' may not have abstract", ctx));
				}

				// extension is not allowed for extern types
				if (ctx.KEYWORD_EXTENDS() != null) {
					throw new InvalidType(createErrorMessage("Extern type '" + typeName + "' may not extend other types", ctx));
				}

				// containment is not allowed for extern types
				if (ctx.KEYWORD_CONTAINS() != null) {
					throw new InvalidType(createErrorMessage("Extern type '" + typeName + "' may not contain other types", ctx));
				}

				// annotations are not allowed on extern types
				if (ctx.annotation().size() > 0) {
					throw new InvalidAnnotation(createErrorMessage("Extern type '" + typeName + "' may not have annotations", ctx));
				}

				// annotations are not allowed on extern types
				if (ctx.typeBody() != null) {
					throw new InvalidType(createErrorMessage("Extern type '" + typeName + "' may not have a body", ctx));
				}

			} //define a type
			else {

				if (core.hasType(typeName)) {
					throw new InvalidType(createErrorMessage("Type '" + typeName + "' is already defined", ctx));
				}

				currentType = (DefaultDLType) core.createType(typeName);
				core.defineType(currentType);

				//make type abstract
				if (ctx.KEYWORD_ABSTRACT() != null) {
					currentType.setAbstract(true);
				} //make type final
				else if (ctx.KEYWORD_FINAL() != null) {
					currentType.setFinal(true);
				}

				//map annotations
				for (DLParser.AnnotationContext aCtx : ctx.annotation()) {

					String annotationTypeName = aCtx.annotationName().getText();

					if (!core.hasAnnotation(annotationTypeName)) {
						throw new UndefinedAnnotation(createErrorMessage("Annotation '" + annotationTypeName + "' is not defined", ctx));
					}

					DLAnnotation annotation = core.getAnnotation(annotationTypeName).get();

					Object[] parameters = fetchStaticParameters(aCtx.staticParameters());

					try {
						annotation.bindToType(core, currentType, parameters);
						currentType.addAnnotation(annotation, parameters);
					} catch (DLException ex) {
						throw new InvalidAnnotation(createErrorMessage("Error binding annotation '" + annotation.getName() + "' to type '" + currentType.getName() + "'", ex, aCtx), ex);
					}
				}

				//extends - set parents
				if (ctx.parentTypeName() != null) {

					for (DLParser.ParentTypeNameContext pCtx : ctx.parentTypeName()) {

						String parentTypeName = pCtx.getText();

						if (!core.hasType(parentTypeName)) {
							throw new UndefinedType(createErrorMessage("Parent type '" + parentTypeName + "' is not defined", pCtx));
						}

						DLType parentType = core.getType(parentTypeName).get();

						if (parentType.isFinal()) {
							throw new InvalidType(createErrorMessage("Parent type " + parentType.getCanonicalName() + " is final and can not be derived from in " + currentType.getCanonicalName(), pCtx));
						}

						currentType.addParent(parentType);
					}
				}

				//contains - set contained types
				if (ctx.containsTypeName() != null) {

					for (DLParser.ContainsTypeNameContext pCtx : ctx.containsTypeName()) {

						String containsTypeName = pCtx.getText();

						if (!core.hasType(containsTypeName)) {
							throw new UndefinedType(createErrorMessage("Contains type '" + containsTypeName + "' is not defined", pCtx));
						}

						currentType.addContainedType(core.getType(containsTypeName).get());
					}
				}
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

	@Override
	public void enterTypeAttributeDefinition(DLParser.TypeAttributeDefinitionContext ctx)
	{
		try {
			String typeName = ctx.typeAttributeDefinitionType().typeIdentifier().getText();

			if (!core.hasType(typeName)) {
				throw new UndefinedType(createErrorMessage("Attribute type '" + typeName + "' is not defined", ctx));
			}

			List<DLType> genericTypes = new ArrayList<>();

			// parse generic types
			if (ctx.typeAttributeDefinitionType().typeAttributeDefinitionGeneric() != null) {

				for (IdentifierContext identifier : ctx.typeAttributeDefinitionType().typeAttributeDefinitionGeneric().identifier()) {

					String genericTypeName = identifier.getText();

					if (!core.hasType(genericTypeName)) {
						throw new UndefinedType(createErrorMessage("Attribute generic type '" + genericTypeName + "' is not defined", ctx.typeAttributeDefinitionType().typeAttributeDefinitionGeneric()));
					}

					genericTypes.add(core.getType(genericTypeName).get());
				}
			}

			DLType type;

			try {
				type = core.getType(typeName, genericTypes).get();
			} catch (InvalidType ex) {
				throw new InvalidType(createErrorMessage("Error retrieving type '" + typeName + "'", ex, ctx.typeAttributeDefinitionType().typeAttributeDefinitionGeneric()), ex);
			}

			String name = ctx.typeAttributeDefinitionName().getText();

			DefaultDLAttribute attribute = (DefaultDLAttribute) core.createAttribute(name, type);

			// parse default value
			Object defaultValue = null;
			if (ctx.typeAttributeDefinitionDefault() != null) {

				//DLInstance localCurrentInstance = currentInstance;
				if (ctx.typeAttributeDefinitionDefault().instanceDefinition() != null) {
					dlInstanceAssignAttribute = attribute;
				} //resolve and validate type of reference
				else if (ctx.typeAttributeDefinitionDefault().REF() != null) {

					Object ref = resolveReference(ctx.typeAttributeDefinitionDefault().getText(), ctx.typeAttributeDefinitionDefault());

					if (ref instanceof DLInstance) {
						DLType refType = ((DLInstance) ref).getType();

						if (type != null && refType == null) {
							throw new InvalidType(createErrorMessage(
								"Type of reference $" + ctx.typeAttributeDefinitionDefault().getText()
								+ " is not matching it should be " + type.getName(), ctx.typeAttributeDefinitionDefault()));
						}

						if (type != null && !type.isAssignableFrom(refType)) {
							throw new InvalidType(createErrorMessage(
								"Type of reference $" + ctx.typeAttributeDefinitionDefault().getText()
								+ " is not matching it is " + refType.getName()
								+ " but should be " + type.getName(), ctx.typeAttributeDefinitionDefault()));
						}
					}

					defaultValue = ref;
				} else {
					try {
						defaultValue = type.read(ctx.typeAttributeDefinitionDefault().getText());
					} catch (InvalidEnumValue ex) {
						throw new InvalidEnumValue(createErrorMessage("Error reading default value for attribute '"
							+ name + "'", ex, ctx.typeAttributeDefinitionDefault()), ex);
					} catch (AssertionError | Exception ex) {
						throw new InvalidValue(createErrorMessage("Error reading default value for attribute '"
							+ name + "'", ex, ctx.typeAttributeDefinitionDefault()), ex);
					}
				}
			}
			attribute.setDefaultValue(defaultValue);

			//map annotations
			for (DLParser.AnnotationContext aCtx : ctx.annotation()) {

				String annotationTypeName = aCtx.annotationName().getText();

				Object[] parameters = fetchStaticParameters(aCtx.staticParameters());

				try {
					core.addAnnotationToAttribute(currentType, attribute, annotationTypeName, parameters);
				} catch (DLException ex) {
					throw new InvalidAnnotation(createErrorMessage("Error binding annotation '"
						+ annotationTypeName + "' to attribute '" + attribute.getName() + "'", ex, aCtx.annotationName()), ex);
				}
			}

			currentType.addAttribute(attribute);
		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void exitTypeDefinition(TypeDefinitionContext ctx)
	{
		try {
			//validate all attributes
			for (DLAttribute attribute : currentType.getAttributes()) {

				try {
					attribute.validate();
				} catch (InvalidAttribute ex) {
					throw new InvalidAnnotation(createErrorMessage("Error validating attribute '" + attribute.getName() + "'", ex, ctx), ex);
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
			if (attributeAssignableContextQueue.size() > 0 && attributeAssignableContextQueue.peek() == ctx) {

				//log.debug("exitAttributeAssignment {} {} {}", lastInstance.getType(), lastInstance.getType().getName(), lastInstance);
				attributeAssignableContextQueue.poll();
				String attributeAssignableKey = attributeAssignableKeyQueue.poll();

				//log.debug("exitAttributeAssignment assign " + attributeInstance.getName() + " " + attributeAssignableKey + " " + lastInstance.getName());
				attributeInstance.set(attributeAssignableKey, lastInstance);

				AttributeAssignableContext assignable = ctx.attributeAssignable().get(0);

				if (!lastInstance.getType().isDerivedTypeOf(attributeInstance.getType().getAttribute(attributeAssignableKey).orElseThrow().getType())) {
					throw new InvalidType(createErrorMessage("Type of instance assignment " + assignable.getText() + " is not matching it is " + lastInstance.getType().getName() + " but should be " + currentInstance.getType().getAttribute(attributeAssignableKey).orElseThrow().getType().getName(), ctx));
				}
			}
		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void enterAttributeAssignment(AttributeAssignmentContext ctx)
	{
		try {
			currentAttributeAssignmentInstances.push(currentInstance);

			DefaultDLType instanceType = (DefaultDLType) currentInstance.getType();

			AttributeAssignableContext assignable = null;

			if (ctx.attributeAssignable().size() > 0) {
				assignable = ctx.attributeAssignable().get(0);
			}

			Object[] assignables = fetchAssignables(ctx);

			//type key -> explicit type or type collision if instance defines it
			if (ctx.attributeType() != null) {
				String typeName = ctx.attributeType().getText();
				String key = ctx.attributeName().getText();

				if (!core.hasType(typeName)) {
					throw new UndefinedType(createErrorMessage("Type '" + typeName + "' is not defined", ctx.attributeType()));
				}

				if (currentInstance.hasAttribute(key)) {
					throw new InvalidAttribute(createErrorMessage("Instance " + currentInstance.getName()
						+ " already has the attribute " + key, ctx.attributeName()));
				}

				DLType type = core.getType(typeName).get();

				//check if type contradicts
				if (instanceType != null) {

					if (instanceType.hasAttribute(key)) {
						if (type != currentInstance.getType().getAttribute(key).orElseThrow().getType()) {
							throw new InvalidType(createErrorMessage("Defined attribute type and given instance type do not match "
								+ type.getClass().getName() + " <> "
								+ currentInstance.getType().getAttribute(key).orElseThrow().getType().getClass().getName(), ctx));
						}
					} else if (!instanceType.isAllowDynamicAttributes()) {
						throw new InvalidType(createErrorMessage("Instance type " + instanceType.getClass().getName() + " does not allow dynamic attributes - " + key, ctx));
					}
				}

				if (assignables != null && assignables.length == 1 && assignable.instanceDefinition() != null) {

					attributeAssignableContextQueue.push(ctx);
					attributeAssignableKeyQueue.push(key);

					//log.debug("enterAttributeAssignment.instanceDefinition2 {} {}", key,  ctx.attributeSymbol().getText());
				} //resolve and validate type of reference
				else if (assignables != null && assignables.length == 1 && assignable.REF() != null) {

					Object ref = resolveReference(assignable.getText(), assignable);

					if (ref instanceof DLInstance) {

						DLType refType = ((DLInstance) ref).getType();

						if (instanceType.hasAttribute(key)) {
							if (!instanceType.getAttribute(key).orElseThrow().getType().isAssignableFrom(refType)) {
								throw new InvalidType(createErrorMessage(
									"Type of reference $" + assignable.getText()
									+ " is not matching it is " + refType.getName()
									+ " but should be " + instanceType.getAttribute(key).orElseThrow().getType().getCanonicalName(), ctx));
							}
						} else if (!instanceType.isAllowDynamicAttributes()) {
							throw new InvalidValue(createErrorMessage("Instance type " + instanceType + " does not allow dynamic attributes - " + key, ctx));
						}
					}

					currentInstance.set(key, ref);
				} else {

					try {
						currentInstance.set(key, type.read(assignables));
					} catch (InvalidType ex2) {
						throw new InvalidType(createErrorMessage("InvalidType", ex2, ctx), ex2);
					} catch (AssertionError | Exception ex) {
						throw new InvalidValue(createErrorMessage("Error reading value", ex, ctx), ex);
					}
				}
			} //just key -> infer from instance or auto type (string, float, int, boolean)
			else {

				String key = ctx.attributeName().getText();

				if (currentInstance.hasAttribute(key)) {
					throw new InvalidAttribute(createErrorMessage("Instance " + currentInstance.getName()
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
							&& // @todo DL this way of preventing arrays to be matched has to be optimized
							!(currentInstance.getType().getAttribute(key).orElseThrow().getType() instanceof ArrayDLType)) {

							Object ref = resolveReference(assignable.getText(), assignable);

							if (ref instanceof DLInstance) {

								DLType refType = ((DLInstance) ref).getType();

								if (!refType.isDerivedTypeOf(currentInstance.getType().getAttribute(key).orElseThrow().getType())) {
									throw new InvalidType(createErrorMessage("Type of reference $" + assignable.getText() + " is not matching it is " + refType.getName() + " but should be " + currentInstance.getType().getAttribute(key).orElseThrow().getType().getName(), ctx));
								}
							}

							currentInstance.set(key, ref);
						} else {
							try {
								//currentInstance.set(key, currentInstance.getType().getAttribute(key).getType().read(assignable.getText()));
								currentInstance.set(key, currentInstance.getType().getAttribute(key).orElseThrow().getType().read(assignables));

							} catch (InvalidType ex2) {
								throw new InvalidType(createErrorMessage("InvalidType", ex2, ctx), ex2);
							} catch (AssertionError | Exception ex) {
								throw new InvalidValue(createErrorMessage("Instance value " + key + " could not be set", ex, assignable), ex);
							}
						}

						return;
					} else if (!instanceType.isAllowDynamicAttributes()) {
						throw new InvalidValue(createErrorMessage("Instance type " + instanceType.getName() + " does not allow dynamic attributes - " + key, ctx));
					}
				}

				//auto type -> also for dynamic attributes on an instance
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
	public void exitTypeIdentifier(DLParser.TypeIdentifierContext ctx)
	{
		try {
			if (!core.hasType(ctx.getText())) {
				throw new InvalidType(createErrorMessage("Type " + ctx.getText() + " is not defined", ctx));
			}
		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static DLModule parse(DLCore core, String moduleId, String data) throws DLException
	{
		DefaultDLModule module = (DefaultDLModule) core.createModule(moduleId);

		DLHrfParsing parsing = new DLHrfParsing(core, module);

		//setup lexer
		DLLexer lexer = new DLLexer(CharStreams.fromString(data));
		lexer.removeErrorListeners();
		lexer.addErrorListener(new DLHrfParsingErrorHandler(parsing, module));
		TokenStream tokens = new CommonTokenStream(lexer);

		//iterate tokens from lexer
		/*while (true) {
			Token token = tokens.LT(1);
			System.out.println("TOKEN: " + token);
			if (token.getType() == DLLexer.EOF) {
				break;
			}
			tokens.consume();
		}
		tokens.seek(0);
		 */
		//setup parser
		DLParser parser = new DLParser(tokens);
		parser.removeErrorListeners();
		parser.addErrorListener(new DLHrfParsingErrorHandler(parsing, module));

		//parse
		DLParser.DataContext root = parser.data();
		ParseTreeWalker walker = new ParseTreeWalker();

		try {
			walker.walk(parsing, root);
		} catch (RuntimeException ex) {
			if (ex.getCause() instanceof DLException) {
				throw (DLException) ex.getCause();
			}
			throw ex;
		}

		return module;
	}
}
