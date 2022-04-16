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
package de.s42.dl.types;

import de.s42.base.conversion.ConversionHelper;
import de.s42.dl.*;
import de.s42.dl.instances.DLInstanceValidator;
import de.s42.dl.exceptions.InvalidType;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.exceptions.InvalidValue;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author Benjamin Schiller
 */
public class DefaultDLType implements DLType
{

	protected final Map<String, String> meta = new HashMap<>();
	protected final Map<String, DLAttribute> attributes = new HashMap<>();
	protected final List<DLInstanceValidator> validators = new ArrayList<>();
	protected final List<DLMappedAnnotation> annotations = new ArrayList<>();
	protected final List<DLType> parents = new ArrayList<>();
	protected final List<DLType> genericTypes = new ArrayList<>();
	protected final List<DLType> containedTypes = new ArrayList<>();

	protected String name;
	protected boolean allowDynamicAttributes = false;
	protected boolean allowGenericTypes = false;
	protected boolean isAbstract = false;
	protected boolean isFinal = false;
	protected boolean complexType = false;

	protected Class javaType;

	public DefaultDLType()
	{

	}

	public DefaultDLType(String name)
	{
		assert name != null;

		this.name = name;
	}

	public DefaultDLType(String name, Class javaType)
	{
		assert name != null;
		assert javaType != null;

		this.name = name;
		this.javaType = javaType;
	}

	public DefaultDLType(Class javaType)
	{
		assert javaType != null;

		this.name = javaType.getName();
		this.javaType = javaType;
	}

	public DefaultDLType copy()
	{
		try {
			DefaultDLType copy = getClass().getConstructor().newInstance();

			copy.name = name;
			copy.allowDynamicAttributes = allowDynamicAttributes;
			copy.allowGenericTypes = allowGenericTypes;
			copy.isAbstract = isAbstract;
			copy.isFinal = isFinal;
			copy.javaType = javaType;
			copy.attributes.putAll(attributes);
			copy.meta.putAll(meta);
			copy.validators.addAll(validators);
			copy.parents.addAll(parents);
			copy.genericTypes.addAll(genericTypes);
			copy.containedTypes.addAll(containedTypes);
			copy.annotations.addAll(annotations);

			return copy;
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
			throw new RuntimeException("Could not copy type - " + ex.getMessage(), ex);
		}
	}

	@Override
	public Class getJavaDataType()
	{
		Class type = getJavaType();

		if (type != null) {
			return type;
		}

		return Object.class;
	}

