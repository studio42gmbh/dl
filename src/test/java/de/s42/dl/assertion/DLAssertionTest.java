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
package de.s42.dl.assertion;

import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.parser.DLHrfParsingException;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class DLAssertionTest
{

	@Test
	public void simpleAssert() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("simpleAssert", "assert true;");
	}

	@Test
	public void simpleAssertWithMessage() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("simpleAssertWithMessage", "assert true : \"true\";");
	}

	@Test(expectedExceptions = DLHrfParsingException.class)
	public void invalidSimpleAssert() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidSimpleAssert", "assert false;");
	}

	@Test(expectedExceptions = DLHrfParsingException.class)
	public void invalidSimpleAssertWithMessage() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidSimpleAssertWithMessage", "assert false : \"false\";");
	}

	@Test
	public void injectedAssert() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.addExported("injectedAssert", "Windows");
		core.parse("injectedAssert", "assert $injectedAssert == Windows : $injectedAssert + \" != Windows\";");
	}

	@Test(expectedExceptions = DLHrfParsingException.class)
	public void invalidInjectedAssert() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.addExported("invalidInjectedAssert", "Linux");
		core.parse("invalidInjectedAssert", "assert $invalidInjectedAssert == Windows : $invalidInjectedAssert + \" != Windows\";");
	}

	@Test
	public void dLAssert() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("dLAssert",
			"String dLAssert : Windows;"
			+ "assert $dLAssert == Windows : $dLAssert + \" != Windows\";"
		);
	}

	@Test(expectedExceptions = DLHrfParsingException.class)
	public void invalidDLAssert() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidDLAssert",
			"String invalidDLAssert : Linux;"
			+ "assert $invalidDLAssert == Windows : $invalidDLAssert + \" != Windows\";"
		);
	}
}
