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
import de.s42.dl.DLAnnotated.DLMappedAnnotation;
import de.s42.dl.exceptions.InvalidAttribute;
import de.s42.dl.exceptions.InvalidInstance;
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
public class DefaultDLInstance implements DLInstance
{

	private final static Logger log = LogManager.getLogger(DefaultDLInstance.class.getName());

	// one DLInstance can just have one Java Object representation
	protected Object javaObject;
	protected String name;
	protected DLType type;
	protected final MappedList<String, Object> attributes = new MappedList<>();
	protected final MappedList<String, DLInstance> children = new MappedList<>();
	protected final List<DLMappedAnnotation> annotations = new ArrayList<>();

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
	public void validate() throws InvalidInstance
	{
		if (getType() != null) {
			getType().validateInstance(this);
		}
	}

	@Override
	public Set<String> getAttributeNames()
	{
		if (getType() != null) {

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
		}
		else {
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
		Set<String> attributeNames = getType().getAttributeNames();

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
		return attributes.contains(key);
	}

	@Override
	public <ReturnType> ReturnType get(String key)
	{
		assert key != null;

		Optional attribute = attributes.get(key);

		if (attribute.isPresent()) {
			return (ReturnType) attribute.orElseThrow();
		} else if (getType() != null && getType().hasAttribute(key)) {
			return (ReturnType) getType().getAttribute(key).orElseThrow().getDefaultValue();
		}
		return null;
	}

	@Override
	public <InstanceType extends DLInstance> InstanceType getInstance(String key)
	{
		assert key != null;

		return (InstanceType) get(key);
	}

	@Override
	public <JavaType> JavaType getInstanceAsJavaObject(String key, DLCore core) throws InvalidAttribute
	{
		assert key != null;

		DefaultDLInstance instance = get(key);

		if (instance == null) {
			throw new InvalidAttribute("Instance attribute '" + key + "' is not mapped.");
		}

		return instance.toJavaObject(core);
	}

	@Override
	public String getString(String key)
	{
		assert key != null;

		return get(key);
	}

	@Override
	public Number getNumber(String key)
	{
		assert key != null;

		try {
			return get(key);
		} catch (ClassCastException ex) {
			throw new RuntimeException("Error getting " + key + " as number as its class is " + key.getClass().getCanonicalName());
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

		return get(key);
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
	public <JavaType> Optional<JavaType> getChildAsJavaObject(String name, DLCore core)
	{
		assert name != null;

		Optional<DLInstance> child = getChild(name);

		if (child.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(child.get().toJavaObject(core));
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
	public Optional<DLInstance> getChild(DLType type)
	{
		assert type != null;

		for (DLInstance child : children.list()) {
			if (type.isAssignableFrom(child.getType())) {
				return Optional.of(child);
			}
		}

		return Optional.empty();
	}

	@Override
	public <JavaType> Optional<JavaType> getChildAsJavaObject(DLType type, DLCore core)
	{
		assert type != null;

		Optional<DLInstance> child = getChild(type);

		if (child.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(child.get().toJavaObject(core));
	}

	@Override
	public List<DLInstance> getChildren(DLType type)
	{
		assert type != null;

		List<DLInstance> result = new ArrayList<>(children.size());

		for (DLInstance child : children.list()) {
			if (type.isAssignableFrom(child.getType())) {
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
			traverseChildren(child, callback);
			callback.accept(child);
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
	public boolean hasName(String name)
	{
		assert name != null;

		return this.name.equals(name);
	}

	@Override
	public boolean hasChildren()
	{
		return !children.isEmpty();
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
	public <ObjectType> Optional<ObjectType> resolvePath(String path)
	{
		assert path != null;

		String[] pathSegments = path.split("\\.");
		DefaultDLInstance current = this;

		for (int i = 0; i < pathSegments.length; ++i) {
			String pathSegment = pathSegments[i];
			Object child = resolveChildOrAttribute(current, pathSegment);

			if (child == null) {
				//throw new InvalidValue("Path " + path + " could not get resolved for " + pathSegment);
				return Optional.empty();
			}

			if (child instanceof DefaultDLInstance) {
				current = (DefaultDLInstance) child;
			} else if (i < pathSegments.length - 1) {
				//throw new InvalidValue("Path " + path + " could not get resolved for " + pathSegment);
				return Optional.empty();
			} else {
				return Optional.of((ObjectType) child);
			}
		}

		// https://github.com/studio42gmbh/dl/issues/13 Unwrap simple instances
		// @improvement this unwrapping should be done more generic if possible
		if (current instanceof SimpleTypeDLInstance) {
			return Optional.ofNullable((ObjectType) ((SimpleTypeDLInstance) current).getData());
		}

		return Optional.of((ObjectType) current);
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
	public boolean hasName()
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
	public synchronized <ObjectType> ObjectType toJavaObject(DLCore core)
	{
		assert core != null;

		try {
			if (javaObject == null) {
				javaObject = core.convertFromInstance(this);
			}
		} catch (Throwable ex) {
			throw new RuntimeException("Error converting instance '" + name + "' - " + ex.getMessage(), ex);
		}

		return (ObjectType) javaObject;
	}

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
	public boolean hasAnnotations()
	{
		return !annotations.isEmpty();
	}

	@Override
	public <JavaType> List<JavaType> getChildrenAsJavaType(Class<? extends JavaType> javaType, DLCore core)
	{
		List<JavaType> result = new ArrayList<>(children.size());

		for (DLInstance child : children.list()) {

			Object childJavaObject = child.toJavaObject(core);

			if (javaType.isAssignableFrom(childJavaObject.getClass())) {
				result.add((JavaType) childJavaObject);
			}
		}

		return result;
	}

	@Override
	public <JavaType> List<JavaType> getChildrenAsJavaType(DLCore core)
	{
		List<JavaType> result = new ArrayList<>(children.size());

		for (DLInstance child : children.list()) {

			result.add(child.toJavaObject(core));
		}

		return result;
	}

	@Override
	public <JavaType> JavaType getChildAsJavaObject(int index, DLCore core)
	{
		return getChild(index).toJavaObject(core);
	}

	@Override
	public int getChildCount()
	{
		return children.size();
	}

	@Override
	public String toString()
	{
		if (getType() != null) {
			return getType().getCanonicalName() + " " + getName();
		} else {
			return "Instance " + getName();
		}
	}
}
