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

import de.s42.dl.DLAnnotation;
import de.s42.dl.DLAttribute;
import de.s42.dl.DLInstance;
import de.s42.dl.DLType;
import de.s42.dl.validation.DLValidator;
import static de.s42.dl.validation.DefaultValidationCode.CanNotValidateAttribute;
import static de.s42.dl.validation.DefaultValidationCode.CanNotValidateInstance;
import static de.s42.dl.validation.DefaultValidationCode.CanNotValidateType;
import static de.s42.dl.validation.DefaultValidationCode.CanNotValidateTypeRead;
import de.s42.dl.validation.ValidationResult;

/**
 *
 * @author Benjamin Schiller
 */
public interface DLContract extends DLValidator, DLAnnotation
{

	@Override
	default public boolean validate(DLAttribute attribute, ValidationResult result)
	{
		if (!canValidateAttribute()) {
			result.addError(CanNotValidateAttribute.toString(), "Can not validate an attribute");
			return false;
		}
		
		return true;
	}

	@Override
	default public boolean validate(DLInstance instance, ValidationResult result)
	{
		if (!canValidateInstance()) {
			result.addError(CanNotValidateInstance.toString(), "Can not validate an instance");
			return false;
		}
		
		return true;
	}

	@Override
	default public boolean validate(DLType type, ValidationResult result)
	{
		if (!canValidateAttribute()) {
			result.addError(CanNotValidateType.toString(), "Can not validate a type");
			return false;
		}
		
		return true;
	}

	@Override
	default public boolean validate(DLType type, Object value, ValidationResult result)
	{
		if (!canValidateTypeRead()) {
			result.addError(CanNotValidateTypeRead.toString(), "can not validate a type value");
			return false;
		}
		
		return true;
	}
}
