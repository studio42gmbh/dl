// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 * 
 * Copyright 2023 Studio 42 GmbH ( https://www.s42m.de ).
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
package de.s42.dl.parser.contracts.operators;

import de.s42.dl.DLAttribute;
import de.s42.dl.DLInstance;
import de.s42.dl.DLType;
import de.s42.dl.annotations.DLAnnotated;
import de.s42.dl.annotations.DLContract;
import de.s42.dl.parser.contracts.DLContractFactory;
import static de.s42.dl.validation.DefaultValidationCode.InvalidContract;
import de.s42.dl.validation.NoopValidationResult;
import de.s42.dl.validation.ValidationResult;
import de.s42.log.LogManager;
import de.s42.log.Logger;

/**
 *
 * @author Benjamin Schiller
 */
public class OrAnnotationFactory extends AbstractBinaryContractFactory
{

	private final static Logger log = LogManager.getLogger(OrAnnotationFactory.class.getName());

	public OrAnnotationFactory(String name, DLContractFactory factoryFirst, DLContractFactory factorySecond, Object[] flatParameters)
	{
		super(name, factoryFirst, factorySecond, flatParameters);
	}

	public OrAnnotationFactory(DLContract contractFirst, DLContract contractSecond, String name, String nameFirst, String nameSecond, DLAnnotated container)
	{
		super(contractFirst, contractSecond, name, nameFirst, nameSecond, container);
	}

	// <editor-fold desc="Validators" defaultstate="collapsed">
	@Override
	public boolean validate(DLAttribute attribute, ValidationResult result)
	{
		if (!canValidateAttribute()) {
			result.addError(InvalidContract.toString(), "Can not validate attribute");
		}

		if (!contractFirst.validate(attribute, NoopValidationResult.NOOP_RESULT)
			&& !contractSecond.validate(attribute, NoopValidationResult.NOOP_RESULT)) {
			result.addError(InvalidContract.toString(), "Or @" + contractFirst.getName() + " both have failed in " + name);
			return false;
		}

		return true;
	}

	@Override
	public boolean validate(DLInstance instance, ValidationResult result)
	{
		if (!canValidateInstance()) {
			result.addError(InvalidContract.toString(), "Can not validate instance");
		}

		if (!contractFirst.validate(instance, NoopValidationResult.NOOP_RESULT)
			&& !contractSecond.validate(instance, NoopValidationResult.NOOP_RESULT)) {
			result.addError(InvalidContract.toString(), "Or @" + contractFirst.getName() + " both have failed in " + name);
			return false;
		}

		return true;
	}

	@Override
	public boolean validate(DLType type, ValidationResult result)
	{
		if (!canValidateType()) {
			result.addError(InvalidContract.toString(), "Can not validate type");
		}

		if (!contractFirst.validate(type, NoopValidationResult.NOOP_RESULT)
			&& !contractSecond.validate(type, NoopValidationResult.NOOP_RESULT)) {
			result.addError(InvalidContract.toString(), "Or @" + contractFirst.getName() + " both have failed in " + name);
			return false;
		}

		return true;
	}

	@Override
	public boolean validate(DLType type, Object value, ValidationResult result)
	{
		if (!canValidateTypeRead()) {
			result.addError(InvalidContract.toString(), "Can not validate type read");
		}

		if (!contractFirst.validate(type, value, NoopValidationResult.NOOP_RESULT)
			&& !contractSecond.validate(type, value, NoopValidationResult.NOOP_RESULT)) {
			result.addError(InvalidContract.toString(), "Or @" + contractFirst.getName() + " both have failed in " + name);
			return false;
		}

		return true;
	}
	//</editor-fold>
}
