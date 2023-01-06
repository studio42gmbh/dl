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
package de.s42.dl.annotations.attributes;

import de.s42.base.validation.IsSymbol;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.*;
import de.s42.dl.annotations.AbstractDLAnnotation;
import de.s42.dl.annotations.DLAnnotationParameter;
import de.s42.dl.attributes.DefaultDLAttribute;
import de.s42.dl.types.DefaultDLType;
import de.s42.dl.validation.DLAttributeValidator;
import de.s42.dl.validation.DLInstanceValidator;
import static de.s42.dl.validation.DefaultValidationCode.InvalidComparison;
import de.s42.dl.validation.ValidationResult;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 *
 * @author Benjamin Schiller
 * @param <DataType>
 * @param <DLAnnotationType>
 */
public abstract class AbstractComparisonDLAnnotation<DataType, DLAnnotationType extends DLAnnotation> extends AbstractDLAnnotation<DLAnnotationType>
{

	@DLAnnotationParameter(ordinal = 0, required = true, validation = IsSymbol.class)
	protected String other;
	
	private class ComparisonDLInstanceValidator implements DLInstanceValidator, DLAttributeValidator
	{

		private final String name;
		private final BiFunction<DataType, DataType, Boolean> comparator;
		private final BiFunction<DataType, DataType, String> errorMessage;

		ComparisonDLInstanceValidator(String name, BiFunction<DataType, DataType, Boolean> comparator, BiFunction<DataType, DataType, String> errorMessage)
		{
			assert name != null;
			assert comparator != null;
			assert errorMessage != null;

			this.name = name;
			this.comparator = comparator;
			this.errorMessage = errorMessage;
		}

		@Override
		public boolean validate(DLInstance instance, ValidationResult result)
		{
			assert instance != null;

			return validateValue(instance, instance.get(name), instance.get(other), result);
		}

		@Override
		public boolean validate(DLAttribute attribute, ValidationResult result)
		{
			assert attribute != null;

			// nothing to do as it can not be validated in a per attribute base
			return true;
		}

		protected boolean validateValue(Object source, Object val, Object valRef, ValidationResult result)
		{
			// allow to have null values
			if (val == null) {
				return true;
			}

			if (valRef == null) {
				return true;
			}

			if (!comparator.apply((DataType) val, (DataType) valRef)) {
				result.addError(InvalidComparison.toString(), errorMessage.apply((DataType) val, (DataType) valRef), source);
				return false;
			}
			
			return true;
		}
	}

	protected abstract String errorMessage(DataType val, DataType refVal);

	protected abstract boolean compare(DataType val, DataType refVal);

	@Override
	public void bindToAttribute(DLAttribute attribute) throws InvalidAnnotation
	{
		assert attribute != null;
		
		container = attribute;
		container.addAnnotation(this);

		ComparisonDLInstanceValidator validator = new ComparisonDLInstanceValidator(
			attribute.getName(),
			(val, refVal) -> {
				return compare(val, refVal);
			},
			(val, refVal) -> {
				return errorMessage(val, refVal);
			});
		((DefaultDLType) attribute.getContainer()).addInstanceValidator(validator);

		if (attribute instanceof DefaultDLAttribute) {
			((DefaultDLAttribute) attribute).addValidator(validator);
		}
	}

	public String getOther()
	{
		return other;
	}

	public void setOther(String other)
	{
		this.other = other;
	}

	@Override
	public int hashCode()
	{
		int hash = super.hashCode();
		hash = 67 * hash + Objects.hashCode(this.other);
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}		
		if (!super.equals(obj)) {
			return false;
		}		
		final AbstractComparisonDLAnnotation<?, ?> otherObj = (AbstractComparisonDLAnnotation<?, ?>) obj;
		return Objects.equals(this.other, otherObj.other);
	}
	
	
}
