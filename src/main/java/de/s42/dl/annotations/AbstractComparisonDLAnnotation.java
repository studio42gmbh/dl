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

import de.s42.base.validation.ValidationHelper;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.*;
import de.s42.dl.DLAnnotated.DLMappedAnnotation;
import de.s42.dl.attributes.DefaultDLAttribute;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.exceptions.InvalidValue;
import de.s42.dl.parameters.NamedParameter;
import de.s42.dl.parameters.NamedParameters;
import de.s42.dl.types.DefaultDLType;
import java.util.function.BiFunction;

/**
 *
 * @author Benjamin Schiller
 * @param <DataType>
 */
public abstract class AbstractComparisonDLAnnotation<DataType> extends AbstractDLAnnotation
{

	/* OTHER */
	public final static NamedParameter OTHER = new NamedParameter(
		"other",
		String.class,
		null,
		ValidationHelper::isString
	);

	/* PARAMETERS */
	public final static NamedParameters PARAMETERS = new NamedParameters(OTHER);

	private class ComparisonDLInstanceValidator implements DLInstanceValidator, DLAttributeValidator
	{

		private final String nameRef;
		private final String name;
		private final BiFunction<DataType, DataType, Boolean> comparator;
		private final BiFunction<DataType, DataType, String> errorMessage;

		ComparisonDLInstanceValidator(String name, String nameRef, BiFunction<DataType, DataType, Boolean> comparator, BiFunction<DataType, DataType, String> errorMessage)
		{
			assert name != null;
			assert nameRef != null;
			assert comparator != null;
			assert errorMessage != null;

			this.name = name;
			this.nameRef = nameRef;
			this.comparator = comparator;
			this.errorMessage = errorMessage;
		}

		@Override
		public void validate(DLInstance instance) throws InvalidInstance
		{
			assert instance != null;

			try {
				validateValue(instance.get(name), instance.get(nameRef));
			} catch (InvalidValue ex) {
				throw new InvalidInstance(ex);
			}
		}

		@Override
		public void validate(DLAttribute attribute)
		{
			assert attribute != null;

			// nothing to do as it can not be validated in a per attribute base
		}

		protected void validateValue(Object val, Object valRef) throws InvalidValue
		{
			// allow to have null values
			if (val == null) {
				return;
			}

			if (valRef == null) {
				return;
			}

			if (!comparator.apply((DataType) val, (DataType) valRef)) {
				throw new InvalidValue(errorMessage.apply((DataType) val, (DataType) valRef));
			}
		}
	}

	protected abstract String errorMessage(DataType val, DataType refVal);

	protected abstract boolean compare(DataType val, DataType refVal);

	public AbstractComparisonDLAnnotation(String name)
	{
		super(name, PARAMETERS);
	}

	public String getOther(DLMappedAnnotation mappedAnnotation) throws InvalidAnnotation
	{
		return OTHER.get(mappedAnnotation.getParameters());
	}

	@Override
	public void bindToAttribute(DLCore core, DLType type, DLAttribute attribute, Object... parameters) throws InvalidAnnotation
	{
		assert type != null;
		assert attribute != null;

		if (!isValidFlatParameters(parameters)) {
			throw new InvalidAnnotation("flat parameters are not valid");
		}

		String nameRef = OTHER.get(parameters);

		ComparisonDLInstanceValidator validator = new ComparisonDLInstanceValidator(
			attribute.getName(),
			nameRef,
			(val, refVal) -> {
				return compare(val, refVal);
			},
			(val, refVal) -> {
				return errorMessage(val, refVal);
			});
		((DefaultDLType) type).addInstanceValidator(validator);

		if (attribute instanceof DefaultDLAttribute) {
			((DefaultDLAttribute) attribute).addValidator(validator);
		}
	}
}
