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
package de.s42.dl.core;

import de.s42.dl.*;
import de.s42.base.beans.BeanHelper;
import de.s42.base.beans.BeanInfo;
import de.s42.base.beans.BeanProperty;
import de.s42.base.beans.InvalidBean;
import de.s42.base.collections.MappedList;
import de.s42.base.conversion.ConversionHelper;
import de.s42.base.files.FilesHelper;
import de.s42.dl.DLAnnotation.AnnotationDL;
import de.s42.dl.DLAnnotation.AnnotationDLContainer;
import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.annotations.*;
import de.s42.dl.attributes.DefaultDLAttribute;
import de.s42.dl.core.resolvers.FileCoreResolver;
import de.s42.dl.core.resolvers.ResourceCoreResolver;
import de.s42.dl.core.resolvers.StringCoreResolver;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.UndefinedType;
import de.s42.dl.exceptions.InvalidPragma;
import de.s42.dl.exceptions.InvalidType;
import de.s42.dl.exceptions.InvalidModule;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.exceptions.InvalidCore;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.exceptions.UndefinedAnnotation;
import de.s42.dl.instances.ComplexTypeDLInstance;
import de.s42.dl.instances.DefaultDLInstance;
import de.s42.dl.instances.DefaultDLModule;
import de.s42.dl.instances.SimpleTypeDLInstance;
import de.s42.dl.java.DLContainer;
import de.s42.dl.types.ArrayDLType;
import de.s42.dl.types.DefaultDLEnum;
import de.s42.dl.types.DefaultDLType;
import de.s42.dl.types.ObjectDLType;
import de.s42.dl.util.*;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author Benjamin Schiller
 */
public class BaseDLCore implements DLCore
{

	private final static Logger log = LogManager.getLogger(BaseDLCore.class.getName());

	protected final Map<String, WeakReference<Object>> convertedCache = new HashMap<>();
	protected final List<DLCoreResolver> resolvers = new ArrayList<>();
	protected final MappedList<String, DLType> types = new MappedList<>();
	protected final MappedList<String, DLPragma> pragmas = new MappedList<>();
	protected final MappedList<String, DLAnnotation> annotations = new MappedList<>();
	protected final Map<String, DLModule> requiredModules = new HashMap<>();
	protected final MappedList<String, DLInstance> exported = new MappedList<>();
	protected Path basePath;
	protected boolean allowDefineTypes;
	protected boolean allowDefineAnnotations;
	protected boolean allowDefinePragmas;
	protected boolean allowRequire;

	public BaseDLCore()
	{
		init();
	}

	private void init()
	{
		addResolver(new FileCoreResolver(this));
		addResolver(new ResourceCoreResolver(this));
		addResolver(new StringCoreResolver(this));
	}

	public <DLCoreType extends DLCore> DLCoreType copy() throws InvalidCore
	{
		try {
			BaseDLCore core = getClass().getConstructor().newInstance();

			core.convertedCache.putAll(convertedCache);
			core.resolvers.addAll(resolvers);
			core.types.addAll(types);
			core.pragmas.addAll(pragmas);
			core.annotations.addAll(annotations);
			core.requiredModules.putAll(requiredModules);
			core.exported.addAll(exported);
			core.basePath = basePath;
			core.allowDefineTypes = allowDefineTypes;
			core.allowDefineAnnotations = allowDefineAnnotations;
			core.allowDefinePragmas = allowDefinePragmas;
			core.allowRequire = allowRequire;

			return (DLCoreType) core;
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
			throw new InvalidCore("Error copying - " + ex.getMessage(), ex);
		}
	}

	@Override
	public DLType createType()
	{
		DLType type = new DefaultDLType();

		return type;
	}

	@Override
	public DLType createType(String typeName)
	{
		DLType type = new DefaultDLType(typeName);

		return type;
	}

	@Override
	public DLEnum createEnum()
	{
		DLEnum type = new DefaultDLEnum();

		return type;
	}

	@Override
	public DLEnum createEnum(String name)
	{
		DLEnum type = new DefaultDLEnum(name);

		return type;
	}

	@Override
	public DLEnum createEnum(String name, Class<? extends Enum> enumImpl)
	{
		DLEnum type = new DefaultDLEnum(name, enumImpl);

		return type;
	}

	@Override
	public DLEnum createEnum(Class<? extends Enum> enumImpl)
	{
		DLEnum type = new DefaultDLEnum(enumImpl);

		return type;
	}

	@Override
	public DLAttribute createAttribute(String attributeName, String typeName) throws DLException
	{
		assert attributeName != null;
		assert typeName != null;

		return createAttribute(attributeName, getType(typeName).get());
	}

