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
import de.s42.dl.annotations.DLContract;
import de.s42.dl.language.DLOperators;
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
public class AndAnnotationFactory extends AbstractBinaryContractFactory
{

	private final static Logger log = LogManager.getLogger(AndAnnotationFactory.class.getName());

	public AndAnnotationFactory(String name, DLContractFactory factoryFirst, DLContractFactory factorySecond, Object[] flatParameters)
	{
		super(name, factoryFirst, factorySecond, flatParameters);
	}

	public AndAnnotationFactory(DLContract contractFirst, DLContract contractSecond, String name, String nameFirst, String nameSecond, Object[] flatParameters)
	{
		super(contractFirst, contractSecond, name, nameFirst, nameSecond, flatParameters);
	}
	
	@Override
	public String getOperatorAsString()
	{
		return DLOperators.And.operator;
	}

	// <editor-fold desc="Validators" defaultstate="collapsed">
	@Override
	public boolean validate(DLAttribute attribute, ValidationResult result)
	{
		if (!canValidateAttribute()) {
			result.addError(InvalidContract.toString(), "Can not validate attribute");
			return false;
		}

		if (!contractFirst.validate(attribute, new NoopValidationResult())) {
			result.addError(InvalidContract.toString(), "And @" + contractFirst.getName() + " first has failed in " + name);
			return false;
		}

		if (!contractSecond.validate(attribute, new NoopValidationResult())) {
			result.addError(InvalidContract.toString(), "And @" + contractSecond.getName() + " second has failed in " + name);
			return false;
		}

		return true;
	}

	@Override
	public boolean validate(DLInstance instance, ValidationResult result)
	{
		if (!canValidateInstance()) {
			result.addError(InvalidContract.toString(), "Can not validate instance");
			return false;
		}

		if (!contractFirst.validate(instance, new NoopValidationResult())) {
			result.addError(InvalidContract.toString(), "And @" + contractFirst.getName() + " first has failed in " + name);
			return false;
		}

		if (!contractSecond.validate(instance, new NoopValidationResult())) {
			result.addError(InvalidContract.toString(), "And @" + contractSecond.getName() + " second has failed in " + name);
			return false;
		}

		return true;
	}

	@Override
	public boolean validate(DLType type, ValidationResult result)
	{
		if (!canValidateType()) {
			result.addError(InvalidContract.toString(), "Can not validate type");
			return false;
		}

		if (!contractFirst.validate(type, new NoopValidationResult())) {
			result.addError(InvalidContract.toString(), "And @" + contractFirst.getName() + " first has failed in " + name);
			return false;
		}

		if (!contractSecond.validate(type, new NoopValidationResult())) {
			result.addError(InvalidContract.toString(), "And @" + contractSecond.getName() + " second has failed in " + name);
			return false;
		}

		return true;
	}

	@Override
	public boolean validate(DLType type, Object value, ValidationResult result)
	{
		if (!canValidateTypeRead()) {
			result.addError(InvalidContract.toString(), "Can not validate type read");
			return false;
		}

		if (!contractFirst.validate(type, value, new NoopValidationResult())) {
			result.addError(InvalidContract.toString(), "And @" + contractFirst.getName() + " first has failed in " + name);
			return false;
		}

		if (!contractSecond.validate(type, value, new NoopValidationResult())) {
			result.addError(InvalidContract.toString(), "And @" + contractSecond.getName() + " second has failed in " + name);
			return false;
		}

		return true;
	}
	//</editor-fold>
}
