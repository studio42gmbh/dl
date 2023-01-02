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
import de.s42.dl.attributes.DefaultDLAttribute;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.types.DefaultDLType;
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
public class RangeDLAnnotation extends AbstractDLContract<RequiredDLAnnotation>
{

	public final static String DEFAULT_SYMBOL = "range";

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.FIELD})
	@DLAnnotationType(RangeDLAnnotation.class)
	public static @interface range
	{

		public int min() default Integer.MIN_VALUE;

		public int max() default Integer.MAX_VALUE;
	}

	@DLAnnotationParameter(ordinal = 0, defaultValue = "-2147483648")
	protected int min = Integer.MIN_VALUE;

	@DLAnnotationParameter(ordinal = 1, defaultValue = "2147483647")
	protected int max = Integer.MAX_VALUE;

	private String attributeName;

	@Override
	public boolean validate(DLAttribute attribute, ValidationResult result)
	{
		return validateValue(attribute.getDefaultValue(), result);
	}

	@Override
	public boolean validate(DLInstance instance, ValidationResult result)
	{
		return validateValue(instance.get(name), result);
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
			result.addError(InvalidValueType.toString(), "Attribute '" + name + "' has to be min " + min + " but is " + doubleVal);
			return false;
		}

		if (doubleVal > max) {
			result.addError(InvalidValueType.toString(), "Attribute '" + name + "' has to be max " + max + " but is " + doubleVal);
			return false;
		}

		return true;
	}

	@Override
	public void bindToAttribute(DLAttribute attribute) throws InvalidAnnotation
	{
		assert attribute != null;

		this.attributeName = attribute.getName();

		((DefaultDLType) attribute.getContainer()).addInstanceValidator(this);
		((DefaultDLAttribute) attribute).addValidator(this);
	}
}