	@Override
	public boolean isDerivedTypeOf(DLType other)
	{
		assert other != null;

		// equal type
		if (this.equals(other)) {
			return true;
		}

		// traverse parents
		for (DLType parentType : getParents()) {
			if (parentType.isDerivedTypeOf(other)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isAssignableFrom(DLType other)
	{
		assert other != null;

		return other.isDerivedTypeOf(this);
	}

	@Override
	public Object read(Object... sources) throws InvalidValue, InvalidType
	{
		// abstract types cannot be read from
		if (isAbstract()) {
			throw new InvalidType("Type '" + getCanonicalName() + "' is abstract and thus can not be used to read input");
		}

		// complex types cannot be read from
		//if (isComplexType()) {
		//	throw new InvalidType("Type '" + getCanonicalName() + "' is complex and thus can not be used to read input");
		//}
		if (sources != null && sources.length == 1) {

			Object source = sources[0];

			if (source == null) {
				throw new InvalidType("sources[0] is null");
			}

			if (getJavaDataType().isAssignableFrom(source.getClass())) {
				return source;
			}

			return sources[0].toString();
		}

		return sources;
	}

	@Override
	public Object write(Object data)
	{
		return ConversionHelper.convert(data, String.class);
	}

	@Override
	public List<DLType> getOwnParents()
	{
		return Collections.unmodifiableList(parents);
	}

	@Override
	public List<DLType> getParents()
	{
		List<DLType> result = new ArrayList<>();

		result.addAll(parents);

		for (DLType parent : parents) {

			for (DLType grandParent : parent.getParents()) {
				if (!result.contains(grandParent)) {
					result.add(grandParent);
				}
			}
		}

		return Collections.unmodifiableList(result);
	}

	@Override
	public boolean hasParents()
	{
		return !parents.isEmpty();
	}

	public void addParent(DLType parent)
	{
		assert parent != null;

		parents.add(parent);
	}

	@Override
	public List<DLType> getOwnContainedTypes()
	{
		return Collections.unmodifiableList(containedTypes);
	}

	@Override
	public List<DLType> getContainedTypes()
	{
		List<DLType> result = new ArrayList<>();

		result.addAll(containedTypes);

		for (DLType parent : parents) {

			for (DLType grandParent : parent.getContainedTypes()) {
				if (!result.contains(grandParent)) {
					result.add(grandParent);
				}
			}
		}

		return Collections.unmodifiableList(result);
	}

	public void addContainedType(DLType containedType)
	{
		assert containedType != null;

		containedTypes.add(containedType);
	}

	@Override
	public boolean mayContainSpecificType(DLType type)
	{
		assert type != null;

		if (containedTypes.contains(type)) {
			return true;
		}

		for (DLType parent : parents) {
			if (parent.mayContainType(type)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean mayContainType(DLType type)
	{
		assert type != null;

		if (mayContainSpecificType(type)) {
			return true;
		}

		for (DLType parentType : type.getParents()) {
			if (mayContainSpecificType(parentType)) {
				return true;
			}
		}

		return false;
	}

	public void addValidator(DLInstanceValidator validator)
	{
		assert validator != null;

		validators.add(validator);
	}

	@Override
	public void validateInstance(DLInstance instance) throws InvalidInstance
	{
		assert instance != null;

		for (DLInstanceValidator validator : validators) {
			validator.validate(instance);
		}

		for (DLType parent : parents) {
			parent.validateInstance(instance);
		}
	}

	public void addAttribute(DLAttribute attribute)
	{
		assert attribute != null;

		attributes.put(attribute.getName(), attribute);
	}

	@Override
	public boolean hasAttribute(String name)
	{
		assert name != null;

		if (attributes.containsKey(name)) {
			return true;
		}

		for (DLType parent : parents) {
			if (((DefaultDLType) parent).hasAttribute(name)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Set<DLAttribute> getOwnAttributes()
	{
		return Collections.unmodifiableSet(new HashSet<>(attributes.values()));
	}

	@Override
	public Set<DLAttribute> getAttributes()
	{
		Map<String, DLAttribute> result = new HashMap<>();

		result.putAll(attributes);

		for (DLType parent : parents) {

			for (DLAttribute attr : parent.getAttributes()) {
				result.putIfAbsent(attr.getName(), attr);
			}
		}

		return Collections.unmodifiableSet(new HashSet<>(result.values()));
	}

	@Override
	public Set<String> getAttributeNames()
	{
		Set<String> result = new HashSet<>(attributes.keySet());

		for (DLType parent : parents) {

			result.addAll(parent.getAttributeNames());
		}

		return Collections.unmodifiableSet(result);
	}

	@Override
	public boolean hasOwnAttributes()
	{
		if (!attributes.isEmpty()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasAttributes()
	{
		if (hasOwnAttributes()) {
			return true;
		}

		for (DLType parent : parents) {

			if (parent.hasAttributes()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Optional<DLAttribute> getAttribute(String name)
	{
		assert name != null;

		DLAttribute attribute = attributes.get(name);

		if (attribute != null) {
			return Optional.of(attribute);
		}

		for (DLType parent : parents) {
			if (((DefaultDLType) parent).hasAttribute(name)) {
				return parent.getAttribute(name);
			}
		}

		return Optional.empty();
	}

	@Override
	public boolean isComplexType()
	{
		return hasAttributes() || complexType;
	}

	@Override
	public boolean isSimpleType()
	{
		return !isComplexType();
	}

	@Override
	public boolean isAllowDynamicAttributes()
	{
		return allowDynamicAttributes;
	}

	public void setAllowDynamicAttributes(boolean allowDynamicAttributes)
	{
		this.allowDynamicAttributes = allowDynamicAttributes;
	}

	@Override
	public boolean isGenericType()
	{
		return allowGenericTypes && (genericTypes.size() > 0);
	}

	public void addGenericType(DLType genericType) throws InvalidType
	{
		assert genericType != null;

		if (!allowGenericTypes) {
			throw new InvalidType("This type " + name + " is not allowing generic types");
		}

		genericTypes.add(genericType);
	}

	public void addGenericTypes(List<DLType> genericTypes) throws InvalidType
	{
		assert genericTypes != null;

		if (!allowGenericTypes) {
			throw new InvalidType("This type " + name + " is not allowing generic types");
		}

		this.genericTypes.addAll(genericTypes);
	}

	@Override
	public List<DLType> getGenericTypes()
	{
		return Collections.unmodifiableList(genericTypes);
	}

	@Override
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public boolean isAbstract()
	{
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract)
	{
		this.isAbstract = isAbstract;
	}

	@Override
	public boolean isAllowGenericTypes()
	{
		return allowGenericTypes;
	}

	public void setAllowGenericTypes(boolean allowGenericTypes)
	{
		this.allowGenericTypes = allowGenericTypes;
	}

	@Override
	public String getCanonicalName()
	{
		if (genericTypes.isEmpty()) {
			return name;
		}

		StringBuilder genericName = new StringBuilder();

		genericName.append(name).append("<");

		Iterator<DLType> it = genericTypes.iterator();
		while (it.hasNext()) {
			DLType genericType = it.next();
			genericName.append(genericType.getName());
			if (it.hasNext()) {
				genericName.append(",");
			}
		}

		genericName.append(">");

		return genericName.toString();
	}

	//@Override
	public void addAnnotation(DLAnnotation annotation, Object... parameters)
	{
		assert annotation != null;

		DLMappedAnnotation mapped = new DLMappedAnnotation(annotation, parameters);

		annotations.add(mapped);
	}

	@Override
	public List<DLMappedAnnotation> getAnnotations()
	{
		return Collections.unmodifiableList(annotations);
	}

	@Override
	public String toString()
	{
		return getCanonicalName();
	}

	@Override
	public boolean isFinal()
	{
		return isFinal;
	}

	public void setFinal(boolean isFinal)
	{
		this.isFinal = isFinal;
	}

	public void setComplexType(boolean complexType)
	{
		this.complexType = complexType;
	}

	public Class getJavaType()
	{
		if (javaType != null) {
			return javaType;
		}

		for (DLType parent : parents) {

			Class parentJavaType = ((DefaultDLType) parent).getJavaType();

			if (parentJavaType != null) {
				return parentJavaType;
			}
		}

		return Object.class;
	}

	public void setJavaType(Class javaType)
	{
		this.javaType = javaType;
	}

	@Override
	public Optional<DLMappedAnnotation> getAnnotation(Class<? extends DLAnnotation> annotationType)
	{
		assert annotationType != null;

		for (DLMappedAnnotation annotation : annotations) {
			if (annotationType.isAssignableFrom(annotation.getAnnotation().getClass())) {
				return Optional.of(annotation);
			}
		}

		return Optional.empty();
	}

	@Override
	public boolean hasAnnotation(Class<? extends DLAnnotation> annotationType)
	{
		assert annotationType != null;

		for (DLMappedAnnotation annotation : annotations) {
			if (annotationType.isAssignableFrom(annotation.getAnnotation().getClass())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean hasParent(DLType parent)
	{
		return getParents().contains(parent);
	}

	@Override
	public boolean hasOwnParent(DLType parent)
	{
		return getOwnParents().contains(parent);
	}
}