	@Override
	public DLAttribute createAttribute(String attributeName, DLType type)
	{
		assert attributeName != null;
		assert type != null;

		return new DefaultDLAttribute(attributeName, type);
	}

	@Override
	public DLAnnotation createAnnotation(Class<? extends DLAnnotation> annotationImpl) throws DLException
	{
		assert annotationImpl != null;

		try {
			DLAnnotation annotation = (DLAnnotation) annotationImpl.getConstructor().newInstance();

			return annotation;
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
			throw new DLException("Can not create annotation " + annotationImpl.getName());
		}
	}

	@Override
	public DLAnnotation addAnnotationToAttribute(DLType type, DLAttribute attribute, String annotationName, Object... parameters) throws DLException
	{
		assert attribute != null;
		assert annotationName != null;

		Optional<DLAnnotation> optAnnotation = getAnnotation(annotationName);

		if (optAnnotation.isEmpty()) {
			throw new UndefinedAnnotation("Annotation '" + annotationName + "' is not defined");
		}

		DLAnnotation annotation = optAnnotation.orElseThrow();

		annotation.bindToAttribute(this, type, attribute, parameters);
		((DefaultDLAttribute) attribute).addAnnotation(annotation, parameters);

		return annotation;
	}

	@Override
	public DLAnnotation addAnnotationToType(DLType type, String annotationName, Object... parameters) throws DLException
	{
		assert type != null;
		assert annotationName != null;
		assert parameters != null;

		Optional<DLAnnotation> optAnnotation = getAnnotation(annotationName);

		if (optAnnotation.isEmpty()) {
			throw new UndefinedAnnotation("Annotation '" + annotationName + "' is not defined");
		}

		DLAnnotation annotation = optAnnotation.orElseThrow();

		annotation.bindToType(this, type, parameters);
		((DefaultDLType) type).addAnnotation(annotation, parameters);

		return annotation;
	}

	@Override
	public DLAnnotation addAnnotationToInstance(DLModule module, DLInstance instance, String annotationName, Object... parameters) throws DLException
	{
		assert module != null;
		assert instance != null;
		assert annotationName != null;
		assert parameters != null;

		Optional<DLAnnotation> optAnnotation = getAnnotation(annotationName);

		if (optAnnotation.isEmpty()) {
			throw new UndefinedAnnotation("Annotation '" + annotationName + "' is not defined");
		}

		DLAnnotation annotation = optAnnotation.orElseThrow();

		annotation.bindToInstance(this, module, instance, parameters);
		((DefaultDLInstance) instance).addAnnotation(annotation, parameters);

		return annotation;
	}

	@Override
	public void doPragma(String pragmaName, Object... parameters) throws InvalidPragma
	{
		assert pragmaName != null;
		assert parameters != null;

		Optional<DLPragma> pragma = getPragma(pragmaName);

		if (pragma.isEmpty()) {
			throw new InvalidPragma("Pragma " + pragmaName + " is not defined");
		}

		pragma.get().doPragma(this, parameters);
	}

	@Override
	public DLType defineAliasForType(String alias, DLType type) throws InvalidCore, InvalidType
	{
		assert alias != null;
		assert type != null;

		if (!allowDefineTypes) {
			throw new InvalidCore("May not define types");
		}

		if (hasType(alias)) {
			throw new InvalidType("Alias '" + alias + "' is already defined");
		}

		types.add(alias, type);

		return type;
	}

	@Override
	public DLAnnotation defineAliasForAnnotation(String alias, DLAnnotation annotation) throws InvalidCore, InvalidAnnotation
	{
		assert alias != null;
		assert annotation != null;

		if (!allowDefineAnnotations) {
			throw new InvalidCore("May not define annotations");
		}

		if (hasAnnotation(alias)) {
			throw new InvalidAnnotation("Annotation '" + alias + "' is already defined");
		}

		annotations.add(alias, annotation);

		return annotation;
	}

	@Override
	public DLPragma defineAliasForPragma(String alias, DLPragma pragma) throws InvalidCore, InvalidPragma
	{
		assert alias != null;
		assert pragma != null;

		if (!allowDefinePragmas) {
			throw new InvalidCore("May not define types");
		}

		if (hasPragma(alias)) {
			throw new InvalidPragma("Alias '" + alias + "' is already defined");
		}

		pragmas.add(alias, pragma);

		return pragma;
	}

