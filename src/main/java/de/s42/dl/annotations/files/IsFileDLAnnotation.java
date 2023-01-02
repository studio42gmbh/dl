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
package de.s42.dl.annotations.files;

import de.s42.dl.DLAttribute;
import de.s42.dl.DLInstance;
import de.s42.dl.annotations.AbstractDLContract;
import de.s42.dl.annotations.DLAnnotationType;
import de.s42.dl.exceptions.InvalidAnnotation;
import static de.s42.dl.validation.DefaultValidationCode.InvalidFile;
import de.s42.dl.validation.ValidationResult;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author Benjamin Schiller
 */
public class IsFileDLAnnotation extends AbstractDLContract<IsFileDLAnnotation>
{

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.FIELD})
	@DLAnnotationType(IsFileDLAnnotation.class)
	public static @interface isFile
	{
	}

	private String attributeName;

	@Override
	public boolean validate(DLInstance instance, ValidationResult result)
	{
		assert instance != null;

		Object val = instance.get(attributeName);

		if (val == null) {
			return true;
		}

		if (!(val instanceof Path)) {
			result.addError(InvalidFile.toString(), "Attribute value '" + attributeName + "' is not referencing a valid file but is " + val, instance);
			return false;
		}

		if (!Files.isRegularFile((Path) val)) {
			result.addError(InvalidFile.toString(), "Attribute value '" + attributeName + "' is not referencing a regular file but " + ((Path) val).toAbsolutePath().normalize().toString(), instance);
			return false;
		}

		return true;
	}

	@Override
	public void bindToAttribute(DLAttribute attribute) throws InvalidAnnotation
	{
		assert attribute != null;

		this.attributeName = attribute.getName();

		attribute.getContainer().addInstanceValidator(this);
	}
}
