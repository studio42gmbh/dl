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

import de.s42.base.conversion.ConversionHelper;
import de.s42.dl.DLAnnotation;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.parameters.NamedParameters;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 *
 * @author Benjamin Schiller
 */
public abstract class AbstractDLAnnotation implements DLAnnotation
{

	protected final String name;
	protected final NamedParameters parameters;

	protected AbstractDLAnnotation(String name)
	{
		assert name != null;

		this.name = name;
		parameters = null;
	}

	protected AbstractDLAnnotation(Class<? extends Annotation> annotationClass)
	{
		assert annotationClass != null;

		this.name = annotationClass.getSimpleName();
		this.parameters = new NamedParameters(annotationClass);
	}

	protected AbstractDLAnnotation(String name, NamedParameters parameters)
	{
		assert name != null;
		assert parameters != null;

		this.name = name;
		this.parameters = parameters;
	}

	protected AbstractDLAnnotation(String name, Class<? extends Annotation> annotationClass)
	{
		assert name != null;
		assert annotationClass != null;

		this.name = name;
		this.parameters = new NamedParameters(annotationClass);
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

	public boolean isValidFlatParameters(Object[] flatParameters)
	{
		return parameters.isValidFlatParameters(flatParameters);
	}

	public <ObjectType> ObjectType getNamedParameter(String parameterName, Object[] flatParameters) throws InvalidAnnotation
	{
		return parameters.get(parameterName, flatParameters);
	}

	@SuppressWarnings("UseSpecificCatch")
	@Deprecated
	protected static Object[] validateParameters(Object[] parameters, Class[] requiredParameterClasses) throws InvalidAnnotation
	{
		if (parameters == null && requiredParameterClasses == null) {
			return new Object[0];
		}

		if (parameters == null) {
			parameters = new Object[0];
		}

		if (requiredParameterClasses == null) {
			requiredParameterClasses = new Class[0];
		}

		if (parameters.length == 0 && requiredParameterClasses.length == 0) {
			return new Object[0];
		}

		if (parameters.length != requiredParameterClasses.length) {
			throw new InvalidAnnotation("Parameter count not matching has to have " + requiredParameterClasses.length + " parameter(s) but has " + parameters.length);
		}

		Object[] result = new Object[parameters.length];

		for (int i = 0; i < parameters.length; ++i) {

			assert requiredParameterClasses[i] != null;

			if (parameters[i] == null) {
				throw new InvalidAnnotation("Parameter " + (i + 1) + " may not be null");
			}

			try {
				result[i] = (Object) ConversionHelper.convert(parameters[i], requiredParameterClasses[i]);
			} catch (Throwable ex) {
				throw new InvalidAnnotation("Parameter " + (i + 1) + " could not get converted to target type " + requiredParameterClasses[i] + " - " + ex.getMessage(), ex);
			}
		}

		return result;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public NamedParameters getParameters()
	{
		return parameters;
	}
}