	public <DataType> DLInstance addExported(String key, DataType value) throws DLException
	{
		assert key != null;
		assert value != null;

		Optional<DLType> optType = getType(value.getClass());

		if (optType.isEmpty()) {
			throw new UndefinedType("No type mapped for " + value.getClass().getName());
		}

		DLType type = optType.orElseThrow();

		// Map a simple type
		if (type.isSimpleType()) {
			SimpleTypeDLInstance<DataType> dataInstance = new SimpleTypeDLInstance<>(value, type, key);
			dataInstance.validate();
			addExported(dataInstance);
			return dataInstance;
		} // https://github.com/studio42gmbh/dl/issues/29
		// Map a complex type
		else {

			try {
				ComplexTypeDLInstance<DataType> instance = new ComplexTypeDLInstance<>(value, type, key);
				instance.validate();
				addExported(instance);
				return instance;
			} catch (InvalidBean ex) {
				throw new InvalidInstance("Could not map value into instance - " + ex.getMessage(), ex);
			}
		}
	}

	@Override
	public void addExported(DLInstance instance) throws InvalidInstance
	{
		assert instance != null;

		if (instance.getName() == null || instance.getName().isBlank()) {
			throw new InvalidInstance("An instance with no or blank name can not be exported to a module");
		}

		if (exported.contains(instance.getName())) {
			throw new InvalidInstance("An instance with name " + instance.getName() + " has already been exported to this module");
		}

		exported.add(instance.getName(), instance);
	}

	@Override
	public void addExported(Collection<DLInstance> instances) throws InvalidInstance
	{
		assert instances != null;

		for (DLInstance instance : instances) {
			addExported(instance);
		}
	}

	@Override
	public boolean hasExported(String name)
	{
		assert name != null;

		return exported.contains(name);
	}

	@Override
	public List<DLInstance> getExported()
	{
		return exported.list();
	}

	@Override
	public Optional<DLInstance> getExported(String name)
	{
		assert name != null;

		return exported.get(name);
	}

	protected Object resolveChildOrAttribute(DLInstance instance, String name)
	{
		assert instance != null;

		Optional<DLInstance> child = instance.getChild(name);

		if (child.isPresent()) {
			return child.get();
		}

		return instance.get(name);
	}

	@Override
	public Object resolveExportedPath(String path)
	{
		assert path != null;

		String[] pathSegments = path.split("\\.");

		Optional<DLInstance> currentRef = getExported(pathSegments[0]);

		if (currentRef.isEmpty()) {
			return null;
		}

		DLInstance current = currentRef.get();

		for (int i = 1; i < pathSegments.length; ++i) {
			String pathSegment = pathSegments[i];
			Object child = resolveChildOrAttribute(current, pathSegment);

			if (child == null) {
				//throw new InvalidValue("Path " + path + " could not get resolved for " + pathSegment);
				return null;
			}

			if (child instanceof DLInstance) {
				current = (DLInstance) child;
			} else if (i < pathSegments.length - 1) {
				//throw new InvalidValue("Path " + path + " could not get resolved for " + pathSegment);
				return null;
			} else {
				return child;
			}
		}

		// https://github.com/studio42gmbh/dl/issues/13 Unwrap simple instances
		// @improvement this unwrapping should be done more generic if possible
		if (current instanceof SimpleTypeDLInstance) {
			return ((SimpleTypeDLInstance) current).getData();
		}

		return current;
	}

	@Override
	public List<DLType> getTypes()
	{
		return types.list();
	}

	@Override
	public List<DLType> getComplexTypes()
	{
		return types
			.list()
			.stream()
			.filter((type) -> {
				return type.isComplexType();
			})
			.collect(Collectors.toUnmodifiableList());
	}

	@Override
	public List<DLType> getSimpleTypes()
	{
		return types
			.list()
			.stream()
			.filter((type) -> {
				return type.isSimpleType();
			})
			.collect(Collectors.toUnmodifiableList());
	}

	@Override
	public List<DLEnum> getEnums()
	{
		return types
			.list()
			.stream()
			.filter((type) -> {
				return (type instanceof DLEnum);
			})
			.map((type) -> {
				return (DLEnum) type;
			})
			.collect(Collectors.toUnmodifiableList());
	}

	public DLType defineType(Class javaType, String... aliases) throws DLException
	{
		assert javaType != null;

		return defineType(createType(javaType), aliases);
	}

	@Override
	public DLType defineType(DLType type, String... aliases) throws InvalidCore, InvalidType
	{
		assert type != null;

		if (!allowDefineTypes) {
			throw new InvalidCore("May not define types");
		}

		if (types.contains(type.getCanonicalName())) {
			throw new InvalidType("Type '" + type.getCanonicalName() + "' already defined");
		}

		types.add(type.getCanonicalName(), type);

		for (String alias : aliases) {
			defineAliasForType(alias, type);
		}

		return type;
	}

