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

import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.exceptions.InvalidType;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author Benjamin Schiller
 */
// @todo https://github.com/studio42gmbh/dl/issues/23 DLType/DefaultDLType Improve and sharpen definition of complex and simple types - does it need further distinction?
public interface DLType extends DLEntity, DLAnnotated
{

	public void validate() throws InvalidType;

	public void validateInstance(DLInstance instance) throws InvalidInstance;

	public Object read(Object... sources) throws DLException;

	public Object write(Object data) throws DLException;
	
	public DLInstance fromJavaObject(DLCore core, Object object) throws DLException;
	
	public void setAttributeFromValue(DLCore core, DLInstance instance, String name, Object value) throws DLException;

	public Class getJavaDataType();
	
	public <ObjectType> ObjectType createJavaInstance() throws DLException;

	public String getCanonicalName();

	public boolean isDerivedTypeOf(DLType other);

	public boolean isAssignableFrom(DLType other);

	public List<DLType> getOwnParents();

	public List<DLType> getParents();

	public boolean hasParents();

	public boolean hasParent(DLType parent);

	public boolean hasOwnParent(DLType parent);

	public List<DLType> getOwnContainedTypes();

	public List<DLType> getContainedTypes();
	
	public boolean hasOwnContainedTypes();
	
	public boolean hasContainedTypes();

	public boolean mayContainSpecificType(DLType type);

	public boolean mayContainType(DLType type);

	public Set<DLAttribute> getOwnAttributes();

	public Set<DLAttribute> getAttributes();

	public Set<String> getAttributeNames();

	public boolean hasAttribute(String name);

	public Optional<DLAttribute> getAttribute(String name);

	public boolean hasOwnAttributes();
	
	public boolean hasAttributes();

	public boolean isAllowDynamicAttributes();

	public List<DLType> getGenericTypes();

	public boolean isGenericType();

	public boolean isAllowGenericTypes();

	public boolean isComplexType();

	public boolean isSimpleType();

	public boolean isAbstract();

	public boolean isFinal();
}
