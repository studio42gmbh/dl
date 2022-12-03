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
import de.s42.dl.DLCore;
import de.s42.dl.DLInstance;
import de.s42.dl.DLInstanceValidator;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.types.DefaultDLType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Benjamin Schiller
 */
public class RequiredDLAnnotation extends AbstractDLAnnotation
{

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.FIELD})
	@DLAnnotationType(RequiredDLAnnotation.class)
	public static @interface required
	{
	}

	private static class RequiredDLInstanceValidator implements DLInstanceValidator
	{

		private final DLAttribute attribute;

		RequiredDLInstanceValidator(DLAttribute attribute)
		{
			assert attribute != null;

			this.attribute = attribute;
		}

		@Override
		public void validate(DLInstance instance) throws InvalidInstance
		{
			assert instance != null;

			Object val = instance.get(attribute.getName());

			if (val == null) {
				throw new InvalidInstance("Attribute value '" + attribute.getName() + "' is required and may not be null");
			}
		}
	}

	@Override
	public void bindToAttribute(DLCore core, DLAttribute attribute) throws InvalidAnnotation
	{
		assert attribute != null;

		((DefaultDLType) attribute.getContainer()).addInstanceValidator(new RequiredDLInstanceValidator(attribute));
	}
}
