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
import de.s42.dl.DLType;
import de.s42.dl.annotations.AbstractDLContract;
import de.s42.dl.annotations.DLAnnotated;
import de.s42.dl.annotations.DLAnnotationParameter;
import de.s42.dl.annotations.DLAnnotationType;
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
public class RangeDLAnnotation extends AbstractDLContract<RangeDLAnnotation>
{

	private final static Logger log = LogManager.getLogger(RangeDLAnnotation.class.getName());

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.FIELD})
	@DLAnnotationType(RangeDLAnnotation.class)
	public static @interface range
	{

		public double min() default -Double.MAX_VALUE;

		public double max() default Double.MAX_VALUE;
	}

	@DLAnnotationParameter(ordinal = 0, defaultValue = "-1.7976931348623157E308")
	protected double min = -Double.MAX_VALUE;

	@DLAnnotationParameter(ordinal = 1, defaultValue = "1.7976931348623157E308")
	protected double max = Double.MAX_VALUE;

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
			return result.isValid();
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

	protected boolean validateValue(Object val, ValidationResult result, DLAnnotated source)
	{
		assert source != null;
		assert result != null;
		
		// allow to have null values
		if (val == null) {
			return result.isValid();
		}

		// make sure its a Number
		if (!(val instanceof Number)) {
			result.addError(InvalidValueType.toString(), "Attribute has to be of type Number in @" + getName(), source);
			return result.isValid();
		}

		double doubleVal = ((Number) val).doubleValue();

		if (doubleVal < min) {
			result.addError(InvalidValueType.toString(),
				"Value has to be min " + min + " but is " + doubleVal + " in @" + getName(),
				source
			);
		} else if (doubleVal > max) {
			result.addError(InvalidValueType.toString(),
				"Value has to be max " + max + " but is " + doubleVal + " in @" + getName(),
				source
			);
		}

		return result.isValid();
	}

	public double getMin()
	{
		return min;
	}

	public void setMin(double min)
	{
		this.min = min;
	}

	public double getMax()
	{
		return max;
	}

	public void setMax(double max)
	{
		this.max = max;
	}
}
