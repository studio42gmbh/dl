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

import de.s42.base.beans.BeanHelper;
import de.s42.base.beans.BeanInfo;
import de.s42.base.beans.InvalidBean;
import de.s42.dl.DLAnnotation;
import de.s42.dl.DLAnnotationFactory;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.exceptions.InvalidValue;
import de.s42.dl.parameters.NamedParameter;
import de.s42.dl.parameters.NamedParameters;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Benjamin Schiller
 * @param <DLAnnotationType>
 */
public abstract class AbstractDLAnnotation<DLAnnotationType extends DLAnnotation> implements DLAnnotation, DLAnnotationFactory<DLAnnotationType>
{

	private final static Logger log = LogManager.getLogger(AbstractDLAnnotation.class.getName());

	protected String name;
	protected DLAnnotated container;
	protected NamedParameters parameters;

	private final static Map<Class<? extends DLAnnotation>, NamedParameters> namedParametersCache = Collections.synchronizedMap(new HashMap<>());

	protected NamedParameters createNamedParameters(Class<? extends DLAnnotation> annotationClass)
	{
		NamedParameters result = new NamedParameters(annotationClass);

		return result;
	}

	protected final synchronized NamedParameters getNamedParameters(Class<? extends DLAnnotation> annotationClass)
	{
		NamedParameters cachedParameters = namedParametersCache.get(annotationClass);

		if (cachedParameters != null) {
			return cachedParameters;
		}

		cachedParameters = createNamedParameters(annotationClass);

		namedParametersCache.put(annotationClass, cachedParameters);

		return cachedParameters;
	}

	protected AbstractDLAnnotation()
	{
		init();
	}

	private void init()
	{
		parameters = getNamedParameters(getClass());
	}

	@Override
	public DLAnnotationType createAnnotation(String name, DLAnnotated container, Object[] flatParameters) throws DLException
	{
		assert name != null;
		assert container != null;

		try {
			AbstractDLAnnotation annotation = (AbstractDLAnnotation) getClass().getConstructor().newInstance();
			annotation.setName(name);
			annotation.setContainer(container);
			parameters.applyFlatParameters(flatParameters, annotation);
			container.addAnnotation(annotation);
			return (DLAnnotationType) annotation;
		} catch (InvalidBean | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
			throw new DLException("Error creating annotation - " + ex.getMessage(), ex);
		}
	}

	@Override
	public boolean hasParameters()
	{
		return parameters.hasParameters();
	}

	@Override
	public Map<String, Object> getNamedParameters()
	{
		try {
			return parameters.getNamedParameters(this);
		} catch (InvalidBean ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Object[] getFlatParameters()
	{
		try {
			return parameters.getFlatParameters(this);
		} catch (InvalidBean | InvalidValue ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Object[] toFlatParameters(Map<String, Object> namedParameters) throws DLException
	{
		return parameters.toFlatParameters(namedParameters);
	}

	@Override
	public boolean isValidNamedParameter(String parameterName, Object value)
	{
		return parameters.isValidNamedParameter(parameterName, value);
	}

	@Override
	public boolean isValidNamedParameters(Map<String, Object> namedParameters)
	{
		return parameters.isValidNamedParameters(namedParameters);
	}

	@Override
	public boolean isValidFlatParameters(Object[] flatParameters)
	{
		return parameters.isValidFlatParameters(flatParameters);
	}

	public <ObjectType> ObjectType getNamedParameter(String parameterName, Object[] flatParameters) throws InvalidAnnotation
	{
		return parameters.get(parameterName, flatParameters);
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public DLAnnotated getContainer()
	{
		return container;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setContainer(DLAnnotated container)
	{
		this.container = container;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		builder.append("@");
		builder.append(name);

		if (parameters.hasParameters()) {

			try {
				BeanInfo info = BeanHelper.getBeanInfo(getClass());

				builder.append("(");
				NamedParameter[] params = parameters.getParameters();
				for (int i = 0; i < params.length; ++i) {
					if (i > 0) {
						builder.append(", ");
					}
					NamedParameter param = params[i];
					builder.append(param.name);
					builder.append(" : ");
					builder.append(info.read(this, param.name));
				}
				builder.append(")");
			} catch (InvalidBean ex) {
				throw new RuntimeException("Error toString this annotation - " + ex.getMessage(), ex);
			}
		}

		return builder.toString();
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 43 * hash + Objects.hashCode(this.name);
		hash = 43 * hash + Objects.hashCode(this.parameters);
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AbstractDLAnnotation<?> other = (AbstractDLAnnotation<?>) obj;
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		return Objects.equals(this.parameters, other.parameters);
	}
}
