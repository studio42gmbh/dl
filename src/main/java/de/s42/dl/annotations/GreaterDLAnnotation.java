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

import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.*;
import de.s42.dl.attributes.DefaultDLAttribute;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.exceptions.InvalidValue;
import de.s42.dl.types.DefaultDLType;

/**
 *
 * @author Benjamin Schiller
 */
public class GreaterDLAnnotation extends AbstractDLAnnotation
{

	private static class GreaterDLInstanceValidator implements DLInstanceValidator, DLAttributeValidator
	{

		private final String nameRef;
		private final String name;

		GreaterDLInstanceValidator(String name, String nameRef)
		{
			assert name != null;
			assert nameRef != null;

			this.name = name;
			this.nameRef = nameRef;
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

			// make sure its a string
			if (!(val instanceof Number)) {
				throw new InvalidValue("Attribute has to be of type Number");
			}

			if (!(valRef instanceof Number)) {
				throw new InvalidValue("Attribute Ref has to be of type Number");
			}

			double doubleVal = ((Number) val).doubleValue();
			double doubleValRef = ((Number) valRef).doubleValue();

			if (doubleVal <= doubleValRef) {
				throw new InvalidValue(
					"Attribute '" + name + "' (" + doubleVal
					+ ") has to be greater than '" + nameRef + "' (" + doubleValRef
					+ ")");
			}
		}
	}

	public final static String DEFAULT_SYMBOL = "greater";

	public GreaterDLAnnotation()
	{
		this(DEFAULT_SYMBOL);
	}

	public GreaterDLAnnotation(String name)
	{
		super(name);
	}

	@Override
	public void bindToAttribute(DLCore core, DLType type, DLAttribute attribute, Object... parameters) throws InvalidAnnotation
	{
		assert type != null;
		assert attribute != null;

		parameters = validateParameters(parameters, new Class[]{String.class});

		String nameRef = (String) parameters[0];

		GreaterDLInstanceValidator validator = new GreaterDLInstanceValidator(attribute.getName(), nameRef);
		((DefaultDLType) type).addInstanceValidator(validator);

		if (attribute instanceof DefaultDLAttribute) {
			((DefaultDLAttribute) attribute).addValidator(validator);
		}
	}
}
