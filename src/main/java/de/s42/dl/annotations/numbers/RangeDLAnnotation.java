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
import de.s42.dl.DLInstance;
import de.s42.dl.DLType;
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

	private String attributeName;

	@Override
	public boolean validate(DLAttribute attribute, ValidationResult result)
	{
		return validateValue(attribute.getDefaultValue(), result);
	}

	@Override
	public boolean validate(DLInstance instance, ValidationResult result)
	{
		return validateValue(instance.get(attributeName), result);
	}

	@Override
	public boolean validate(DLType type, Object value, ValidationResult result)
	{
		if (value == null) {
			return true;
		}

		if (!value.getClass().isArray()) {
			result.addError(InvalidValueType.toString(), "Attribute has to be of type Array");
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

	protected boolean validateValue(Object val, ValidationResult result)
	{
		// allow to have null values
		if (val == null) {
			return true;
		}

		// make sure its a Number
		if (!(val instanceof Number)) {
			result.addError(InvalidValueType.toString(), "Attribute has to be of type Number");
			return false;
		}

		double doubleVal = ((Number) val).doubleValue();

		if (doubleVal < min) {
			result.addError(InvalidValueType.toString(),
				((attributeName != null) ? "Attribute '" + attributeName + "'" : "Value") + " has to be min " + min + " but is " + doubleVal
			);
			return false;
		}

		if (doubleVal > max) {
			result.addError(InvalidValueType.toString(),
				((attributeName != null) ? "Attribute '" + attributeName + "'" : "Value") + " has to be max " + max + " but is " + doubleVal
			);
			return false;
		}

		return true;
	}

	@Override
	public void bindToType(DLType type) throws DLException
	{
		assert type != null;

		type.addReadValidator(this);
	}

	@Override
	public void bindToAttribute(DLAttribute attribute) throws InvalidAnnotation
	{
		assert attribute != null;

		attributeName = attribute.getName();

		attribute.getContainer().addInstanceValidator(this);
		attribute.addValidator(this);
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
