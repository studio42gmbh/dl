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
import de.s42.dl.DLType;
import de.s42.dl.exceptions.InvalidType;
import java.util.Arrays;
import java.util.List;

/**
 *
 *
 * @author Benjamin Schiller
 */
// @todo https://github.com/studio42gmbh/dl/issues/10 add List support
public class ListDLType extends DefaultDLType
{

	public final static String DEFAULT_SYMBOL = "List";

	public ListDLType()
	{
		this(DEFAULT_SYMBOL, null);
	}

	public ListDLType(DLType genericType)
	{
		this(DEFAULT_SYMBOL, genericType);
	}

	public ListDLType(String name, DLType genericType)
	{
		super(name);

		init(genericType);
	}

	private void init(DLType genericType)
	{
		setAllowGenericTypes(true);

		if (genericType != null) {
			try {
				addGenericType(genericType);
			} catch (InvalidType ex) {
				throw new RuntimeException("This should not happen as setAllowGenericTypes was just called - " + ex, ex);
			}
		}
	}

	@Override
	public Object read(Object... sources)
	{
		assert sources != null;

		List result;

		if (getGenericTypes().size() > 0) {
			result = ConversionHelper.convertList(sources, getGenericTypes().get(0).getJavaDataType());
		} else {
			result = Arrays.asList(sources);
		}

		return result;
	}

	@Override
	public Class getJavaDataType()
	{
		return List.class;
	}
}
