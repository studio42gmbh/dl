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
package de.s42.dl.annotations;

import de.s42.dl.DLAttribute;
import de.s42.dl.DLType;
import de.s42.dl.validation.ValidationResult;
import de.s42.log.LogManager;
import de.s42.log.Logger;

/**
 *
 * @author Benjamin Schiller
 * @param <DLConceptType>
 */
public abstract class AbstractValueDLContract<DLConceptType extends AbstractValueDLContract> extends AbstractDLContract<DLConceptType>
{

	private final static Logger log = LogManager.getLogger(AbstractValueDLContract.class.getName());

	abstract protected boolean validateValue(Object value, ValidationResult result, DLAnnotated source);

	@Override
	public boolean canValidateAttribute()
	{
		return true;
	}

	@Override
	public boolean canValidateTypeRead()
	{
		return true;
	}

	@Override
	public boolean validate(DLAttribute attribute, ValidationResult result)
	{
		assert attribute != null;
		assert result != null;

		return validateValue(attribute.getDefaultValue(), result, attribute);
	}

	@Override
	public boolean validate(DLAttribute attribute, Object value, ValidationResult result)
	{
		assert attribute != null;
		assert result != null;

		return validateValue(value, result, attribute);
	}

	@Override
	public boolean validate(DLType type, Object value, ValidationResult result)
	{
		assert type != null;
		assert result != null;

		if (value == null) {
			return true;
		}

		if (value.getClass().isArray()) {

			// Validate each val in the array
			Object[] values = (Object[]) value;
			for (Object val : values) {
				validateValue(val, result, type);
			}

			return result.isValid();
		}

		return validateValue(value, result, type);
	}
}
