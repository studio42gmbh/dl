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
package de.s42.dl.annotations.types;

import de.s42.dl.annotations.AbstractDLAnnotation;

/**
 *
 * @author Benjamin Schiller
 */
public class ContainOnlyDLAnnotation extends AbstractDLAnnotation<ContainOnlyDLAnnotation>
{

	public final static String DEFAULT_SYMBOL = "containOnly";

	/*
	private static class ContainOnlyDLInstanceValidator implements DLInstanceValidator
	{

		private final DLCore core;
		private final String typeName;
		private DLType type;
		private final int min;
		private final int max;

		ContainOnlyDLInstanceValidator(DLCore core, String typeName, int min, int max)
		{
			assert min >= 0;
			assert max >= min;
			assert core != null;
			assert typeName != null;

			this.core = core;
			this.typeName = typeName;
			this.min = min;
			this.max = max;
		}

		@Override
		public void validate(DLInstance instance) throws InvalidInstance
		{
			assert instance != null;

			// @todo https://github.com/studio42gmbh/dl/issues/14 Remove sketchy lookup of other type at instance validation time and move it back to type validation time
			if (type == null) {
				Optional<DLType> optType;
				try {
					optType = core.getType(typeName);
				} catch (DLException ex) {
					throw new InvalidInstance("OContained type not found - " + ex.getMessage(), ex);
				}

				if (optType.isEmpty()) {
					throw new InvalidInstance("Contained type '" + typeName + "' is not defined");
				}

				type = optType.orElseThrow();
			}

			int count = instance.getChildren(type).size();
			int maxCount = instance.getChildren().size();

			if (count != maxCount) {
				throw new InvalidInstance("Instance may only contain type '" + type + "' between " + min + " and " + max + " times, but contains other types");
			}

			if (count < min || count > max) {
				throw new InvalidInstance("Instance has to contain type '" + type + "' between " + min + " and " + max + " times, but is contained " + count + " times");
			}
		}
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

		((DefaultDLType) type).addInstanceValidator(new ContainOnlyDLInstanceValidator(core, typeName, min, max));

	}
	 */
}
