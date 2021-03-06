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

import de.s42.dl.DLAttribute;
import de.s42.dl.DLCore;
import de.s42.dl.DLInstance;
import de.s42.dl.DLInstanceValidator;
import de.s42.dl.DLType;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.types.DefaultDLType;
import java.util.UUID;

/**
 *
 * @author Benjamin Schiller
 */
public class GenerateUUIDDLAnnotation extends AbstractDLAnnotation
{

	private static class GenerateUUIDDLInstanceValidator implements DLInstanceValidator
	{

		private final DLAttribute attribute;

		GenerateUUIDDLInstanceValidator(DLAttribute attribute)
		{
			assert attribute != null;

			this.attribute = attribute;
		}

		@Override
		public void validate(DLInstance instance)
		{
			assert instance != null;

			Object val = instance.get(attribute.getName());

			if (val == null) {
				instance.set(attribute.getName(), UUID.randomUUID());
			}
		}
	}

	public final static String DEFAULT_SYMBOL = "generateUUID";

	public GenerateUUIDDLAnnotation()
	{
		this(DEFAULT_SYMBOL);
	}

	public GenerateUUIDDLAnnotation(String name)
	{
		super(name);
	}

	@Override
	public void bindToAttribute(DLCore core, DLType type, DLAttribute attribute, Object... parameters) throws InvalidAnnotation
	{
		assert type != null;
		assert attribute != null;

		validateParameters(parameters, null);

		((DefaultDLType) type).addInstanceValidator(new GenerateUUIDDLInstanceValidator(attribute));
	}
}