	public DLType defineArrayType(Class genericJavaClass) throws DLException
	{
		assert genericJavaClass != null;

		return defineArrayType(getType(genericJavaClass).orElseThrow());
	}

	public DLType defineArrayType(DLType genericType) throws DLException
	{
		assert genericType != null;

		return defineType(new ArrayDLType(
			ArrayDLType.DEFAULT_SYMBOL,
			genericType.getCanonicalName(),
			this),
			genericType.getJavaDataType().arrayType().getName());
	}

	@SuppressWarnings({"UseSpecificCatch", "null"})
	@Override
	public DLType createType(Class<?> typeClass) throws DLException
	{
		assert typeClass != null;

		if (!allowDefineTypes) {
			throw new InvalidCore("May not define types");
		}

		// https://github.com/studio42gmbh/dl/issues/3 special handling implementations of DLType 
		if (DLType.class.isAssignableFrom(typeClass)) {

			try {
				DLType dlType = (DLType) typeClass.getConstructor().newInstance();

				if (types.contains(dlType.getName())) {
					throw new InvalidType("Type '" + dlType.getName() + "' already defined");
				}

				return dlType;
			} catch (Exception ex) {
				throw new DLException("Error instantiating DLType '" + typeClass + "' implementation - " + ex.getMessage(), ex);
			}
		}

		// Create type from introspecting the given class
		String typeName = typeClass.getName();

		if (hasType(typeName)) {
			throw new InvalidType("Type '" + typeName + "' already defined");
		}

		// Create DLType and set basic values
		DefaultDLType classType = (DefaultDLType) createType(typeName);
		classType.setComplexType(true);
		classType.setJavaType(typeClass);
		classType.addAnnotation(getAnnotation(JavaDLAnnotation.DEFAULT_SYMBOL).get());

		try {

			BeanInfo<?> info = BeanHelper.getBeanInfo(typeClass);

			// Check abstract
			if (info.isInterface()
				|| info.isAbstract()) {
				classType.setAbstract(true);
			}

			// Check final
			if (info.isFinal()) {
				classType.setFinal(true);
			}

			//Attach single type annotation
			if (typeClass.isAnnotationPresent(AnnotationDL.class)) {
				AnnotationDL annotation = (AnnotationDL) typeClass.getAnnotation(AnnotationDL.class);
				addAnnotationToType(classType, annotation.value(), (Object[]) annotation.parameters());
			}

			// Attach multiple annotated annotations
			if (typeClass.isAnnotationPresent(AnnotationDLContainer.class)) {
				AnnotationDLContainer annotationContainer = (AnnotationDLContainer) typeClass.getAnnotation(AnnotationDLContainer.class);

				for (AnnotationDL annotation : annotationContainer.value()) {
					addAnnotationToType(classType, annotation.value(), (Object[]) annotation.parameters());
				}
			}

			// Map properties
			for (BeanProperty<?> property : info.getProperties()) {

				String attributeName = property.getName();

				// Dont map name and class
				if ("class".equals(attributeName)
					|| "name".equals(attributeName)) {
					continue;
				}

				DLType attributeType;
				Class attributeJavaType = property.getPropertyClass();

				// Equal - use the new type directly
				if (attributeJavaType.equals(typeClass)) {
					attributeType = classType;
				} // Not equal - find other type
				else {
					// Create specialized array type
					if (attributeJavaType.isArray()) {

						Optional<DLType> optType = getArrayType(attributeJavaType.getComponentType());

						if (optType.isEmpty()) {
							log.info("Ignoring attribute", attributeName, "as array type", attributeJavaType, "is unknown");
							continue;
						}

						attributeType = optType.orElseThrow();
					} // Create type optionally with generic types
					else {

						Optional<DLType> optType = getType(attributeJavaType, property.getGenericTypes());

						if (optType.isEmpty()) {
							log.info("Ignoring attribute", attributeName, "as type", attributeJavaType, "is unknown");
							continue;
						}

						attributeType = optType.orElseThrow();
					}
				}

				DefaultDLAttribute attribute = new DefaultDLAttribute(attributeName, attributeType);

				// Check for annotation AttributeDL
				if (property.isAnnotationPresent(AttributeDL.class)) {

					AttributeDL attr = property.getAnnotation(AttributeDL.class);

					// Set default value if the annotated defaultvalue is not blank
					String defValue = attr.defaultValue();
					if (!defValue.isBlank()) {
						try {
							attribute.setDefaultValue(
								ConversionHelper.convert(defValue, attributeType.getJavaDataType())
							);
						} catch (Throwable ex) {
							throw new RuntimeException("Error converting AttributeDL default value for attribute " + attribute.getName() + " - " + ex.getMessage(), ex);
						}
					}

					// Add required if the annotated attribute is required
					if (attr.required()) {
						addAnnotationToAttribute(classType, attribute, RequiredDLAnnotation.DEFAULT_SYMBOL);
					}
				}

				// Add single annotated annotations
				if (property.isAnnotationPresent(AnnotationDL.class)) {
					AnnotationDL annotation = property.getAnnotation(AnnotationDL.class);
					addAnnotationToAttribute(classType, attribute, annotation.value(), (Object[]) annotation.parameters());
				}

				// Add multiple annotated annotations
				if (property.isAnnotationPresent(AnnotationDLContainer.class)) {
					AnnotationDLContainer annotationContainer = property.getAnnotation(AnnotationDLContainer.class);

					for (AnnotationDL annotation : annotationContainer.value()) {
						addAnnotationToAttribute(classType, attribute, annotation.value(), (Object[]) annotation.parameters());
					}
				}

				// Add readonly or write only annotations
				if (property.isWriteOnly()) {
					addAnnotationToAttribute(classType, attribute, WriteOnlyDLAnnotation.DEFAULT_SYMBOL);
				} else if (property.isReadOnly()) {
					addAnnotationToAttribute(classType, attribute, ReadOnlyDLAnnotation.DEFAULT_SYMBOL);
				}

				classType.addAttribute(attribute);
			}
		} catch (InvalidBean ex) {
			throw new InvalidType("Class " + typeClass + " can not be introspected - " + ex.getMessage(), ex);
		}

		// Direct superclass
		if (typeClass.getSuperclass() != null) {

			if (hasType(typeClass.getSuperclass())) {
				DLType superType = getType(typeClass.getSuperclass()).get();
				classType.addParent(superType);
			}
		}

		// Direct interfaces
		for (Class interfaceClass : typeClass.getInterfaces()) {
			if (hasType(interfaceClass)) {
				DLType interfaceType = getType(interfaceClass).get();
				classType.addParent(interfaceType);
			}
		}

		// Make type derive from Object if given in core
		if (!classType.hasParents()
			&& hasType(ObjectDLType.DEFAULT_SYMBOL)) {
			classType.addParent(getType(ObjectDLType.DEFAULT_SYMBOL).get());
		}

		// Handle contains with searching for DLContainer<?>
		for (Type interfaceType : typeClass.getGenericInterfaces()) {
			if (interfaceType instanceof ParameterizedType) {

				ParameterizedType paramType = ((ParameterizedType) interfaceType);

				// validate if the type is a DLContainer
				if (DLContainer.class.isAssignableFrom((Class) paramType.getRawType())) {

					// check and add its types in diamond operator as contained types
					for (Type type : ((ParameterizedType) interfaceType).getActualTypeArguments()) {

						// container uses the current type - use the new type
						if ((type instanceof Class) && ((Class) type).equals(typeClass)) {
							classType.addContainedType(classType);
						} // other type -> find in core
						else if (type instanceof TypeVariable) {
							throw new InvalidType("Can not use generic type arguments");
						} else {
							classType.addContainedType(getType((Class) type).orElseThrow());
						}
					}
				}
			}
		}

		return classType;
	}

