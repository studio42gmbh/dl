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
package de.s42.dl.annotations.numbers;

import de.s42.dl.DLAttribute;
import de.s42.dl.DLInstance;
import de.s42.dl.DLType;
import de.s42.dl.annotations.AbstractDLContract;
import de.s42.dl.annotations.DLAnnotationType;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidAnnotation;
import static de.s42.dl.validation.DefaultValidationCode.InvalidValueType;
import de.s42.dl.validation.ValidationResult;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Benjamin Schiller
 */
public class EvenDLAnnotation extends AbstractDLContract<EvenDLAnnotation>
{

	private final static Logger log = LogManager.getLogger(EvenDLAnnotation.class.getName());

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.FIELD})
	@DLAnnotationType(EvenDLAnnotation.class)
	public static @interface even
	{
	}

	@Override
	public boolean validate(DLAttribute attribute, ValidationResult result)
	{
		return validateValue(attribute.getDefaultValue(), result);
	}

	@Override
	public boolean validate(DLInstance instance, String attributeName, ValidationResult result)
	{
		return validateValue(instance.getAttribute(attributeName).orElse(null), result);
	}

	@Override
	public boolean canValidateAttribute()
	{
		return true;
	}
	
	@Override
	public boolean validate(DLType type, Object value, ValidationResult result)
	{
		if (value == null) {
			return true;
		}

		if (!value.getClass().isArray()) {
			result.addError(InvalidValueType.toString(), "Value has to be of type Array");
			return false;
		}

		boolean valid = true;

		// Validate each val in the array
		Object[] values = (Object[]) value;
		for (Object val : values) {
			valid &= validateValue(val, result);
		}

		return valid;
	}

	@Override
	public boolean canValidateTypeRead()
	{
		return true;
	}

	@Override
	public boolean canValidateInstance()
	{
		return true;
	}
	
	protected boolean validateValue(Object val, ValidationResult result)
	{
		// allow to have null values
		if (val == null) {
			return true;
		}

		// make sure its a Number
		if (!(val instanceof Number)) {
			result.addError(InvalidValueType.toString(), "Value has to be of type Number");
			return false;
		}

		long longVal = ((Number) val).longValue();

		if (longVal % 2 != 0) {
			result.addError(InvalidValueType.toString(),
				"Value has to be even but is " + longVal
			);
			return false;
		}

		return true;
	}
}
