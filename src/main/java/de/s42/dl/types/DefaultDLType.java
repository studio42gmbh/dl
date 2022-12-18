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

import de.s42.base.beans.BeanHelper;
import de.s42.base.beans.BeanInfo;
import de.s42.base.beans.InvalidBean;
import de.s42.base.collections.MappedList;
import de.s42.base.conversion.ConversionHelper;
import de.s42.dl.*;
import de.s42.dl.annotations.AbstractDLAnnotated;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidType;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.exceptions.InvalidValue;
import de.s42.dl.exceptions.UndefinedType;
import de.s42.dl.validation.DLInstanceValidator;
import de.s42.dl.validation.DLTypeValidator;
import static de.s42.dl.validation.DefaultValidationCode.InvalidAttributeRedefinition;
import de.s42.dl.validation.ValidationResult;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author Benjamin Schiller
 */
public class DefaultDLType extends AbstractDLAnnotated implements DLType
{

	private final static Logger log = LogManager.getLogger(DefaultDLType.class.getName());

	protected final MappedList<String, DLAttribute> attributes = new MappedList<>();
	protected final List<DLTypeValidator> validators = new ArrayList<>();
	protected final List<DLInstanceValidator> instanceValidators = new ArrayList<>();
	protected final List<DLType> parents = new ArrayList<>();
	protected final List<DLType> genericTypes = new ArrayList<>();
	protected final List<DLType> containedTypes = new ArrayList<>();

	protected DLCore core;
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

			copy.core = core; // ATTENTION: This type is not contained in core after copy
			copy.name = name;
			copy.allowDynamicAttributes = allowDynamicAttributes;
			copy.allowGenericTypes = allowGenericTypes;
			copy.isAbstract = isAbstract;
			copy.isFinal = isFinal;
			copy.javaType = javaType;
			copy.attributes.addAll(attributes);
			copy.validators.addAll(validators);
			copy.instanceValidators.addAll(instanceValidators);
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
	public boolean validate(ValidationResult result)
	{
		assert result != null;

		boolean value = true;

		List<DLType> allParents = getParents();

		// Validate all attributes of this and its parent types are consistent
		Map<String, DLAttribute> attribs = new HashMap<>(attributes.map());
		for (DLType par : allParents) {

			// Compare each attribute of a parent to the collected attributes
			for (DLAttribute parAttrib : par.getOwnAttributes()) {

				DLAttribute alreadyContained = attribs.get(parAttrib.getName());

				// If it is redefined it has to have the absolute equal data type
				if (alreadyContained != null && !alreadyContained.equalOrMoreSpecificDataType(parAttrib)) {
					result.addError(InvalidAttributeRedefinition.toString(), "Attribute '" + alreadyContained + "' has a different data type than '" + parAttrib + "'");
					value = false;
				} // Add this attribute to the list of scanned attributes
				else {
					attribs.put(parAttrib.getName(), parAttrib);
				}
			}
		}

		// Validate own attributes
		for (DLAttribute attribute : getOwnAttributes()) {
			value &= attribute.validate(result);
		}

		// Validate type with local validators
		for (DLTypeValidator validator : getValidators()) {
			value &= validator.validate(this, result);
		}

		// This will get all distinct parents (deep)
		for (DLType parent : allParents) {
			value &= validateParent(parent, result);
		}

		return value;
	}

	/**
	 * This method is used internally to validate this type using the parent types validators
	 *
	 * @param parent
	 * @param result
	 *
	 * @return
	 */
	protected boolean validateParent(DLType parent, ValidationResult result)
	{
		assert parent != null;
		assert result != null;

		boolean value = true;

		// Validate attributes of parent
		for (DLAttribute attribute : parent.getOwnAttributes()) {
			value &= attribute.validate(result);
		}

		// Iterate type validators and validate with this type
		for (DLTypeValidator validator : parent.getValidators()) {
			value &= validator.validate(this, result);
		}

		return value;
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

		// Equal type
		if (this.equals(other)) {
			return true;
		}

		// More specific generic of other non generic
		if (this.getName().equals(other.getName())
			&& this.isGenericType() && !other.isGenericType()) {
			return true;
		}

		// Traverse parents
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

		// https://github.com/studio42gmbh/dl/issues/12 complex types cannot be read from
		if (isComplexType()) {
			throw new InvalidType("Type '" + getCanonicalName() + "' is complex and thus can not be used to read input");
		}

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
	public DLInstance fromJavaObject(Object object) throws DLException
	{
		try {
			BeanInfo info = BeanHelper.getBeanInfo(object.getClass());

			String instanceName = null;

			// write the name
			if (info.hasReadProperty("name")) {
				instanceName = (String) info.read(object, "name");
			}

			DLInstance instance = core.createInstance(this, instanceName);

			// Fill instance from object
			for (DLAttribute attribute : getAttributes()) {

				if (attribute.isWritable()) {

					Object rawValue = (Object) info.read(object, attribute.getName());

					DLType valueType = attribute.getType();

					// @todo why did i put the raw value conversion in? it seems to break generic types like maps Map<String, Object> to be reduced to Map
					/*if (rawValue != null) {
						Optional<DLType> optType = core.getType(rawValue.getClass());
						
						if (optType.isEmpty()) {
							throw new InvalidType("Type " + rawValue.getClass().getName() + " of attribute " + attribute.getName() + " is not mapped");
						}
						
						valueType = optType.orElseThrow();
					}*/
					Object value;

					if (valueType.isSimpleType()) {
						value = attribute.getType().read(rawValue);
					} else {
						value = valueType.fromJavaObject(rawValue);
					}

					instance.set(attribute.getName(), value);

					//log.debug("Set attribute", attribute.getName(), value);
				}
			}

			// Add children if the object is a DLContainer and has the trivial method getChildren
			if (object instanceof DLContainer && object.getClass().getMethod("getChildren") != null) {

				List children = (List) object.getClass().getMethod("getChildren").invoke(object);

				for (Object child : children) {
					instance.addChild(core.convertFromJavaObject(child));
				}

			}

			ValidationResult result = new ValidationResult();
			if (!instance.validate(result)) {
				throw new InvalidInstance("Object could not get converted - " + result.toMessage());
			}

			return instance;
		} catch (InvalidInstance | UndefinedType | InvalidBean | NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
			throw new InvalidInstance("Error converting from object - " + ex.getMessage(), ex);
		}
	}

	@Override
	public void setAttributeFromValue(DLInstance instance, String name, Object value) throws DLException
	{
		assert instance != null;
		assert name != null;

		Optional<DLAttribute> attributeType = getAttribute(name);

		// Set a defined attribute
		if (attributeType.isPresent()) {

			if (value instanceof DLInstance) {
				instance.set(name, value);
			} else {
				instance.set(name, attributeType.orElseThrow().getType().read(value));
			}
		} // Set a dynamic attribute
		else {

			instance.set(name, value);
		}
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

			for (DLType containedParent : parent.getContainedTypes()) {
				if (!result.contains(containedParent)) {
					result.add(containedParent);
				}
			}
		}

		return Collections.unmodifiableList(result);
	}

