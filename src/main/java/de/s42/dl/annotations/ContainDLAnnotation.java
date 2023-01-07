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

import de.s42.base.validation.IsSymbol;
import de.s42.dl.*;
import static de.s42.dl.validation.DefaultValidationCode.InvalidContain;
import de.s42.dl.validation.ValidationResult;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

/**
 *
 * @author Benjamin Schiller
 */
public class ContainDLAnnotation extends AbstractDLContract<ContainDLAnnotation>
{

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.TYPE})
	@DLAnnotationType(ContainDLAnnotation.class)
	public static @interface contain
	{

		public int min() default 0;

		public int max() default Integer.MAX_VALUE;

		public String contain();
	}

	@DLAnnotationParameter(ordinal = 0, required = true, validation = IsSymbol.class)
	protected String contain;

	@DLAnnotationParameter(ordinal = 1, defaultValue = "0")
	protected int min;

	@DLAnnotationParameter(ordinal = 2, defaultValue = "2147483647")
	protected int max = Integer.MAX_VALUE;

	@Override
	public boolean canValidateType()
	{
		return true;
	}

	@Override
	public boolean canValidateInstance()
	{
		return true;
	}
	
	/**
	 * Ensure type may generally contain the other type and the other type exists.
	 *
	 * @param type
	 * @param result
	 *
	 * @return
	 */
	@Override
	public boolean validate(DLType type, ValidationResult result)
	{
		assert type != null;
		assert result != null;

		Optional<DLType> optContainedType = validateAndGetContainedType(type, result);

		if (optContainedType.isEmpty()) {
			return false;
		}

		DLType containedType = optContainedType.orElseThrow();

		if (!type.mayContainType(containedType)) {
			result.addError(InvalidContain.toString(), "Type '" + type + "' may not contain '" + containedType + "' not found in core");
			return false;
		}

		return true;
	}

	@Override
	public boolean validate(DLInstance instance, ValidationResult result)
	{
		assert instance != null;
		assert result != null;

		DLType type = instance.getType();

		Optional<DLType> optContainedType = validateAndGetContainedType(type, result);

		if (optContainedType.isEmpty()) {
			return false;
		}

		DLType containedType = optContainedType.orElseThrow();

		int count = instance.getChildren(containedType).size();

		if (count < min || count > max) {
			result.addError(InvalidContain.toString(), "Instance has to contain type '" + containedType.getCanonicalName() + "' between " + min + " and " + max + " times, but is contained " + count + " times", instance);
			return false;
		}

		return true;
	}

	protected Optional<DLType> validateAndGetContainedType(DLType type, ValidationResult result)
	{
		assert type != null;
		assert result != null;

		Optional<DLType> optContainedType = type.getCore().getType(contain);

		if (optContainedType.isEmpty()) {
			result.addError(InvalidContain.toString(), "Type '" + contain + "' not found in core");
			return Optional.empty();
		}

		DLType containedType = optContainedType.orElseThrow();

		if (!type.mayContainType(containedType)) {
			result.addError(InvalidContain.toString(), "Type '" + type + "' may not contain '" + containedType + "' not found in core");
			return Optional.empty();
		}

		return Optional.of(containedType);
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public int getMin()
	{
		return min;
	}

	public void setMin(int min)
	{
		this.min = min;
	}

	public int getMax()
	{
		return max;
	}

	public void setMax(int max)
	{
		this.max = max;
	}

	public String getContain()
	{
		return contain;
	}

	public void setContain(String contain)
	{
		this.contain = contain;
	}
	//</editor-fold>
}