	@Override
	public boolean hasType(String name)
	{
		assert name != null;

		return types.contains(name);
	}

	@Override
	public boolean hasType(Class javaType)
	{
		assert javaType != null;

		return types.contains(javaType.getName());
	}

	public boolean hasArrayType(Class javaType) throws DLException
	{
		assert javaType != null;

		Optional<DLType> componentType = getType(javaType);

		if (componentType.isEmpty()) {
			return false;
		}

		String canonicalName = getTypeName(ArrayDLType.DEFAULT_SYMBOL, List.of(componentType.orElseThrow()));

		return types.contains(canonicalName);
	}

	@Override
	public boolean hasEnum(String name)
	{
		assert name != null;

		Optional<DLType> type = types.get(name);

		if (type.isEmpty()) {
			return false;
		}

		return type.get() instanceof DLEnum;
	}

	@Override
	public Optional<DLType> getType(Class javaClass) throws DLException
	{
		assert javaClass != null;

		return getType(javaClass.getName());
	}

	public Optional<DLType> getType(Class javaClass, List<Class> genericTypes) throws DLException
	{
		assert javaClass != null;

		if (genericTypes == null || genericTypes.isEmpty()) {
			return getType(javaClass);
		}

		List<DLType> genericDLTypes = new ArrayList<>();
		for (Class javaGenericTypeClass : genericTypes) {

			Optional<DLType> optType = getType(javaGenericTypeClass);

			if (optType.isEmpty()) {
				// do nothing?
			} else {
				genericDLTypes.add(optType.orElseThrow());
			}
		}

		return getType(javaClass.getName(), genericDLTypes);
	}

