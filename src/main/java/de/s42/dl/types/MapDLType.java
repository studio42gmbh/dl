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
package de.s42.dl.types;

import de.s42.base.conversion.ConversionHelper;
import de.s42.dl.DLInstance;
import de.s42.dl.DLType;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.exceptions.InvalidType;
import de.s42.dl.exceptions.InvalidValue;
import static de.s42.dl.validation.DefaultValidationCode.DynamicAttributeNotAllowed;
import static de.s42.dl.validation.DefaultValidationCode.InvalidGenericParameters;
import static de.s42.dl.validation.DefaultValidationCode.InvalidGenericTypes;
import static de.s42.dl.validation.DefaultValidationCode.InvalidValueType;
import de.s42.dl.validation.ValidationResult;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 *
 *
 * @author Benjamin Schiller
 */
// https://github.com/studio42gmbh/dl/issues/11 map support
public class MapDLType extends DefaultDLType
{

	public final static String DEFAULT_SYMBOL = "Map";

	public MapDLType()
	{
		this(DEFAULT_SYMBOL);
	}

	public MapDLType(String name)
	{
		this(name, null, null);
	}

	public MapDLType(DLType genericKeyType, DLType genericValueType)
	{
		this(DEFAULT_SYMBOL, genericKeyType, genericValueType);
	}

	public MapDLType(String name, DLType genericKeyType, DLType genericValueType)
	{
		super(name);

		init(genericKeyType, genericValueType);
	}

	private void init(DLType genericKeyType, DLType genericValueType)
	{
		setAllowGenericTypes(true);
		setAllowDynamicAttributes(true);

		if (genericKeyType != null && genericValueType != null) {
			try {
				addGenericType(genericKeyType);
				addGenericType(genericValueType);
			} catch (InvalidType ex) {
				throw new RuntimeException("This should not happen as setAllowGenericTypes was just called - " + ex, ex);
			}
		}
	}

	@Override
	public boolean mayContainType(DLType type)
	{
		if (!isGenericType()) {
			return true;
		}

		return super.mayContainType(type);
	}

	@Override
	public DLInstance fromJavaObject(Object object) throws DLException
	{
		if (!(object instanceof Map)) {
			throw new InvalidInstance("object is required to be of class Map");
		}

		DLInstance instance = core.createInstance(this);

		// @todo properly handle complex elements
		if (isGenericType()) {

			Class valueType = getMapValueType();

			for (Map.Entry entry : (Set<Map.Entry>) ((Map) object).entrySet()) {
				instance.set(entry.getKey().toString(), ConversionHelper.convert(entry.getValue(), valueType));
			}
		} else {
			for (Map.Entry entry : (Set<Map.Entry>) ((Map) object).entrySet()) {
				instance.set(entry.getKey().toString(), entry.getValue());
			}
		}

		if (!instance.validate(new ValidationResult())) {
			throw new InvalidInstance("Object could not get converted");
		}

		return instance;
	}

	@Override
	public void setAttributeFromValue(DLInstance instance, String name, Object value) throws DLException
	{
		if (isGenericType()) {

			Class valueType = getMapValueType();

			try {
				instance.set(name, ConversionHelper.convert(value, valueType));
			} catch (RuntimeException ex) {
				throw new InvalidValue(
					"Error converting attribute " + name
					+ " to value type " + valueType.getCanonicalName() + " - " + ex.getMessage(), ex);
			}
		} else {
			instance.set(name, value);
		}
	}

	@Override
	public Object read(Object... sources) throws InvalidType, InvalidValue
	{
		assert sources != null;

		// @todo handle reading of maps
		if (sources.length % 2 != 0) {
			throw new InvalidValue("has to contain an even number of inputs, but has " + sources.length);
		}

		Map result = new HashMap();

		int genericTypesSize = getGenericTypes().size();
		if (genericTypesSize > 0) {

			if (genericTypesSize != 2) {
				throw new InvalidType("may contain either 0 or 2 generic types");
			}

			Class keyType = getMapKeyType();
			Class valueType = getMapValueType();

			// Make map be type checked
			result = Collections.checkedMap(
				result,
				keyType,
				valueType
			);

			for (int i = 0; i < sources.length; i += 2) {
				result.put(
					ConversionHelper.convert(sources[i], keyType),
					ConversionHelper.convert(sources[i + 1], valueType)
				);
			}
		} else {
			for (int i = 0; i < sources.length; i += 2) {
				result.put(sources[i], sources[i + 1]);
			}
		}

		return result;
	}

	@Override
	public boolean validateInstance(DLInstance instance, ValidationResult result)
	{
		boolean valid = super.validateInstance(instance, result);

		// Validate entries (dynamic atributes) for generic types of map 
		if (isGenericType() && instance.hasAttributes()) {

			try {
				Class keyType = getMapKeyType();

				if (!String.class.isAssignableFrom(keyType)) {
					result.addError(DynamicAttributeNotAllowed.toString(), "Key type " + keyType.getName() + " is not supporting dynamic attributes", this);
					valid = false;
				}

				Class valueType = getMapValueType();

				for (Map.Entry<String, Object> entry : instance.getAttributes().entrySet()) {

					Object value = entry.getValue();

					if (value != null && !valueType.isAssignableFrom(value.getClass())) {
						result.addError(InvalidValueType.toString(),
							"Value " + value + " in Map is not of value type "
							+ valueType.getName() + " but "
							+ value.getClass().getName(), this);
						valid = false;
					}
				}
			} catch (InvalidType ex) {
				result.addError(InvalidGenericTypes.toString(), "Error validating instance", this);
			}
		}

		return valid;
	}

	@Override
	public Map<?, ?> createJavaInstance() throws InvalidType
	{
		if (isGenericType()) {

			Class keyType = getMapKeyType();
			Class valueType = getMapValueType();

			return Collections.checkedMap(
				new HashMap<>(),
				keyType,
				valueType
			);
		} else {
			return new HashMap<>();
		}
	}

	@Override
	public Class getJavaDataType()
	{
		return Map.class;
	}

	public Class getMapKeyType() throws InvalidType
	{
		if (!isGenericType()) {
			return Object.class;
		}

		return getGenericTypes().get(0).getJavaDataType();
	}

	public Class getMapValueType() throws InvalidType
	{
		if (!isGenericType()) {
			return Object.class;
		}

		return getGenericTypes().get(1).getJavaDataType();
	}

	public Optional<DLType> getGenericKeyType()
	{
		if (!isGenericType()) {
			return Optional.empty();
		}

		return Optional.of(getGenericTypes().get(0));
	}

	public Optional<DLType> getGenericValueType()
	{
		if (!isGenericType()) {
			return Optional.empty();
		}

		return Optional.of(getGenericTypes().get(1));
	}

	@Override
	public void addGenericType(DLType genericType) throws InvalidType
	{
		assert genericType != null;

		if (getGenericTypes().size() >= 2) {
			throw new InvalidType("may only contain 2 generic types");
		}

		super.addGenericType(genericType);
	}

	@Override
	public boolean validate(ValidationResult result)
	{
		boolean valid = super.validate(result);

		int count = getGenericTypes().size();
		if (count != 0 && count != 2) {
			result.addError(InvalidGenericParameters.toString(), "May only contain 0 or 2 generic types", this);
			valid = false;
		}

		return valid;
	}
}
