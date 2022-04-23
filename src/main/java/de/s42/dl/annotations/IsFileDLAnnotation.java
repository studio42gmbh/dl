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
import de.s42.dl.exceptions.InvalidAttribute;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.types.DefaultDLType;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author Benjamin Schiller
 */
public class IsFileDLAnnotation extends AbstractDLAnnotation
{

	protected static class IsFileDLInstanceValidator implements DLValidator
	{

		protected final String attributeName;

		protected IsFileDLInstanceValidator(String attributeName)
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
			} catch (InvalidAnnotation ex) {
				throw new InvalidAttribute(ex);
			}
		}

		@Override
		public void validate(DLInstance instance) throws InvalidInstance
		{
			assert instance != null;

			try {
				validateValue(instance.get(attributeName));
			} catch (InvalidAnnotation ex) {
				throw new InvalidInstance(ex);
			}
		}

		protected void validateValue(Object val) throws InvalidAnnotation
		{
			if (val == null) {
				return;
			}

			if (!(val instanceof Path)) {
				throw new InvalidAnnotation("Values of " + DEFAULT_SYMBOL + " annotation are required to be of class Path but is " + val.getClass());
			}

			if (!Files.isRegularFile((Path) val)) {
				throw new InvalidAnnotation("Path " + ((Path) val).toAbsolutePath() + " is not a valid file");
			}
		}
	}

	public final static String DEFAULT_SYMBOL = "isFile";

	public IsFileDLAnnotation()
	{
		this(DEFAULT_SYMBOL);
	}

	public IsFileDLAnnotation(String name)
	{
		super(name);
	}

	@Override
	public void bindToAttribute(DLCore core, DLType type, DLAttribute attribute, Object... parameters) throws InvalidAnnotation
	{
		assert type != null;
		assert attribute != null;

		validateParameters(parameters, null);

		IsFileDLInstanceValidator validator = new IsFileDLInstanceValidator(attribute.getName());

		if (attribute instanceof DefaultDLAttribute) {
			((DefaultDLAttribute) attribute).addValidator(validator);
		}

		((DefaultDLType) type).addInstanceValidator(validator);
	}
}
