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
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidType;
import de.s42.dl.validation.DLInstanceValidator;
import de.s42.dl.validation.DLTypeValidator;
import de.s42.dl.validation.DLValidatable;
import de.s42.dl.validation.ValidationResult;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author Benjamin Schiller
 */
// @todo https://github.com/studio42gmbh/dl/issues/23 DLType/DefaultDLType Improve and sharpen definition of complex and simple types - does it need further distinction?
public interface DLType extends DLEntity, DLAnnotated, DLValidatable
{

	DLCore getCore();

	String getCanonicalName();

	String getShortestName();
	
	Class getJavaDataType();

	// VALIDATION
	boolean validateInstance(DLInstance instance, ValidationResult result);

	boolean addValidator(DLTypeValidator validator);

	boolean addInstanceValidator(DLInstanceValidator validator);

	List<DLTypeValidator> getValidators();

	List<DLInstanceValidator> getInstanceValidators();

	// DATA ACCESS
	Object read(Object... sources) throws DLException;

	Object write(Object data) throws DLException;

	void setAttributeFromValue(DLInstance instance, String name, Object value) throws DLException;

	boolean canRead();

	// CONVERSION
	DLInstance fromJavaObject(Object object) throws DLException;

	Object createJavaInstance() throws DLException;

	// IDENTITY
	boolean isDerivedTypeOf(DLType other);

	boolean isAssignableFrom(DLType other);

	// PARENTS
	List<DLType> getOwnParents();

	/**
	 * Shall return all distinct parents (deep)
	 *
	 * @return
	 */
	List<DLType> getParents();

	boolean hasParents();

	boolean hasParent(DLType parent);

	boolean hasOwnParent(DLType parent);

	// CONTAINED
	List<DLType> getOwnContainedTypes();

	List<DLType> getContainedTypes();

	boolean hasOwnContainedTypes();

	boolean hasContainedTypes();

	boolean mayContainType(DLType type);

	// ATTRIBUTES
	void addAttribute(DLAttribute attribute) throws InvalidType;

	Set<DLAttribute> getOwnAttributes();

	Set<DLAttribute> getAttributes();

	Set<String> getAttributeNames();

	boolean hasAttribute(String name);

	Optional<DLAttribute> getAttribute(String name);

	boolean hasOwnAttributes();

	boolean hasAttributes();

	boolean isDynamic();

	// GENERIC
	List<DLType> getGenericTypes();

	boolean isGenericType();

	boolean isAllowGenericTypes();

	// FLAGS
	boolean isComplexType();

	boolean isSimpleType();

	boolean isAbstract();

	boolean isFinal();

	boolean isDeclaration();
}
