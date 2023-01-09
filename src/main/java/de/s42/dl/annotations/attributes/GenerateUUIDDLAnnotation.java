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

import de.s42.dl.DLAttribute;
import de.s42.dl.DLInstance;
import de.s42.dl.annotations.AbstractDLContract;
import de.s42.dl.annotations.DLAnnotationType;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.validation.ValidationResult;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.UUID;

/**
 * ATTENTION: This annotation is experimental and currently sneaking into the validation phase.
 * I am considering to give such mutating annotations a special phase
 *
 * @author Benjamin Schiller
 */
public class GenerateUUIDDLAnnotation extends AbstractDLContract<GenerateUUIDDLAnnotation>
{

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.FIELD})
	@DLAnnotationType(GenerateUUIDDLAnnotation.class)
	public static @interface generateUUID
	{
	}

	@Override
	public boolean canValidateAttribute()
	{
		return true;
	}

	@Override
	public void bindToAttribute(DLAttribute attribute) throws DLException
	{
		assert attribute != null;

		validateThis();

		//log.debug("bindToAttribute", attribute);
		container = attribute;
		container.addAnnotation(this);

		attribute.getContainer().addInstanceValidator(this);
	}

	@Override
	public boolean validate(DLInstance instance, ValidationResult result)
	{
		assert result != null;

		String attributeName = container.getName();

		Object value = instance.get(attributeName);

		// ATTENTION: Tis is mutating the container! Experimental!
		if (value == null) {
			instance.set(attributeName, UUID.randomUUID());
		}

		return result.isValid();
	}
}
