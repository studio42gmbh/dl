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
package de.s42.dl.instances;

import de.s42.base.collections.MappedList;
import de.s42.dl.*;
import de.s42.dl.annotations.AbstractDLAnnotated;
import de.s42.dl.exceptions.InvalidAttribute;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.validation.DLInstanceValidator;
import de.s42.dl.validation.ValidationResult;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

/**
 *
 * @author Benjamin Schiller
 */
// @todo https://github.com/studio42gmbh/dl/issues/21 DLInstance rethink interface and implementation of getters - Optional? Performance, Security, Convenience
public class DefaultDLInstance extends AbstractDLAnnotated implements DLInstance
{

	private final static Logger log = LogManager.getLogger(DefaultDLInstance.class.getName());

	// one DLInstance can just have one Java Object representation
	protected Object javaObject;
	protected DLType type;
	protected final MappedList<String, Object> attributes = new MappedList<>();
	protected final MappedList<String, DLInstance> children = new MappedList<>();
	protected final List<DLInstanceValidator> validators = new ArrayList<>();

	public DefaultDLInstance()
	{
		this((String) null);
	}

	/**
	 *
	 * @param name optional
	 */
	public DefaultDLInstance(String name)
	{
		this(null, name);
	}

	/**
	 *
	 * @param type optional
	 */
	public DefaultDLInstance(DLType type)
	{
		this(type, null);
	}

	/**
	 *
	 * @param type optional
	 * @param name optional
	 */
	public DefaultDLInstance(DLType type, String name)
	{
		this.name = name;
		this.type = type;
	}

	@Override
	public DLCore getCore()
	{
		return type.getCore();
	}

	@Override
	public boolean addValidator(DLInstanceValidator validator)
	{
		assert validator != null;

		return validators.add(validator);
	}

	@Override
	public List<DLInstanceValidator> getValidators()
	{
		return Collections.unmodifiableList(validators);
	}

	@Override
	public boolean validate(ValidationResult result)
	{
		assert result != null;

		// Type validator
		if (type != null) {
			type.validateInstance(this, result);
		}

		// Instance validators
		for (DLInstanceValidator validator : validators) {
			validator.validate(this, result);
		}

		return result.isValid();
	}

	@Override
	public Optional<DLAttribute> getAttribute(String name)
	{
		assert name != null;

		return type.getAttribute(name);
	}

	@Override
	public Set<String> getAttributeNames()
	{
		if (type != null) {

			// @improvement is there a faster way to combine the attributes of the instance and the type? dynamic of parents is tricky
			Set<String> attributeNames = new HashSet<>(attributes.keys());

			attributeNames.addAll(getType().getAttributeNames());

			return Collections.unmodifiableSet(attributeNames);
		}

		return Collections.unmodifiableSet(attributes.keys());
	}

	@Override
	public void set(String key, Object value)
	{
		assert key != null;

		if (value != null) {
			attributes.add(key, value);
		} else {
			attributes.remove(key);
		}
	}

	@Override
	public Map<String, Object> getAttributes()
	{
		return attributes.map();
	}

	@Override
	public boolean hasAttributes()
	{
		return !attributes.isEmpty();
	}

