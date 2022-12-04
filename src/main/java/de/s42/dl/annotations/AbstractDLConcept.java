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
import de.s42.dl.DLInstance;
import de.s42.dl.DLType;
import static de.s42.dl.validation.DefaultValidationCode.CanNotValidateAttribute;
import static de.s42.dl.validation.DefaultValidationCode.CanNotValidateInstance;
import static de.s42.dl.validation.DefaultValidationCode.CanNotValidateType;
import de.s42.dl.validation.ValidationResult;

/**
 *
 * @author Benjamin Schiller
 * @param <DLConceptType>
 */
public abstract class AbstractDLConcept<DLConceptType extends DLConcept> extends AbstractDLAnnotation<DLConceptType> implements DLConcept
{
	
	@Override
	public boolean validate(DLAttribute attribute, ValidationResult result)
	{
		result.addError(CanNotValidateAttribute.toString(), "Concept " + getClass().getCanonicalName() + " can not validate an attribute");
		return false;
	}

	@Override
	public boolean validate(DLInstance instance, ValidationResult result)
	{
		result.addError(CanNotValidateInstance.toString(), "Concept " + getClass().getCanonicalName() + " can not validate an instance");
		return false;
	}

	@Override
	public boolean validate(DLType type, ValidationResult result)
	{
		result.addError(CanNotValidateType.toString(), "Concept " + getClass().getCanonicalName() + " can not validate a type");
		return false;
	}
}