	@Override
	public boolean hasOwnContainedTypes()
	{
		return !containedTypes.isEmpty();
	}

	@Override
	public boolean hasContainedTypes()
	{
		if (hasOwnContainedTypes()) {
			return true;
		}

		for (DLType parent : parents) {

			if (parent.hasOwnContainedTypes()) {
				return true;
			}
		}

		return false;
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

	@Override
	public boolean addInstanceValidator(DLInstanceValidator validator)
	{
		assert validator != null;

		return instanceValidators.add(validator);
	}

	@Override
	public boolean addValidator(DLTypeValidator validator)
	{
		assert validator != null;

		return validators.add(validator);
	}

	@Override
	public List<DLTypeValidator> getValidators()
	{
		return Collections.unmodifiableList(validators);
	}

	@Override
	public List<DLInstanceValidator> getInstanceValidators()
	{
		return Collections.unmodifiableList(instanceValidators);
	}

	@Override
	public boolean validateInstance(DLInstance instance, ValidationResult result)
	{
		assert instance != null;

		boolean valid = true;

		for (DLInstanceValidator validator : instanceValidators) {
			valid &= validator.validate(instance, result);
		}

		for (DLType parent : parents) {
			valid &= parent.validateInstance(instance, result);
		}

		return valid;
	}

	@Override
	public void addAttribute(DLAttribute attribute) throws InvalidType
	{
		assert attribute != null;

		if (attributes.contains(attribute.getName())) {
			throw new InvalidType("Attribute with name '" + attribute.getName() + "' is already contained in type " + getCanonicalName());
		}

		attributes.add(attribute.getName(), attribute);
	}

	@Override
	public boolean hasAttribute(String name)
	{
		assert name != null;

		if (attributes.contains(name)) {
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
		return attributes.values();
	}

	@Override
	public Set<DLAttribute> getAttributes()
	{
		Map<String, DLAttribute> result = new HashMap<>();

		result.putAll(attributes.map());

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
		Set<String> result = new HashSet<>(attributes.keys());

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

		Optional<DLAttribute> attribute = attributes.get(name);

		if (attribute.isPresent()) {
			return attribute;
		}

		for (DLType parent : parents) {
			if (((DefaultDLType) parent).hasAttribute(name)) {
				return parent.getAttribute(name);
			}
		}

		return Optional.empty();
	}

	@Override
	// @todo https://github.com/studio42gmbh/dl/issues/23 DLType/DefaultDLType Improve and sharpen definition of complex and simple types - does it need further distinction?
	public boolean isComplexType()
	{
		return complexType
			|| isAllowDynamicAttributes()
			|| hasAttributes()
			|| hasContainedTypes();
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
		return allowGenericTypes && (!genericTypes.isEmpty());
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

	@Override
	public boolean isDeclaration()
	{
		return !isFinal() && !isAbstract() && !isGenericType() && !hasAttributes() && !hasAnnotations() && !hasParents();
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
	public boolean hasParent(DLType parent)
	{
		return getParents().contains(parent);
	}

	@Override
	public boolean hasOwnParent(DLType parent)
	{
		return getOwnParents().contains(parent);
	}

	@Override
	public Object createJavaInstance() throws InvalidType
	{
		try {
			return getJavaDataType().getConstructor().newInstance();
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
			throw new InvalidType("Type can not create a java instance - " + ex.getMessage(), ex);
		}
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(name, genericTypes);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof DefaultDLType)) {
			return false;
		}

		DefaultDLType other = (DefaultDLType) obj;

		return Objects.equals(name, other.name)
			&& Objects.equals(genericTypes, other.genericTypes);
	}

	@Override
	public DLCore getCore()
	{
		return core;
	}

	public void setCore(DLCore core)
	{
		this.core = core;
	}
}
