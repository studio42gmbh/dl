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

import de.s42.dl.DLInstance;
import de.s42.dl.DLType;
import de.s42.dl.exceptions.InvalidType;

/**
 *
 * @author Benjamin Schiller
 */
public class ArrayDLType extends DefaultDLType
{

	public final static String DEFAULT_SYMBOL = "Array";

	public ArrayDLType()
	{
		this(DEFAULT_SYMBOL);
	}

	public ArrayDLType(String name)
	{
		super(name);

		allowGenericTypes = true;
	}

	@Override
	public Object read(Object... sources) throws InvalidType
	{
		if (sources == null) {
			return new Object[0];
		}

		// @todo DL validate types of contents
		if (isGenericType()) {

			//log.debug("Validating types");
			for (Object source : sources) {

				if (source instanceof DLInstance) {

					DLInstance instance = (DLInstance) source;
					boolean foundType = false;

					//check all generic types
					for (DLType genericType : genericTypes) {

						// if one fits - fine
						if (genericType.isAssignableFrom(instance.getType())) {
							foundType = true;
							break;
						}
					}

					if (!foundType) {
						throw new InvalidType("Type " + instance.getType().getName() + " is not contained in genericTypes");
					}
				} else {

					if (source != null && getJavaDataType().isAssignableFrom(source.getClass())) {
						throw new InvalidType("Source '" + source + "' is not contained in genericTypes");
					}
				}
			}
		}

		return sources;
	}

	@Override
	public Class getJavaDataType()
	{
		// @todo DL is this really a viable way of typing the array type?
		if (genericTypes.size() == 1) {
			return genericTypes.iterator().next().getJavaDataType().arrayType();
		}

		return Object[].class;
	}
}
