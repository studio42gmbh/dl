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

import de.s42.dl.annotations.AbstractValueDLContract;
import de.s42.dl.annotations.DLAnnotated;
import de.s42.dl.annotations.DLAnnotationType;
import static de.s42.dl.validation.DefaultValidationCode.InvalidValueType;
import de.s42.dl.validation.ValidationResult;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Benjamin Schiller
 */
public class EvenDLAnnotation extends AbstractValueDLContract<EvenDLAnnotation>
{

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.FIELD})
	@DLAnnotationType(EvenDLAnnotation.class)
	public static @interface even
	{
	}

	@Override
	protected boolean validateValue(Object value, ValidationResult result, DLAnnotated source)
	{
		assert source != null;
		assert result != null;

		// Allow to have null values
		if (value == null) {
			return result.isValid();
		}

		// Make sure its a Number
		if (!(value instanceof Number)) {
			result.addError(InvalidValueType.toString(), "Value has to be of type Number in @" + getName(), source);
			return result.isValid();
		}

		long longVal = ((Number) value).longValue();

		if (longVal % 2 != 0) {
			result.addError(InvalidValueType.toString(),
				"Value has to be even but is " + longVal + " in @" + getName(),
				source
			);
		}

		return result.isValid();
	}
}
