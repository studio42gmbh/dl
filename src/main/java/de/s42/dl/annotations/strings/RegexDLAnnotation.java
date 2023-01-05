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
public class RegexDLAnnotation extends AbstractDLAnnotation
{

	public final static String DEFAULT_SYMBOL = "regex";

	/*
	private static class RegexDLInstanceValidator implements DLInstanceValidator, DLAttributeValidator
	{

		private final String name;
		private final Pattern pattern;

		RegexDLInstanceValidator(String name, Pattern pattern)
		{
			assert name != null;
			assert pattern != null;

			this.name = name;
			this.pattern = pattern;
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

			String stringVal = (String) val;

			if (!pattern.matcher(stringVal).matches()) {
				throw new InvalidAnnotation("Attribute value '" + name + "' has to match patttern '" + pattern.pattern() + "' but is " + stringVal);
			}
		}
	}

	public RegexDLAnnotation()
	{
		this(DEFAULT_SYMBOL);
	}

	public RegexDLAnnotation(String name)
	{
		super(name);
	}

	@Override
	public void bindToAttribute(DLCore core, DLType type, DLAttribute attribute, Object... parameters) throws InvalidAnnotation
	{
		assert type != null;
		assert attribute != null;

		parameters = validateParameters(parameters, new Class[]{String.class});

		String regex = (String) parameters[0];

		RegexDLInstanceValidator validator = new RegexDLInstanceValidator(attribute.getName(), Pattern.compile(regex));
		((DefaultDLType) type).addInstanceValidator(validator);

		if (attribute instanceof DefaultDLAttribute) {
			((DefaultDLAttribute) attribute).addValidator(validator);
		}
	}
	 */
}
