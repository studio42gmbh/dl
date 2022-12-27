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
package de.s42.dl.references;

import de.s42.dl.*;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * This shows some of the potential of expressions in DL
 * See https://github.com/studio42gmbh/dl/issues/20
 *
 * @author Benjamin Schiller
 */
public class ReferencesTest
{
	public static class Data {
		public int x;
		public String y;
		public Object z;
	}
	

	@Test
	public void validSimpleReference() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("validSimpleReference",
			"type T { Integer val; }"
			+ "T t { val : 42; }"
			+ "T tr : $t;");
		Assert.assertEquals(module.getInstance("tr").getType().getName(), "T");
	}
	
	@Test
	public void validSimpleReferencePath() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("validSimpleReferencePath",
			"type T { Integer val; }"
			+ "T t { val : 42; }"
			+ "Integer p : $t.val;");
		Assert.assertEquals(module.getInt("p"), 42);
	}
	
	

	@Test(enabled = false)
	public void validReferencePath() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("validReferencePath",
			"type T { Integer val; }"
			+ "T t { val : 42; }"
			+ "Integer p : $t.val?.test.t;");
		Assert.assertEquals(module.getInt("p"), 42);
	}
}
