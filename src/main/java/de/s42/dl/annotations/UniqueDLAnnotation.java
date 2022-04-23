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

import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.*;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.types.DefaultDLType;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Benjamin Schiller
 */
public class UniqueDLAnnotation extends AbstractDLAnnotation
{

	private static class UniqueDLInstanceValidator implements DLValidator
	{

		private final Set uniqueCache = new HashSet();
		private final DLAttribute attribute;

		UniqueDLInstanceValidator(DLAttribute attribute)
		{
			assert attribute != null;

			this.attribute = attribute;
		}

		@Override
		public void validate(DLInstance instance) throws InvalidInstance
		{
			assert instance != null;

			Object val = instance.get(attribute.getName());

			if (uniqueCache.contains(val)) {
				throw new InvalidInstance("Attribute value '" + attribute.getName() + "' has to be unique");
			}

			uniqueCache.add(val);
		}
	}

	public final static String DEFAULT_SYMBOL = "unique";

	public UniqueDLAnnotation()
	{
		this(DEFAULT_SYMBOL);
	}

	public UniqueDLAnnotation(String name)
	{
		super(name);
	}

	@Override
	public void bindToAttribute(DLCore core, DLType type, DLAttribute attribute, Object... parameters) throws InvalidAnnotation
	{
		assert type != null;
		assert attribute != null;

		validateParameters(parameters, null);

		((DefaultDLType) type).addInstanceValidator(new UniqueDLInstanceValidator(attribute));
	}
}
