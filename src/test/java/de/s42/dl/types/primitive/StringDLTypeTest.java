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
package de.s42.dl.types.primitive;

import de.s42.dl.DLCore;
import de.s42.dl.DLModule;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class StringDLTypeTest
{

	//private final static Logger log = LogManager.getLogger(StringDLTypeTest.class.getName());
	@Test
	public void readStringFromModule() throws DLException
	{
		DLCore core = new DefaultCore();

		DLModule module = core.parse("readStringFromModule",
			"String s : \"Test 42\";");

		Assert.assertEquals(module.getString("s"), "Test 42");
	}

	// @todo might want to allow single instance simple types
	@Test(enabled = false)
	public void readStringInstance() throws DLException
	{
		DLCore core = new DefaultCore();

		core.parse("readStringInstance",
			"String s @export { value : \"Test 42\"; }");

		//DLInstance val = core.convertFromJavaObject("Test 42");
		//log.info(DLHelper.describe(core.getType(String.class).orElseThrow()));
		Assert.assertEquals(core.getExported("s").orElseThrow().getString("value"), "Test 42");
	}

}
