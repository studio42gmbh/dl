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

import de.s42.base.collections.MappedList;
import de.s42.base.conversion.ConversionHelper;
import de.s42.dl.*;
import de.s42.dl.exceptions.InvalidValue;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Benjamin Schiller
 */
public class DefaultDLEnum extends DefaultDLType implements DLEnum
{

	private final MappedList<String, Object> values = new MappedList<>();

	public DefaultDLEnum()
	{
		javaType = String.class;
	}

	public DefaultDLEnum(String name)
	{
		super(name);

		javaType = String.class;
	}

	public DefaultDLEnum(Class<? extends Enum> enumImpl)
	{
		this(enumImpl.getName(), enumImpl);
	}

	public DefaultDLEnum(String name, Class<? extends Enum> enumImpl)
	{
		super(name);

		assert enumImpl != null;

		javaType = enumImpl;

		for (Object enumC : enumImpl.getEnumConstants()) {
			values.add(enumC.toString(), enumC);
		}
	}

	@Override
	public List getValues()
	{
		return values.list();
	}

	public void addValue(String value) throws InvalidValue
	{
		assert value != null;

		if (javaType != null && Enum.class.isAssignableFrom(javaType)) {
			throw new InvalidValue("May not add values to enum backed DLEnum '" + getName() + "'");
		}

		if (values.contains(value)) {
			throw new InvalidValue("Value '" + value + "' is already contained in enum");
		}

		values.add(value, value);
	}

	@Override
	public boolean contains(String name)
	{
		return values.contains(name);
	}

	@Override
	public Object valueOf(String name) throws InvalidValue
	{
		Optional<Object> value = values.get(name);

		if (value.isEmpty()) {
			throw new InvalidValue(
				"Value '" + name
				+ "' is not contained in enum '" + getName() + "' "
				+ Arrays.toString(values.toArray())
			);
		}

		return value.orElseThrow();
	}

	@Override
	public Object read(Object... sources) throws InvalidValue
	{
		assert sources != null;

		Object[] result = ConversionHelper.convertArray(sources, new Class[]{getJavaType()});

		if (getJavaType().equals(String.class)) {
			if (!values.contains(result[0].toString())) {
				throw new InvalidValue(
					"Value '" + result[0]
					+ "' is not contained in enum '" + getName() + "' "
					+ Arrays.toString(values.toArray())
				);
			}
		}

		return result[0];
	}
}
