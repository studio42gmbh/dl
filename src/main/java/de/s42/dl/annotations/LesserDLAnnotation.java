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

/**
 *
 * @author Benjamin Schiller
 */
public class LesserDLAnnotation extends AbstractComparisonDLAnnotation<Object, LesserDLAnnotation>
{

	public final static String DEFAULT_SYMBOL = "lesser";

	@Override
	protected String errorMessage(Object val, Object refVal)
	{
		return "val '" + val + "' must be lesser than refval '" + refVal + "'";
	}

	@Override
	protected boolean compare(Object val, Object refVal)
	{
		assert val != null;
		assert refVal != null;

		if (val instanceof Double && refVal instanceof Double) {
			return ((Double) val < (Double) refVal);
		} else if (val instanceof Float && refVal instanceof Float) {
			return ((Float) val < (Float) refVal);
		} else if (val instanceof Long && refVal instanceof Long) {
			return ((Long) val < (Long) refVal);
		} else if (val instanceof Integer && refVal instanceof Integer) {
			return ((Integer) val < (Integer) refVal);
		} else if (val instanceof Short && refVal instanceof Short) {
			return ((Short) val < (Short) refVal);
		} else if (val instanceof String && refVal instanceof String) {
			return ((String) val).compareTo((String) refVal) < 0;
		}

		throw new IllegalArgumentException("Types of val and refVal have to be Number or String");
	}
}
