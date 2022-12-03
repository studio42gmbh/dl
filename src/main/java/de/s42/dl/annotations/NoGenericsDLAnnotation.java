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
import de.s42.dl.DLType;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.types.DefaultDLType;
import de.s42.dl.validation.DLTypeValidator;
import static de.s42.dl.validation.DefaultValidationCode.InvalidGenericTypes;
import de.s42.dl.validation.ValidationResult;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Benjamin Schiller
 */
public class NoGenericsDLAnnotation extends AbstractDLAnnotation
{

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.FIELD})
	@DLAnnotationType(NoGenericsDLAnnotation.class)
	public static @interface noGenerics
	{
	}

	private static class NoGenericsValidator implements DLTypeValidator
	{

		@Override
		public boolean validate(DLType type, ValidationResult result)
		{
			assert type != null;
			
			boolean valid = true;

			for (DLAttribute attribute : type.getAttributes()) {
				if (attribute.getType().isGenericType()) {
					result.addError(InvalidGenericTypes.toString(), "Type " + type + " may not contain generics, but " + attribute + " has", type);
					valid = false;
				}
			}
			
			return valid;
		}
	}

	@Override
	public void bindToType(DLCore core, DLType type) throws DLException
	{
		assert type != null;

		((DefaultDLType) type).addValidator(new NoGenericsValidator());
	}
}
