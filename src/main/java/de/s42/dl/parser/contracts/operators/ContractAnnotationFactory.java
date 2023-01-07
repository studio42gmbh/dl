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

import de.s42.dl.DLAnnotation;
import de.s42.dl.DLAnnotationFactory;
import de.s42.dl.DLAttribute;
import de.s42.dl.DLInstance;
import de.s42.dl.DLType;
import de.s42.dl.annotations.DLAnnotated;
import de.s42.dl.annotations.DLContract;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.parser.contracts.DLContractFactory;
import static de.s42.dl.validation.DefaultValidationCode.InvalidContract;
import de.s42.dl.validation.ValidationResult;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author Benjamin Schiller
 */
public class ContractAnnotationFactory implements DLContractFactory, DLContract
{

	private final static Logger log = LogManager.getLogger(ContractAnnotationFactory.class.getName());

	protected final Object[] flatParameters;
	protected final DLAnnotationFactory factory;
	protected final DLContract contract;
	protected String name;
	protected final String contractName;
	protected DLAnnotated container;

	public ContractAnnotationFactory(DLContract contract, String name)
	{
		assert contract != null;
		assert name != null;

		this.contract = contract;
		this.name = name;
		this.contractName = null;		
		this.factory = null;
		this.flatParameters = null;
		this.container = null;
	}

	public ContractAnnotationFactory(String contractName, DLAnnotationFactory factory, Object[] flatParameters)
	{
		assert contractName != null;
		assert factory != null;

		this.factory = factory;
		this.flatParameters = flatParameters;
		this.contract = null;
		this.contractName = contractName;
		this.container = null;
		this.name = null;		
	}

	@Override
	public DLContract createAnnotation(String ignored, Object[] ignoredParameters) throws DLException
	{
		try {
			DLAnnotation proxiedAnnotation = factory.createAnnotation(this.contractName, this.flatParameters);
			DLContract newContract = (ContractAnnotationFactory) getClass()
				.getConstructor(DLContract.class, String.class)
				.newInstance(proxiedAnnotation, getName());
			return newContract;
		} catch (DLException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
			throw new DLException("Error creating annotation - " + ex.getMessage(), ex);
		}
	}

	@Override
	public Class getAnnotationType()
	{
		return getClass();
	}

	// <editor-fold desc="Bindings" defaultstate="collapsed">
	@Override
	public void bindToInstance(DLInstance instance) throws DLException
	{
		assert instance != null;

		//log.debug("bindToInstance", instance);
		container = instance;
		instance.addAnnotation(this);

		if (canValidateInstance()) {
			instance.addValidator(this);
		}
	}

	@Override
	public void bindToType(DLType type) throws DLException
	{
		assert type != null;

		//log.debug("bindToType", type);
		container = type;
		type.addAnnotation(this);

		if (canValidateType() || canValidateTypeRead()) {
			type.addValidator(this);
		}

		if (canValidateInstance()) {
			type.addInstanceValidator(this);
		}
	}

	@Override
	public void bindToAttribute(DLAttribute attribute) throws DLException
	{
		assert attribute != null;

		//log.debug("bindToAttribute", attribute);
		container = attribute;
		attribute.addAnnotation(this);

		if (canValidateAttribute()) {
			attribute.addValidator(this);
			attribute.getContainer().addInstanceValidator(this);
		}
	}
	//</editor-fold>

	// <editor-fold desc="CanValidates" defaultstate="collapsed">
	@Override
	public boolean canValidateAttribute()
	{
		return contract.canValidateAttribute();
	}

	@Override
	public boolean canValidateInstance()
	{
		return contract.canValidateInstance();
	}

	@Override
	public boolean canValidateType()
	{
		return contract.canValidateType();
	}

	@Override
	public boolean canValidateTypeRead()
	{
		return contract.canValidateTypeRead();
	}	
	//</editor-fold>

	// <editor-fold desc="Validators" defaultstate="collapsed">
	@Override
	public boolean validate(DLAttribute attribute, ValidationResult result)
	{
		if (!canValidateAttribute()) {
			result.addError(InvalidContract.toString(), "Can not validate attribute");
			return false;
		}

		return contract.validate(attribute, result);
	}

	@Override
	public boolean validate(DLInstance instance, ValidationResult result)
	{
		if (!canValidateInstance()) {
			result.addError(InvalidContract.toString(), "Can not validate instance");
			return false;
		}

		return contract.validate(instance, result);
	}

	@Override
	public boolean validate(DLType type, ValidationResult result)
	{
		if (!canValidateType()) {
			result.addError(InvalidContract.toString(), "Can not validate type");
			return false;
		}

		return contract.validate(type, result);
	}

	@Override
	public boolean validate(DLType type, Object value, ValidationResult result)
	{
		if (!canValidateTypeRead()) {
			result.addError(InvalidContract.toString(), "Can not validate type read");
			return false;
		}

		return contract.validate(type, value, result);
	}
	//</editor-fold>

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	@Override
	public Object[] getFlatParameters()
	{
		return flatParameters;
	}

	public DLAnnotationFactory getFactory()
	{
		return factory;
	}

	@Override
	public String getName()
	{
		// @todo ugly way to manage nesting vs. no nesting and preserving original annotation name ...
		return (this.name != null) ? this.name : this.contractName;
	}

	@Override
	public DLAnnotated getContainer()
	{
		return container;
	}

	@Override
	public boolean hasParameters()
	{
		return false;
	}

	@Override
	public Map<String, Object> getNamedParameters()
	{
		return Collections.EMPTY_MAP;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}
	
	public DLContract getContract()
	{
		return contract;
	}

	@Override
	public String getOperatorAsString()
	{
		return "";
	}
	//</editor-fold>
}
