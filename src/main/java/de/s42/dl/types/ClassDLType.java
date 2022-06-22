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
import de.s42.dl.exceptions.InvalidValue;

/**
 *
 * @author Benjamin Schiller
 */
public class ClassDLType extends SimpleDLType
{

	public final static String DEFAULT_SYMBOL = "Class";

	public ClassDLType()
	{
		this(DEFAULT_SYMBOL);
	}

	public ClassDLType(String name)
	{
		super(name);
	}

	@Override
	public Object read(Object... sources) throws InvalidValue
	{
		assert sources != null;

		Object[] result = ConversionHelper.convertArray(sources, new Class[]{String.class});

		try {
			return Class.forName((String) result[0]);
		} catch (ClassNotFoundException ex) {
			throw new InvalidValue("Class " + (String) result[0] + " could not be found");
		}
	}

	@Override
	public Class getJavaDataType()
	{
		return Class.class;
	}
}
