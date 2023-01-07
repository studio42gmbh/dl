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
import de.s42.dl.DLAttribute;
import de.s42.dl.DLInstance;
import de.s42.dl.DLType;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidType;
import de.s42.dl.validation.DLTypeValidator;
import de.s42.dl.validation.ValidationResult;

/**
 *
 * @author Benjamin Schiller
 */
public abstract class SimpleDLType extends DefaultDLType
{

	public final static String ATTRIBUTE_VALUE = "value";

	public SimpleDLType(String name, Class javaType)
	{
		super(name, javaType);
	}

	@Override
	public DLInstance fromJavaObject(Object object) throws DLException
	{
		assert object != null;
		
		DLInstance instance = core.createInstance(this);

		instance.set(ATTRIBUTE_VALUE, read(object));

		return instance;
	}

	@Override
	public boolean isComplexType()
	{
		return false;
	}

	@Override
	public boolean isDynamic()
	{
		return false;
	}

	@Override
	public boolean isSimpleType()
	{
		return true;
	}

	@Override
	public boolean validate(ValidationResult result)
	{
		assert result != null;

		for (DLTypeValidator validator : getValidators()) {
			if (validator.canValidateType()) {
				validator.validate(this, result);
			}
		}

		for (DLType parent : getParents()) {
			for (DLTypeValidator validator : parent.getValidators()) {
				if (validator.canValidateType()) {
					validator.validate(this, result);
				}
			}
		}

		return result.isValid();
	}

	@Override
	public Object read(Object... sources) throws InvalidType
	{
		assert sources != null;

		Object[] data;
		try {
			data = ConversionHelper.convertArray(sources, new Class[]{getJavaDataType()});
		} catch (RuntimeException ex) {
			throw new InvalidType("Type '" + getCanonicalName() + "' could not convert read - " + ex.getMessage(), ex);
		}

		// Validate read
		ValidationResult result = new ValidationResult();
		if (!validateRead(data[0], result)) {
			throw new InvalidType("Type '" + getCanonicalName() + "' could not validate read - " + result.toMessage());
		}

		return data[0];
	}

	@Override
	public void setJavaType(Class javaType)
	{
		throw new UnsupportedOperationException("May not setJavaType");
	}

	@Override
	public void addAttribute(DLAttribute attribute)
	{
		throw new UnsupportedOperationException("May not addAttribute");
	}

	@Override
	public void setDynamic(boolean dynamic)
	{
		if (dynamic) {
			throw new UnsupportedOperationException("May not set dynamic");
		}
	}

	@Override
	public void setComplexType(boolean complexType)
	{
		if (complexType) {
			throw new UnsupportedOperationException("May not setComplexType");
		}
	}

	@Override
	public void setAttributeFromValue(DLInstance instance, String name, Object value) throws DLException
	{
		throw new UnsupportedOperationException("May not setAttributeFromValue");
	}
}
