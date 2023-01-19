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
package de.s42.dl;

import de.s42.dl.annotations.DLAnnotated;
import de.s42.dl.exceptions.InvalidAttribute;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.validation.DLInstanceValidator;
import de.s42.dl.validation.DLValidatable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author Benjamin Schiller
 */
// @todo https://github.com/studio42gmbh/dl/issues/21 DLInstance rethink interface and implementation of getters - Optional? Performance, Security, Convenience
public interface DLInstance extends DLEntity, DLAnnotated, DLValidatable
{

	public boolean isNamed();

	default public boolean isUnnamed()
	{
		return !isNamed();
	}

	public DLType getType();
	
	default public DLCore getCore()
	{
		return getType().getCore();
	}

	// VALIDATION
	public boolean addValidator(DLInstanceValidator validator);

	public List<DLInstanceValidator> getValidators();

	// ATTRIBUTES
	public Set<String> getAttributeNames();

	public Optional<DLAttribute> getAttribute(String name);

	public Map<String, Object> getAttributes();

	public boolean hasDynamicAttributes();

	public boolean hasAttributes();

	public boolean hasAttribute(String name);

	// DATA ACCESS
	public void set(String key, Object value);

	public Object get(String key);

	public DLInstance getInstance(String key);

	public String getString(String key);

	public Number getNumber(String key);

	public short getShort(String key);

	public char getChar(String key);

	public byte getByte(String key);

	public int getInt(String key);

	public long getLong(String key);

	public float getFloat(String key);

	public double getDouble(String key);

	public boolean getBoolean(String key);

	// CONTAIN
	public DLInstance getChild(int index);

	public Optional<DLInstance> getChild(String name);

	public Optional<DLInstance> getChild(DLType type);

	public Optional<DLInstance> resolveChild(String path);

	public void addChild(DLInstance child) throws InvalidInstance;

	public void addChildren(Collection<DLInstance> children) throws InvalidInstance;

	public List<DLInstance> getChildren();

	public List<DLInstance> getChildren(DLType type);

	public boolean hasChild(String name);

	public boolean hasChildren();

	public int getChildCount();

	// CONVERSION
	public Object toJavaObject();

	public Object getInstanceAsJavaObject(String key) throws InvalidAttribute;

	public Object getChildAsJavaObject(int index);

	public Optional<?> getChildAsJavaObject(String name);

	public Optional<?> getChildAsJavaObject(DLType type);

	public List getChildrenAsJavaType(Class<?> javaType);

	public List getChildrenAsJavaType();
}
