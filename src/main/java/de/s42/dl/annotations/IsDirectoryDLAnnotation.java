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

import de.s42.dl.*;
import de.s42.dl.attributes.DefaultDLAttribute;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.exceptions.InvalidAttribute;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.exceptions.InvalidValue;
import de.s42.dl.types.DefaultDLType;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author Benjamin Schiller
 */
public class IsDirectoryDLAnnotation extends AbstractDLAnnotation
{

	private static class IsDirectoryDLInstanceValidator extends AbstractDLValidator
	{

		private final String attributeName;

		IsDirectoryDLInstanceValidator(String attributeName)
		{
			assert attributeName != null;

			this.attributeName = attributeName;
		}

		@Override
		public void validate(DLAttribute attribute) throws InvalidAttribute
		{
			assert attribute != null;

			try {
				validateValue(attribute.getDefaultValue());
			} catch (InvalidValue ex) {
				throw new InvalidAttribute(ex);
			}
		}

		@Override
		public void validate(DLInstance instance) throws InvalidInstance
		{
			assert instance != null;

			try {
				validateValue(instance.get(attributeName));
			} catch (InvalidValue ex) {
				throw new InvalidInstance(ex);
			}
		}

		protected void validateValue(Object val) throws InvalidValue
		{
			if (val == null) {
				return;
			}

			if (!(val instanceof Path)) {
				throw new InvalidValue("Values of " + DEFAULT_SYMBOL + " annotation are required to be of class Path");
			}

			if (!Files.isDirectory((Path) val)) {
				throw new InvalidValue("Path " + ((Path) val).toAbsolutePath() + " is not a valid directory");
			}
		}
	}

	public final static String DEFAULT_SYMBOL = "isDirectory";

	public IsDirectoryDLAnnotation()
	{
		this(DEFAULT_SYMBOL);
	}

	public IsDirectoryDLAnnotation(String name)
	{
		super(name);
	}

	@Override
	public void bindToAttribute(DLCore core, DLType type, DLAttribute attribute, Object... parameters) throws InvalidAnnotation
	{
		assert type != null;
		assert attribute != null;

		validateParameters(parameters, null);

		IsDirectoryDLInstanceValidator validator = new IsDirectoryDLInstanceValidator(attribute.getName());

		if (attribute instanceof DefaultDLAttribute) {
			((DefaultDLAttribute) attribute).addValidator(validator);
		}

		((DefaultDLType) type).addValidator(validator);
	}
}