	@Override
	public Optional<DLType> getType(String name) throws DLException
	{
		assert name != null;
		
		Optional<DLType> type = types.get(name);
		
		if (type.isPresent()) {
			return type;
		}
		
		// No generics -> no dynamic type generation
		if (!isGenericTypeName(name)) {
			return Optional.empty();
		}
		
		String rawTypeName = getRawTypeName(name);
		List<DLType> generics = getGenericsFromTypeName(name);
		
		return getType(rawTypeName, generics);
	}
	
	protected boolean isGenericTypeName(String name)
	{
		assert name != null;
		
		return name.contains("<");
	}
	
	protected String getRawTypeName(String name)
	{
		int index = name.indexOf('<');
		
		if (index < 0) {
			return name;
		}
		
		return name.substring(0, index);
	}
	
	// @todo Add support for nested generic types
	protected List<DLType> getGenericsFromTypeName(String name) throws DLException
	{
		List<DLType> result = new ArrayList<>();
		
		int index = name.indexOf('<');
		int index2 = name.indexOf('>');
		
		if (index < 0) {
			return result;
		}
		
		if (index2 <= index) {
			throw new InvalidType("Type name " + name + " is invalid");
		}
		
		String[] genericTypeNames = name.substring(index + 1, index2).split("\\s*,\\s*");
		
		for (String genericTypeName : genericTypeNames) {
			
			Optional<DLType> optType = getType(genericTypeName);
			
			if (optType.isEmpty()) {
				throw new UndefinedType("Type " + genericTypeName + " is not defined");
			}
			
			result.add(optType.orElseThrow());
		}		

		return result;		
	}
	
	protected String getTypeName(String name, List<DLType> genericTypes)
	{
		if (genericTypes == null || genericTypes.isEmpty()) {
			return name;
		}

		String genericName = name + "<";

		Iterator<DLType> it = genericTypes.iterator();
		while (it.hasNext()) {
			DLType genericType = it.next();
			genericName += genericType.getName();
			if (it.hasNext()) {
				genericName += ",";
			}
		}

		genericName += ">";

		return genericName;
	}

	public Optional<DLType> getArrayType(Class genericJavaType) throws DLException
	{
		assert genericJavaType != null;

		Optional<DLType> genericType = getType(genericJavaType);

		if (genericType.isEmpty()) {
			return Optional.empty();
		}

		return getArrayType(genericType.orElseThrow());
	}

	public Optional<DLType> getArrayType(DLType genericType) throws InvalidCore, InvalidType, UndefinedType
	{
		assert genericType != null;

		return getType(ArrayDLType.DEFAULT_SYMBOL, List.of(genericType));
	}

	@Override
	public Optional<DLType> getType(String name, List<DLType> genericTypes) throws InvalidCore, InvalidType, UndefinedType
	{
		assert name != null;
		assert genericTypes != null;

		String canonicalName = getTypeName(name, genericTypes);

		Optional<DLType> type = types.get(canonicalName);

		if (type.isPresent()) {
			return type;
		}

		if (!types.contains(name)) {
			return Optional.empty();
		}

		if (genericTypes.isEmpty()) {
			throw new UndefinedType("Type " + name + " is not defined");
		}

		// add generic version
		type = types.get(name);

		if (!allowDefineTypes) {
			throw new InvalidCore("May not define types");
		}

		// generate specific typed version
		DefaultDLType specificType = ((DefaultDLType) type.get()).copy();

		specificType.addGenericTypes(genericTypes);

		types.add(canonicalName, specificType);

		//log.debug("Mapping generic type " + canonicalName);
		return Optional.of(specificType);
	}

	@Override
	public DLAnnotation defineAnnotation(DLAnnotation annotation, String... aliases) throws InvalidCore, InvalidAnnotation
	{
		assert annotation != null;
		assert aliases != null;

		if (!allowDefineAnnotations) {
			throw new InvalidCore("May not define annotations");
		}

		if (hasAnnotation(annotation.getName())) {
			throw new InvalidAnnotation("Annotation '" + annotation.getName() + "' is already defined");
		}

		annotations.add(annotation.getName(), annotation);

		for (String alias : aliases) {
			defineAliasForAnnotation(alias, annotation);
		}

		return annotation;
	}

