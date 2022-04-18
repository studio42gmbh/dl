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

import de.s42.base.testing.AssertHelper;
import de.s42.dl.DLCore;
import de.s42.dl.DLModule;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class DoubleDLTest
{

	public static class TestType
	{

		protected double doubleValue;

		public double getDoubleValue()
		{
			return doubleValue;
		}

		public void setDoubleValue(double doubleValue)
		{
			this.doubleValue = doubleValue;
		}
	}

	@Test
	public void validDoubleNormalNotation() throws DLException
	{
		DLCore core = new DefaultCore();
		core.defineType(core.createType(TestType.class), "TestType");
		DLModule module = core.parse("Anonymous", "TestType { doubleValue : 103455.2346634; }");
		TestType instance = module.getChild(0).toJavaObject(core);
		AssertHelper.assertEpsilonEquals(instance.getDoubleValue(), 103455.2346634, "Double is not matching");
	}

	@Test
	public void validDoubleScientificNotation() throws DLException
	{
		DLCore core = new DefaultCore();
		core.defineType(core.createType(TestType.class), "TestType");
		DLModule module = core.parse("Anonymous", "TestType { doubleValue : 1.43E-4; }");
		TestType instance = module.getChild(0).toJavaObject(core);
		AssertHelper.assertEpsilonEquals(instance.getDoubleValue(), 1.43E-4, "Double is not matching");
	}
}
