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

/**
 *
 * @author Benjamin Schiller
 */
public class RangeDLAnnotation extends AbstractDLAnnotation
{

	public final static String DEFAULT_SYMBOL = "range";

	/*
	private static class RangeDLInstanceValidator implements DLInstanceValidator, DLAttributeValidator
	{

		private final String name;
		private final double min;
		private final double max;

		RangeDLInstanceValidator(String name, double min, double max)
		{
			assert name != null;
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

			// make sure its a Number
			if (!(val instanceof Number)) {
				throw new InvalidAnnotation("Attribute has to be of type Number");
			}

			double doubleVal = ((Number) val).doubleValue();

			if (doubleVal < min) {
				throw new InvalidAnnotation("Attribute '" + name + "' has to be min " + min + " but is " + doubleVal);
			}

			if (doubleVal > max) {
				throw new InvalidAnnotation("Attribute '" + name + "' has to be max " + max + " but is " + doubleVal);
			}
		}
	}

	public RangeDLAnnotation()
	{
		this(DEFAULT_SYMBOL);
	}

	public RangeDLAnnotation(String name)
	{
		super(name);
	}

	@Override
	public void bindToAttribute(DLCore core, DLType type, DLAttribute attribute, Object... parameters) throws InvalidAnnotation
	{
		assert type != null;
		assert attribute != null;

		parameters = validateParameters(parameters, new Class[]{Double.class, Double.class});

		double min = (Double) parameters[0];
		double max = (Double) parameters[1];

		RangeDLInstanceValidator validator = new RangeDLInstanceValidator(attribute.getName(), min, max);
		((DefaultDLType) type).addInstanceValidator(validator);

		if (attribute instanceof DefaultDLAttribute) {
			((DefaultDLAttribute) attribute).addValidator(validator);
		}
	}
	 */
}
