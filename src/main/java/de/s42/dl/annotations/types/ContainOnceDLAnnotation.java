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
 * This annotation allows to constrain contains of other Instances. The resolvment of the other type is lazy.
 *
 * @author Benjamin Schiller
 */
public class ContainOnceDLAnnotation extends AbstractDLAnnotation<ContainOnceDLAnnotation>
{

	public final static String DEFAULT_SYMBOL = "containOnce";

	/*
	private static class ContainOnceDLInstanceValidator implements DLInstanceValidator
	{

		private final DLCore core;
		private final String typeName;
		private DLType type;

		ContainOnceDLInstanceValidator(DLCore core, String typeName)
		{
			assert core != null;
			assert typeName != null;

			this.core = core;
			this.typeName = typeName;
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

			if (count != 1) {
				throw new InvalidInstance("Instance has to contain type '" + type + "' exactly once, but is contained " + count + " times");
			}
		}
	}

	@Override
	public void bindToType(DLCore core, DLType type) throws InvalidAnnotation
	{
		assert type != null;

		parameters = validateParameters(parameters, new Class[]{String.class});

		String typeName = (String) parameters[0];

		((DefaultDLType) type).addInstanceValidator(new ContainOnceDLInstanceValidator(core, typeName));
		 
	}
	 */
}
