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

import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.*;
import de.s42.dl.types.DefaultDLType;

/**
 *
 * @author Benjamin Schiller
 */
public class ContainDLAnnotation extends AbstractDLAnnotation
{

	private static class ContainOnceDLInstanceValidator extends AbstractDLValidator
	{

		private final DLType type;
		private final int min;
		private final int max;

		ContainOnceDLInstanceValidator(DLType type, int min, int max)
		{
			assert type != null;
			assert min >= 0;
			assert max >= min;

			this.type = type;
			this.min = min;
			this.max = max;
		}

		@Override
		public void validate(DLInstance instance) throws InvalidInstance
		{
			assert instance != null;

			int count = instance.getChildren(type).size();

			if (count < min || count > max) {
				throw new InvalidInstance("Instance has to contain type '" + type + "' between " + min + " and " + max + " times, but is contained " + count + " times");
			}
		}
	}

	public final static String DEFAULT_SYMBOL = "contain";

	public ContainDLAnnotation()
	{
		this(DEFAULT_SYMBOL);
	}

	public ContainDLAnnotation(String name)
	{
		super(name);
	}

	@Override
	public void bindToType(DLCore core, DLType type, Object... parameters) throws InvalidAnnotation
	{
		assert core != null;
		assert type != null;

		parameters = validateParameters(parameters, new Class[]{String.class, Integer.class, Integer.class});

		String typeName = (String) parameters[0];
		int min = (Integer) parameters[1];
		int max = (Integer) parameters[2];

		if (min < 0) {
			throw new InvalidAnnotation("min has to be >= 0 but is " + min);
		}

		if (max < min) {
			throw new InvalidAnnotation("max has to be >= min but is " + max + " and min is " + min);
		}

		DLType containedType = core.getType(typeName).get();

		((DefaultDLType) type).addValidator(new ContainOnceDLInstanceValidator(containedType, min, max));
	}
}
