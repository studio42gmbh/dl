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

import de.s42.dl.DLAnnotationFactory;
import de.s42.dl.DLAttribute;
import de.s42.dl.DLInstance;
import de.s42.dl.DLType;
import de.s42.dl.annotations.DLAnnotated;
import de.s42.dl.annotations.DLContract;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.parser.contracts.DLContractFactory;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author Benjamin Schiller
 */
public class AbstractBinaryContractFactory implements DLContractFactory, DLContract
{

	private final static Logger log = LogManager.getLogger(AbstractBinaryContractFactory.class.getName());

	protected final Object[] flatParameters;
	protected final DLContractFactory factoryFirst;
	protected final DLContractFactory factorySecond;
	protected final DLContract contractFirst;
	protected final DLContract contractSecond;
	protected final String name;
	protected final String nameFirst;
	protected final String nameSecond;
	protected final DLAnnotated container;

	public AbstractBinaryContractFactory(
		DLContract contractFirst,
		DLContract contractSecond,
		String name,
		String nameFirst,
		String nameSecond,
		DLAnnotated container
	)
	{
		assert container != null;
		assert contractFirst != null;
		assert contractSecond != null;
		assert name != null;
		assert nameFirst != null;
		assert nameSecond != null;
		assert container != null;

		this.contractFirst = contractFirst;
		this.contractSecond = contractSecond;
		this.name = name;
		this.nameFirst = nameFirst;
		this.nameSecond = nameSecond;
		this.factoryFirst = null;
		this.factorySecond = null;
		this.flatParameters = null;
		this.container = container;
	}

	public AbstractBinaryContractFactory(
		String name,
		DLContractFactory factoryFirst,
		DLContractFactory factorySecond,
		Object[] flatParameters
	)
	{
		assert name != null;
		assert factoryFirst != null;
		assert factorySecond != null;

		this.factoryFirst = factoryFirst;
		this.factorySecond = factorySecond;
		this.flatParameters = flatParameters;
		this.nameFirst = null;
		this.nameSecond = null;
		this.contractFirst = null;
		this.contractSecond = null;
		this.name = name;
		this.container = null;
	}

	@Override
	public DLContract createAnnotation(String ignored, DLAnnotated container, Object[] flatParameters) throws DLException
	{
		//assert name != null;
		assert container != null;

		try {
			DLContract proxiedAnnotationFirst = factoryFirst.createAnnotation(factoryFirst.getName(), container, this.flatParameters);
			DLContract proxiedAnnotationSecond = factorySecond.createAnnotation(factorySecond.getName(), container, this.flatParameters);

			if (!proxiedAnnotationFirst.canEqualValidations(proxiedAnnotationSecond)) {
				throw new InvalidAnnotation("@" + factoryFirst.getName() + " and @" + factorySecond.getName() + " can not be combind as they have different can validations in annotation @" + getName() + "");
			}

			AbstractBinaryContractFactory contract = (AbstractBinaryContractFactory) getClass()
				.getConstructor(DLContract.class, DLContract.class, String.class, String.class, String.class, DLAnnotated.class)
				.newInstance(proxiedAnnotationFirst, proxiedAnnotationSecond, this.name, factoryFirst.getName(), factorySecond.getName(), container);
			container.addAnnotation(contract);
			return contract;
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
			throw new InvalidAnnotation("Error creating annotation @" + getName() + " - " + ex.getMessage(), ex);
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
		//log.debug("bindToInstance", instance);

		if (canValidateInstance()) {
			instance.addValidator(this);
		}
	}

	@Override
	public void bindToType(DLType type) throws DLException
	{
		//log.debug("bindToType", type);

		if (canValidateType()) {
			type.addValidator(this);
		}

		if (canValidateTypeRead()) {
			type.addReadValidator(this);
		}

		if (canValidateInstance()) {
			type.addInstanceValidator(this);
		}
	}

	@Override
	public void bindToAttribute(DLAttribute attribute) throws DLException
	{
		//log.debug("bindToAttribute", attribute);

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
		return contractFirst.canValidateAttribute() && contractSecond.canValidateAttribute();
	}

	@Override
	public boolean canValidateInstance()
	{
		return contractFirst.canValidateInstance() && contractSecond.canValidateInstance();
	}

	@Override
	public boolean canValidateType()
	{
		return contractFirst.canValidateType() && contractSecond.canValidateType();
	}

	@Override
	public boolean canValidateTypeRead()
	{
		return contractFirst.canValidateTypeRead() && contractSecond.canValidateTypeRead();
	}
	//</editor-fold>

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	@Override
	public Object[] getFlatParameters()
	{
		return flatParameters;
	}

	public DLAnnotationFactory getFactoryFirst()
	{
		return factoryFirst;
	}

	public DLAnnotationFactory getFactorySecond()
	{
		return factorySecond;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public String getNameFirst()
	{
		return nameFirst;
	}

	public String getNameSecond()
	{
		return nameSecond;
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
	//</editor-fold>		
}
