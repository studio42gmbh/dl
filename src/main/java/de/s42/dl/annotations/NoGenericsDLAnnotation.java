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

import de.s42.dl.*;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidType;
import de.s42.dl.types.DefaultDLType;

/**
 *
 * @author Benjamin Schiller
 */
public class NoGenericsDLAnnotation extends AbstractDLAnnotation
{

	private static class NoGenericsValidator implements DLTypeValidator
	{

		@Override
		public void validate(DLType type) throws InvalidType
		{
			assert type != null;

			for (DLAttribute attribute : type.getAttributes()) {
				if (attribute.getType().isGenericType()) {
					throw new InvalidType("Type " + type + " may not contain generics, but " + attribute + " has");
				}
			}
		}
	}

	public final static String DEFAULT_SYMBOL = "noGenerics";

	public NoGenericsDLAnnotation()
	{
		this(DEFAULT_SYMBOL);
	}

	public NoGenericsDLAnnotation(String name)
	{
		super(name);
	}

	@Override
	public void bindToType(DLCore core, DLType type, Object... parameters) throws DLException
	{
		assert type != null;

		((DefaultDLType) type).addValidator(new NoGenericsValidator());
	}
}