	@Override
	public boolean hasDynamicAttributes()
	{
		Set<String> attributeNames = type.getAttributeNames();

		for (String attributeName : attributes.keys()) {
			if (!attributeNames.contains(attributeName)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean hasAttribute(String key)
	{
		assert key != null;

		return attributes.contains(key);
	}

	@Override
	public Object get(String key)
	{
		assert key != null;

		Optional attribute = attributes.get(key);

		if (attribute.isPresent()) {
			return attribute.orElseThrow();
		} else if (type != null && type.hasAttribute(key)) {
			return type.getAttribute(key).orElseThrow().getDefaultValue();
		}
		return null;
	}

	@Override
	public DLInstance getInstance(String key)
	{
		assert key != null;

		return (DLInstance) get(key);
	}

	@Override
	public Object getInstanceAsJavaObject(String key) throws InvalidAttribute
	{
		assert key != null;

		DefaultDLInstance instance = (DefaultDLInstance) get(key);

		if (instance == null) {
			throw new InvalidAttribute("Instance attribute '" + key + "' is not mapped.");
		}

		return instance.toJavaObject();
	}

	@Override
	public String getString(String key)
	{
		assert key != null;

		try {
			return (String) get(key);
		} catch (ClassCastException ex) {
			throw new RuntimeException("Error getting " + key + " as String as its class is " + key.getClass().getCanonicalName());
		}
	}

	@Override
	public short getShort(String key)
	{
		assert key != null;

		try {
			return (short) get(key);
		} catch (ClassCastException ex) {
			throw new RuntimeException("Error getting " + key + " as String as its class is " + key.getClass().getCanonicalName());
		}
	}

	@Override
	public char getChar(String key)
	{
		assert key != null;

		try {
			return (char) get(key);
		} catch (ClassCastException ex) {
			throw new RuntimeException("Error getting " + key + " as String as its class is " + key.getClass().getCanonicalName());
		}
	}

	@Override
	public byte getByte(String key)
	{
		assert key != null;

		try {
			return (byte) get(key);
		} catch (ClassCastException ex) {
			throw new RuntimeException("Error getting " + key + " as String as its class is " + key.getClass().getCanonicalName());
		}
	}

	@Override
	public Number getNumber(String key)
	{
		assert key != null;

		try {
			return (Number) get(key);
		} catch (ClassCastException ex) {
			throw new RuntimeException("Error getting " + key + " as Number as its class is " + key.getClass().getCanonicalName());
		}
	}

	@Override
	public int getInt(String key)
	{
		assert key != null;

		return getNumber(key).intValue();
	}

	@Override
	public long getLong(String key)
	{
		assert key != null;

		return getNumber(key).longValue();
	}

	@Override
	public float getFloat(String key)
	{
		assert key != null;

		return getNumber(key).floatValue();
	}

	@Override
	public double getDouble(String key)
	{
		assert key != null;

		return getNumber(key).doubleValue();
	}

	@Override
	public boolean getBoolean(String key)
	{
		assert key != null;

		try {
			return (boolean) get(key);
		} catch (ClassCastException ex) {
			throw new RuntimeException("Error getting " + key + " as Boolean as its class is " + key.getClass().getCanonicalName());
		}

	}

	@Override
	public DLInstance getChild(int index)
	{
		assert index >= 0;
		assert index < children.size();

		return children.get(index);
	}

	@Override
	public Optional<DLInstance> getChild(String name)
	{
		assert name != null;

		return children.get(name);
	}

	@Override
	public Optional getChildAsJavaObject(String name)
	{
		assert name != null;

		Optional<DLInstance> child = getChild(name);

		if (child.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(child.get().toJavaObject());
	}

	@Override
	public Optional<DLInstance> resolveChild(String path)
	{
		assert path != null;

		String[] pathSegments = path.split("\\.");
		DLInstance current = this;

		for (String pathSegment : pathSegments) {

			Optional<DLInstance> child = current.getChild(pathSegment);

			if (child.isEmpty()) {
				return Optional.empty();
			}

			current = child.get();
		}

		return Optional.of(current);
	}

	@Override
	public Optional<DLInstance> getChild(DLType childType)
	{
		assert childType != null;

		for (DLInstance child : children.list()) {
			if (childType.isAssignableFrom(child.getType())) {
				return Optional.of(child);
			}
		}

		return Optional.empty();
	}

	@Override
	public Optional getChildAsJavaObject(DLType childType)
	{
		assert childType != null;

		Optional<DLInstance> child = getChild(childType);

		if (child.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(child.get().toJavaObject());
	}

	@Override
	public List<DLInstance> getChildren(DLType childType)
	{
		assert childType != null;

		List<DLInstance> result = new ArrayList<>(children.size());

		for (DLInstance child : children.list()) {
			if (childType.isAssignableFrom(child.getType())) {
				result.add(child);
			}
		}

		return result;
	}

	@Override
	public void addChild(DLInstance child) throws InvalidInstance
	{
		assert child != null;

		if (!type.mayContainType(child.getType())) {
			throw new InvalidInstance(
				"Child '" + child.getName()
				+ "' with type '" + child.getType()
				+ "' with parents " + Arrays.toString(child.getType().getParents().toArray())
				+ " may not be contained but " + Arrays.toString(type.getContainedTypes().toArray())
			);
		}

		String childName = child.getName();
		if (childName != null) {

			if (children.contains(childName)) {
				throw new InvalidInstance("Child with name " + childName + " is already mapped");
			}

			children.add(childName, child);
		} else {
			children.add(UUID.randomUUID().toString(), child);
		}
	}

	@Override
	public void addChildren(Collection<DLInstance> children) throws InvalidInstance
	{
		assert children != null;

		for (DLInstance child : children) {
			addChild(child);
		}
	}

	protected void traverseChildren(DLInstance current, Consumer<DLInstance> callback)
	{
		assert current != null;
		assert callback != null;

		current.getChildren().forEach((child) -> {
			traverseChildren((DLInstance) child, callback);
			callback.accept((DLInstance) child);
		});
	}

	public void traverseChildren(Consumer<DLInstance> callback)
	{
		assert callback != null;

		traverseChildren(this, callback);
	}

	@Override
	public List<DLInstance> getChildren()
	{
		return children.list();
	}

	@Override
	public boolean hasChild(String name)
	{
		assert name != null;

		return children.contains(name);
	}

	@Override
	public boolean hasChildren()
	{
		return !children.isEmpty();
	}

	@Override
	public boolean isNamed()
	{
		return (name != null) && !name.isBlank();
	}

	@Override
	public DLType getType()
	{
		return type;
	}

	public void setType(DLType type)
	{
		this.type = type;
	}

	@SuppressWarnings("UseSpecificCatch")
	@Override
	public synchronized Object toJavaObject()
	{
		try {
			if (javaObject == null) {
				DLCore core = getCore();
				javaObject = core.convertFromInstance(this);
			}
		} catch (Throwable ex) {
			throw new RuntimeException("Error converting instance '" + name + "' - " + ex.getMessage(), ex);
		}

		return javaObject;
	}

	@Override
	public <ObjectType extends Object> Optional<ObjectType> getChildAsJavaObject(Class<ObjectType> javaType)
	{
		assert javaType != null;

		DLType instanceType = getCore().getType(javaType).orElseThrow();

		for (DLInstance child : getChildren(instanceType)) {

			return Optional.of((ObjectType) child.toJavaObject());
		}

		return Optional.empty();
	}

	@Override
	public <ObjectType extends Object> List<ObjectType> getChildrenAsJavaType(Class<ObjectType> javaType)
	{
		assert javaType != null;

		DLType instanceType = getCore().getType(javaType).orElseThrow();

		List<ObjectType> result = new ArrayList<>(children.size());

		for (DLInstance child : getChildren(instanceType)) {

			Object childJavaObject = child.toJavaObject();

			result.add((ObjectType) childJavaObject);
		}

		return result;
	}

	@Override
	public List getChildrenAsJavaType()
	{
		List result = new ArrayList<>(children.size());

		for (DLInstance child : children.list()) {

			result.add(child.toJavaObject());
		}

		return result;
	}

	@Override
	public Object getChildAsJavaObject(int index)
	{
		return getChild(index).toJavaObject();
	}

	@Override
	public int getChildCount()
	{
		return children.size();
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		if (type != null) {
			builder.append(type.getCanonicalName());
		}

		if (name != null) {
			builder.append(" ");
			builder.append(name);
		}

		/*for (DLAnnotation annotation : annotations) {
			builder.append(" ");
			builder.append(annotation.toString());
		}*/
		return builder.toString();
	}
}
