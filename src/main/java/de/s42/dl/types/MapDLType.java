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
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.exceptions.InvalidType;
import de.s42.dl.exceptions.InvalidValue;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
	public Object read(Object... sources) throws InvalidType, InvalidValue
	{
		assert sources != null;

		if (sources.length % 2 != 0) {
			throw new InvalidValue("has to contain an even number of inputs, but has " + sources.length);
		}

		Map result = new HashMap();

		int genericTypesSize = getGenericTypes().size();
		if (genericTypesSize > 0) {

			if (genericTypesSize != 2) {
				throw new InvalidType("may contain either 0 or 2 generic types");
			}

			Class keyType = getGenericTypes().get(0).getJavaDataType();
			Class valueType = getGenericTypes().get(1).getJavaDataType();

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
	public void validateInstance(DLInstance instance) throws InvalidInstance
	{
		super.validateInstance(instance);

		// Validate entries (dynamic atributes) for generic types of map 
		if (isGenericType() && instance.hasAttributes()) {

			try {
				Class keyType = getMapKeyType();

				if (!String.class.isAssignableFrom(keyType)) {
					throw new InvalidInstance("Key type " + keyType.getName() + " is not supporting dynamic attributes");
				}

				Class valueType = getMapValueType();

				for (Map.Entry<String, Object> entry : instance.getAttributes().entrySet()) {

					Object value = entry.getValue();

					if (value != null && !valueType.isAssignableFrom(value.getClass())) {
						throw new InvalidInstance(
							"Value " + value + " is not of value type "
							+ valueType.getName() + " but "
							+ value.getClass().getName());
					}
				}
			} catch (InvalidType ex) {
				throw new InvalidInstance("Error validating instance - " + ex.getMessage(), ex);
			}
		}
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
	public void validate() throws InvalidType
	{
		super.validate();

		int count = getGenericTypes().size();
		if (count != 0 && count != 2) {
			throw new InvalidType("may only contain 0 or 2 generic types");
		}
	}
}
