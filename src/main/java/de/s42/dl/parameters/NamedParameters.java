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
package de.s42.dl.parameters;

import de.s42.base.beans.BeanHelper;
import de.s42.base.beans.BeanInfo;
import de.s42.base.beans.BeanProperty;
import de.s42.base.beans.InvalidBean;
import de.s42.base.conversion.ConversionHelper;
import de.s42.dl.DLAnnotation;
import de.s42.dl.annotations.DLAnnotationParameter;
import de.s42.dl.exceptions.InvalidValue;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 *
 * @author Benjamin Schiller
 */
public final class NamedParameters
{

	private final static Logger log = LogManager.getLogger(NamedParameters.class.getName());

	protected final NamedParameter[] parameters;
	protected final Map<String, NamedParameter> parametersByName = new HashMap<>();

	public NamedParameters(Class<? extends DLAnnotation> annotationClass)
	{
		try {
			List<NamedParameter> parametersAsList = new ArrayList();

			BeanInfo<?> info = BeanHelper.getBeanInfo(annotationClass);

			for (BeanProperty property : info.getProperties()) {

				if (property.getField() != null) {

					Field field = property.getField();

					// Just load paraneters with annotation @DLAnnotationParameter
					DLAnnotationParameter annotationParameter = field.getAnnotation(DLAnnotationParameter.class);

					if (annotationParameter != null) {

						Class type = ConversionHelper.wrapPrimitives(field.getType());
						NamedParameter namedParameter;

						Object defaultValue = null;
						if (!annotationParameter.defaultValue().isBlank()) {
							defaultValue = ConversionHelper.convert(annotationParameter.defaultValue(), field.getType());
						}

						namedParameter = new NamedParameter(
							field.getName(),
							type,
							defaultValue,
							annotationParameter.required(),
							annotationParameter.ordinal(),
							(Function<Object, Boolean>) annotationParameter.validation().getConstructor().newInstance()
						);
						// Set the given ordinal
						namedParameter.ordinal = annotationParameter.ordinal();
						// @todo determine ordinal from special annotation to each field/method
						parametersAsList.add(namedParameter);
					}
				}
			}

			// Sort all parameters by the given ordinal
			parametersAsList.sort((o1, o2) -> {
				return Integer.compare(o1.ordinal, o2.ordinal);
			});

			this.parameters = parametersAsList.toArray(NamedParameter[]::new);

			init();
		} catch (InvalidBean | IllegalAccessException | InstantiationException | NoSuchMethodException | RuntimeException | InvocationTargetException ex) {
			throw new RuntimeException("Error creating named parameters from annotation class '" + annotationClass.getName() + "' - " + ex.getMessage(), ex);
		}
	}

