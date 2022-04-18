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
import de.s42.dl.*;
import de.s42.dl.exceptions.InvalidEnumValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Benjamin Schiller
 */
public class DefaultDLEnum extends DefaultDLType implements DLEnum
{

	private final List<String> values = new ArrayList<>();

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
			values.add((String) enumC.toString());
		}
	}

	@Override
	public List<String> getValues()
	{
		return Collections.unmodifiableList(values);
	}

	public void addValue(String value) throws InvalidEnumValue
	{
		assert value != null;

		if (javaType != null && Enum.class.isAssignableFrom(javaType)) {
			throw new InvalidEnumValue("May not add values to enum backed DLEnum '" + getName() + "'");
		}

		if (values.contains(value)) {
			throw new InvalidEnumValue("Value '" + value + "' is already contained in enum");
		}

		values.add(value);
	}

	@Override
	public Object read(Object... sources) throws InvalidEnumValue
	{
		assert sources != null;

		Object[] result = ConversionHelper.convertArray(sources, new Class[]{String.class});

		if (!values.contains((String) result[0])) {
			throw new InvalidEnumValue(
				"Value '" + result[0]
				+ "' is not contained in enum '" + getName() + "' "
				+ Arrays.toString(values.toArray())
			);
		}

		return result[0];
	}
}