	public void redefineAnnotation(DLAnnotation annotation) throws InvalidCore, InvalidAnnotation
	{
		assert annotation != null;

		if (!allowDefineAnnotations) {
			throw new InvalidCore("May not define annotations");
		}

		if (!hasAnnotation(annotation.getName())) {
			throw new InvalidAnnotation("Annotation '" + annotation.getName() + "' is not defined");
		}

		annotations.add(annotation.getName(), annotation);
	}

	@Override
	public boolean hasAnnotation(String name)
	{
		assert name != null;

		return annotations.contains(name);
	}

	@Override
	public Optional<DLAnnotation> getAnnotation(String name)
	{
		assert name != null;

		return annotations.get(name);
	}

	@Override
	public List<DLAnnotation> getAnnotations()
	{
		return annotations.list();
	}

	@Override
	public DLPragma definePragma(DLPragma pragma, String... aliases) throws InvalidPragma, InvalidCore
	{
		assert pragma != null;

		if (!allowDefinePragmas) {
			throw new InvalidCore("May not define pragmas");
		}

		if (hasPragma(pragma.getName())) {
			throw new InvalidPragma("Pragma '" + pragma + "' is already defined");
		}

		pragmas.add(pragma.getName(), pragma);

		for (String alias : aliases) {
			defineAliasForPragma(alias, pragma);
		}

		return pragma;
	}

	@Override
	public boolean hasPragma(String name)
	{
		assert name != null;

		return pragmas.contains(name);
	}

	@Override
	public Optional<DLPragma> getPragma(String name)
	{
		assert name != null;

		return pragmas.get(name);
	}

	@Override
	public List<DLPragma> getPragmas()
	{
		return pragmas.list();
	}

	@Override
	public DLModule parse(String moduleId) throws DLException
	{
		return parse(moduleId, null);
	}

	@Override
	public DLModule parse(String moduleId, String data) throws DLException
	{
		assert moduleId != null;

		// if the module was already loaded -> just return from cache
		if (requiredModules.containsKey(moduleId)) {
			log.debug("Ignoring file module (already loaded) " + moduleId);
			return requiredModules.get(moduleId);
		}

		DLModule module = null;

		if (data != null) {
			for (DLCoreResolver resolver : resolvers) {
				if (resolver.canParse(moduleId, data)) {
					module = resolver.parse(moduleId, data);
				}
			}
		} else {
			for (DLCoreResolver resolver : resolvers) {
				if (resolver.canParse(moduleId)) {
					module = resolver.parse(moduleId);
				}
			}
		}

		// still not loaded -> sry thats
		if (module == null) {
			throw new InvalidModule("No parser found for module " + moduleId);
		}

		requiredModules.put(moduleId, module);

		return module;
	}

	@Override
	public DLInstance createInstance(DLType type)
	{
		return new DefaultDLInstance(type);
	}

	@Override
	public DLInstance createInstance(DLType type, String name)
	{
		return new DefaultDLInstance(type, name);
	}

	@Override
	public DLModule createModule()
	{
		return new DefaultDLModule();
	}

	@Override
	public DLModule createModule(String name)
	{
		return new DefaultDLModule(name);
	}

	@Override
	public DLInstance convertFromJavaObject(Object object) throws DLException
	{
		assert object != null;

		Optional<DLType> optType = getType(object.getClass());

		if (optType.isEmpty()) {
			throw new InvalidType("No type mapped for java class " + object.getClass());
		}

		DLType type = optType.orElseThrow();

		return (DLInstance) type.fromJavaObject(this, object);
	}

	public void writeToFile(Path file, DLModule module) throws IOException
	{
		assert module != null;
		assert file != null;

		// store module to the given file
		String stringData = DLHelper.toString(module, true);

		//log.debug("\n" + stringData);
		FilesHelper.writeStringToFile(file, stringData);
	}

	@Override
	public Path getBasePath()
	{
		return basePath;
	}

	public void setBasePath(Path basePath)
	{
		this.basePath = basePath;
	}

	@Override
	public boolean isAllowDefineTypes()
	{
		return allowDefineTypes;
	}

	@Override
	public void setAllowDefineTypes(boolean allowDefineTypes)
	{
		this.allowDefineTypes = allowDefineTypes;
	}

	@Override
	public boolean isAllowDefineAnnotations()
	{
		return allowDefineAnnotations;
	}

	@Override
	public void setAllowDefineAnnotations(boolean allowDefineAnnotations)
	{
		this.allowDefineAnnotations = allowDefineAnnotations;
	}

	public List<DLCoreResolver> getResolvers()
	{
		return Collections.unmodifiableList(resolvers);
	}