	/**
	 * This allows to synthetizise the parameters from a given annotation.
	 * THIS FEATURE IS EXPERIMEMTAL!
	 *
	 * @ param annotationClass
	 *
	 */
	/*
	public NamedParameters(Class<? extends Annotation> annotationClass) throws RuntimeException
	{
		assert annotationClass != null;

		List<NamedParameter> parametersAsList = new ArrayList();

		// Read values from fixed fields
		for (Field field : annotationClass.getFields()) {

			if (!DLAnnotationHelper.SUPPRESSED_ANNOTATION_ELEMENT_NAMES.contains(field.getName())) {

				// Just load paraneters with annotation @DLAnnotationParameter
				DLAnnotationParameter annotationParameter = field.getAnnotation(DLAnnotationParameter.class);

				if (annotationParameter != null) {

					Class type = ConversionHelper.wrapPrimitives(field.getType());
					NamedParameter namedParameter;
					try {
						namedParameter = new NamedParameter(
							field.getName(),
							type,
							null,
							(Function<Object, Boolean>) annotationParameter.validation().getConstructor().newInstance()
						);
					} catch (RuntimeException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException ex) {
						throw new RuntimeException("Error creating named parameters from annotation class '" + annotationClass.getName() + "' - " + ex.getMessage(), ex);
					}
					// Set the given ordinal
					namedParameter.ordinal = annotationParameter.ordinal();
					// @todo determine ordinal from special annotation to each field/method
					parametersAsList.add(namedParameter);
				}
			}
		}

		// Read values from methods
		for (Method method : annotationClass.getMethods()) {

			if (!DLAnnotationHelper.SUPPRESSED_ANNOTATION_ELEMENT_NAMES.contains(method.getName())) {

				// Just load paraneters with annotation @DLAnnotationParameter
				DLAnnotationParameter annotationParameter = method.getAnnotation(DLAnnotationParameter.class);

				if (annotationParameter != null) {

					Class type = ConversionHelper.wrapPrimitives(method.getReturnType());
					Object defaultValue = method.getDefaultValue();
					NamedParameter namedParameter;
					try {
						namedParameter = new NamedParameter(
							method.getName(),
							type,
							defaultValue,
							(Function<Object, Boolean>) annotationParameter.validation().getConstructor().newInstance()
						);
					} catch (RuntimeException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException ex) {
						throw new RuntimeException("Error creating named parameters from annotation class '" + annotationClass.getName() + "' - " + ex.getMessage(), ex);
					}
					// Set the given ordinal
					namedParameter.ordinal = annotationParameter.ordinal();
					// @todo determine ordinal from special annotation to each field/method
					parametersAsList.add(namedParameter);
				}
			}
		}
		// Sort all parameters by the given ordinal
		parametersAsList.sort((o1, o2) -> {
			return Integer.compare(o1.ordinal, o2.ordinal);
		});

		this.parameters = parametersAsList.toArray(NamedParameter[]::new);

		init();
	}
	 */
	/**
	 *
	 * @param parameters
	 */
	public NamedParameters(NamedParameter... parameters)
	{
		assert parameters != null;

		this.parameters = parameters;

		init();
	}

	private void init()
	{
		for (int i = 0; i < parameters.length; ++i) {
			NamedParameter parameter = parameters[i];
			parameter.parameters = this;
			parameter.ordinal = i;
			parametersByName.put(parameter.name, parameter);
		}
	}

	public void applyNamedParameters(Map<String, Object> namedParameters, Object object) throws InvalidValue, InvalidBean
	{
		applyFlatParameters(toFlatParameters(namedParameters), object);
	}

	/**
	 * Applies the flat parameters using bean introspection with BeanHelper
	 *
	 * @param flatParameters
	 * @param object
	 *
	 * @throws InvalidValue
	 * @throws InvalidBean
	 */
	public void applyFlatParameters(Object[] flatParameters, Object object) throws InvalidValue, InvalidBean
	{
		if (!isValidFlatParameters(flatParameters)) {
			throw new InvalidValue("Invalid flat parameters can not get applied");
		}

		BeanInfo<?> info = BeanHelper.getBeanInfo(object.getClass());

		for (int i = 0; i < parameters.length; ++i) {

			NamedParameter parameter = parameters[i];

			Object flatParameter = null;

			if (flatParameters != null && flatParameters.length > i) {
				flatParameter = flatParameters[i];
			}

			if (flatParameter == null) {
				flatParameter = parameter.defaultValue;
			}

			BeanProperty property = info.getProperty(parameter.name).orElseThrow();
			property.write(object, ConversionHelper.convert(flatParameter, property.getPropertyClass()));
		}
	}

	public boolean hasParameters()
	{
		return parameters.length > 0;
	}

	public boolean contains(String parameterName)
	{
		return parametersByName.containsKey(parameterName);
	}

	public boolean contains(NamedParameter parameter)
	{
		return indexOf(parameter) > -1;
	}

	public Optional<NamedParameter> fromName(String parameterName)
	{
		return Optional.ofNullable(parametersByName.get(parameterName));
	}

	public Optional<NamedParameter> fromIndex(int ordinal)
	{
		if (ordinal < 0 || ordinal >= parameters.length) {
			return Optional.empty();
		}

		return Optional.of(parameters[ordinal]);
	}

