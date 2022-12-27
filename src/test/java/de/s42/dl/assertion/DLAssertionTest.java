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
	public void validSimpleAssert() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("validSimpleAssert", "assert true;");
	}

	@Test
	public void validSimpleAssertWithMessage() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("validSimpleAssert", "assert true : \"true\";");
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
		core.parse("invalidSimpleAssert", "assert false : \"false\";");
	}

	@Test
	public void validInjectedAssert() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.addExported("OS", "Windows");
		core.parse("validInjectedAssert", "assert $OS == Windows : $OS + \" != Windows\";");
	}

	@Test(expectedExceptions = DLHrfParsingException.class)
	public void invalidInjectedAssert() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.addExported("OS", "Linux");
		core.parse("validInjectedAssert", "assert $OS == Windows : $OS + \" != Windows\";");
	}

	@Test
	public void validDLAssert() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("validDLAssert",
			"String OS : Windows;"
			+ "assert $OS == Windows : $OS + \" != Windows\";"
		);
	}

	@Test(expectedExceptions = DLHrfParsingException.class)
	public void invalidDLAssert() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidDLAssert",
			"String OS : Linux;"
			+ "assert $OS == Windows : $OS + \" != Windows\";"
		);
	}
}
