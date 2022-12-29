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
package de.s42.dl.instances;

import de.s42.dl.DLModule;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidAttribute;
import de.s42.dl.exceptions.InvalidValue;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author Benjamin Schiller
 */
public class DLInstanceTest
{

	@Test(expectedExceptions = InvalidValue.class)
	public void invalidAssignemntToSimpleInstance() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidAssignemntToSimpleInstance",
			"type A; A test : TEST;"
		);
	}

	@Test(expectedExceptions = InvalidAttribute.class)
	public void invalidDoubleAttributeAssignment() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidDoubleAttributeAssignment",
			"type A; type B { A test; } B test2 { test: A{}; test : A{}; }"
		);
	}

	@Test
	public void simpleInstanceWithJavaBuildInstanceEquality() throws Exception
	{
		DefaultCore core = new DefaultCore();
		DLModule module = core.parse("simpleInstanceWithJavaBuildInstanceEquality",
			"type T { Integer x; } T t1 {}"
		);

		assertNotNull(module.getChild("t1").orElseThrow());
		assertEquals(module.getChild("t1").orElseThrow().getName(), "t1");
	}
}
