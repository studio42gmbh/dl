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
package de.s42.dl.annotations.strings;

import de.s42.dl.annotations.AbstractDLAnnotation;

/**
 *
 * @author Benjamin Schiller
 */
public class LengthDLAnnotation extends AbstractDLAnnotation
{

	public final static String DEFAULT_SYMBOL = "length";

	/*
	private static class LengthDLInstanceValidator implements DLInstanceValidator, DLAttributeValidator
	{

		private final String name;
		private final int min;
		private final int max;

		LengthDLInstanceValidator(String name, int min, int max)
		{
			assert name != null;
			assert min >= 0;
			assert max > min;

			this.name = name;
			this.min = min;
			this.max = max;
		}

		@Override
		public void validate(DLAttribute attribute) throws InvalidAttribute
		{
			assert attribute != null;

			try {
				validateValue(attribute.getDefaultValue());
			} catch (InvalidAnnotation ex) {
				throw new InvalidAttribute(ex);
			}
		}

		@Override
		public void validate(DLInstance instance) throws InvalidInstance
		{
			assert instance != null;

			try {
				validateValue(instance.get(name));
			} catch (InvalidAnnotation ex) {
				throw new InvalidInstance(ex);
			}
		}

		protected void validateValue(Object val) throws InvalidAnnotation
		{
			// allow to have null values
			if (val == null) {
				return;
			}

			// make sure its a string
			if (!(val instanceof String)) {
				throw new InvalidAnnotation("Attribute has to be of type String");
			}

			int length = ((String) val).length();

			if (length < min) {
				throw new InvalidAnnotation("Attribute value '" + name + "' has to be min length " + min + " but is " + length);
			}

			if (length > max) {
				throw new InvalidAnnotation("Attribute value '" + name + "' has to be max length " + max + " but is " + length);
			}
		}
	}

	public LengthDLAnnotation()
	{
		this(DEFAULT_SYMBOL);
	}

	public LengthDLAnnotation(String name)
	{
		super(name);
	}

	@Override
	public void bindToAttribute(DLCore core, DLType type, DLAttribute attribute, Object... parameters) throws InvalidAnnotation
	{
		assert type != null;
		assert attribute != null;

		parameters = validateParameters(parameters, new Class[]{Integer.class, Integer.class});

		int min = (Integer) parameters[0];
		int max = (Integer) parameters[1];

		LengthDLInstanceValidator validator = new LengthDLInstanceValidator(attribute.getName(), min, max);
		((DefaultDLType) type).addInstanceValidator(validator);

		if (attribute instanceof DefaultDLAttribute) {
			((DefaultDLAttribute) attribute).addValidator(validator);
		}
	}
	 */
}