	public void addResolver(DLCoreResolver resolver)
	{
		assert resolver != null;

		resolvers.add(resolver);
	}

	public void addResolverFirst(DLCoreResolver resolver)
	{
		assert resolver != null;

		resolvers.add(0, resolver);
	}

	public void removeResolver(DLCoreResolver resolver)
	{
		assert resolver != null;

		resolvers.remove(resolver);
	}

	@SuppressWarnings({"UseSpecificCatch", "AssertWithSideEffects"})
	@Override
	public synchronized <ObjectType> ObjectType convertFromInstance(DLInstance instance) throws InvalidInstance
	{
		assert instance != null;
		assert instance.getType() != null;
		assert instance.getType().getJavaDataType() != null;

		try {

			DLType type = instance.getType();

			Object convertInstance;
			String cacheKey = "" + instance.hashCode();

			WeakReference cacheRef = convertedCache.get(cacheKey);

			if (cacheRef != null) {

				convertInstance = cacheRef.get();

				if (convertInstance != null) {

					return (ObjectType) convertInstance;
				}
			} else {
				convertedCache.remove(cacheKey);
			}

			convertInstance = type.createJavaInstance();

			//allow maps to be set by that
			if (convertInstance instanceof Map) {

				//write properties of the instance
				for (String attributeName : instance.getAttributeNames()) {

					Object value = instance.get(attributeName);

					if (value instanceof Object[]) {

						Object[] convValue = new Object[((Object[]) value).length];

						for (int i = 0; i < ((Object[]) ((Object[]) value)).length; ++i) {

							Object val = ((Object[]) value)[i];

							if (val instanceof DLInstance) {
								convValue[i] = ((DLInstance) val).toJavaObject(this);
							} else {
								convValue[i] = val;
							}
						}

						value = convValue;
					} else if (value instanceof DLInstance) {
						value = ((DLInstance) value).toJavaObject(this);
					}

					((Map) convertInstance).put(attributeName, value);
				}

				for (DLInstance child : instance.getChildren()) {

					if (child.hasName()) {

						Object convertChild = convertFromInstance(child);

						((Map) convertInstance).put(child.getName(), convertChild);
					}
				}
			} //default to assume its a bean
			else {

				Class convertClass = type.getJavaDataType();
				BeanInfo info = BeanHelper.getBeanInfo(convertClass);

				//write properties of the instance
				for (String attributeName : instance.getAttributeNames()) {

					Object value = instance.get(attributeName);

					if (value instanceof Object[]) {

						Object[] convValue = new Object[((Object[]) value).length];

						for (int i = 0; i < ((Object[]) ((Object[]) value)).length; ++i) {

							Object val = ((Object[]) value)[i];

							if (val instanceof DLInstance) {
								convValue[i] = ((DLInstance) val).toJavaObject(this);
							} else {
								convValue[i] = val;
							}
						}

						value = convValue;
					} else if (value instanceof DLInstance) {

						try {
							value = ((DLInstance) value).toJavaObject(this);
						} catch (AssertionError ex) {
							throw new AssertionError("Error converting value '" + attributeName + "' to JavaObject in instance '" + instance.getName() + "'", ex);
						}
					}

					if (value != null) {
						info.writeConverted(convertInstance, attributeName, value);
					}
				}

				if (info.hasWriteProperty("name")) {
					info.write(convertInstance, "name", instance.getName());
				}

				// has children and implements the DLContainer interface
				if (instance.hasChildren() && (convertInstance instanceof DLContainer)) {

					for (DLInstance child : instance.getChildren()) {

						Object convertChild = convertFromInstance(child);

						((DLContainer) convertInstance).addChild(child.getName(), convertChild);
					}
				}
			}

			convertedCache.put(cacheKey, new WeakReference<>(convertInstance));

			return (ObjectType) convertInstance;
		} catch (Throwable ex) {
			throw new InvalidInstance("Error initializing instance " + instance.getName() + " of java type " + instance.getType().getJavaDataType() + " - " + ex.getMessage(), ex);
		}
	}

	public void emptyConversionCache()
	{
		convertedCache.clear();
	}

	@Override
	public boolean isAllowDefinePragmas()
	{
		return allowDefinePragmas;
	}

	@Override
	public void setAllowDefinePragmas(boolean allowDefinePragmas)
	{
		this.allowDefinePragmas = allowDefinePragmas;
	}

	@Override
	public boolean isAllowRequire()
	{
		return allowRequire;
	}

	@Override
	public void setAllowRequire(boolean allowRequire)
	{
		this.allowRequire = allowRequire;
	}
}