	public int indexOf(String parameterName)
	{
		return indexOf(parametersByName.get(parameterName));
	}

	public int indexOf(NamedParameter parameter)
	{
		if (parameter == null) {
			return -1;
		}

		return parameter.ordinal;
	}

	public Map<String, Object> getNamedParameters(Object object) throws InvalidBean
	{
		Map<String, Object> result = new HashMap<>();

		BeanInfo<?> info = BeanHelper.getBeanInfo(object.getClass());

		for (int i = 0; i < parameters.length; ++i) {

			NamedParameter parameter = parameters[i];

			BeanProperty property = info.getProperty(parameter.name).orElseThrow();
			Object val = property.read(object);
			result.put(parameter.name, val);
		}

		return result;
	}

	public Object[] getFlatParameters(Object object) throws InvalidBean, InvalidValue
	{
		return toFlatParameters(getNamedParameters(object));
	}

	public Object[] toFlatParameters(Map<String, Object> namedParameters) throws InvalidValue
	{
		Object[] flatParameters = new Object[parameters.length];

		for (int i = 0; i < parameters.length; ++i) {
			NamedParameter parameter = parameters[i];
			Object val = namedParameters.get(parameter.name);
			flatParameters[i] = (val != null) ? val : parameter.defaultValue;

			if (!parameter.isValid(flatParameters[i])) {
				throw new InvalidValue("namedParameter " + parameter.name + " is invalid");
			}
		}

		return flatParameters;
	}

	public <ObjectType> ObjectType get(NamedParameter parameter, Object[] flatParameters)
	{
		int index = indexOf(parameter);

		if (index >= -1) {
			return (ObjectType) ConversionHelper.convert(flatParameters[index], parameter.type);
		}

		return null;
	}

	public <ObjectType> ObjectType get(String parameterName, Object[] flatParameters)
	{
		int index = indexOf(parameterName);

		if (index >= -1) {
			NamedParameter parameter = parameters[index];
			return (ObjectType) ConversionHelper.convert(flatParameters[index], parameter.type);
		}

		return null;
	}

	public <ObjectType> ObjectType getOrDefault(String parameterName, Map<String, Object> namedParameters)
	{
		Optional<NamedParameter> optParameter = fromName(parameterName);

		if (optParameter.isEmpty()) {
			return null;
		}

		return getOrDefault(optParameter.orElseThrow(), namedParameters);
	}

	public <ObjectType> ObjectType getOrDefault(NamedParameter parameter, Map<String, Object> namedParameters)
	{
		return (ObjectType) ConversionHelper.convert(namedParameters.getOrDefault(parameter.name, parameter.defaultValue), parameter.type);
	}

	public boolean isValidFlatParameters(Object[] flatParameters)
	{
		// If both are null we are always good
		if (flatParameters == null && parameters.length == 0) {
			return true;
		}

		// The flatparameters may never be longer than the parameters
		if (flatParameters != null && flatParameters.length > parameters.length) {
			return false;
		}

		for (int i = 0; i < parameters.length; ++i) {
			NamedParameter parameter = parameters[i];

			Object flatParameter = null;

			if (flatParameters != null && flatParameters.length > i) {
				flatParameter = flatParameters[i];
			}

			if (!parameter.isValid(flatParameter)) {
				return false;
			}
		}

		return true;
	}

	public boolean isValidNamedParameters(Map<String, Object> namedParameters)
	{
		try {
			return isValidFlatParameters(toFlatParameters(namedParameters));	
		}
		catch (InvalidValue ex)
		{
			return true;
		}
	}

	public boolean isValidNamedParameter(String parameterName, Object value)
	{
		Optional<NamedParameter> optParameter = fromName(parameterName);

		if (optParameter.isEmpty()) {
			return false;
		}

		return optParameter.orElseThrow().isValid(value);
	}

	public NamedParameter[] getParameters()
	{
		return parameters;
	}
}
