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

import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.*;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class DLTypesContainsTest
{

	@Test
	public void validContainsForType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type A; type B contains A; B test { A test2; }");
	}

	@Test
	public void validContainsSelfForType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type A contains A; A test { A test2; }");
	}

	@Test
	public void validContainsForMultipleTypes() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type A; type A2; type B contains A, A2; B test { A2 test2; A test3; A test4; }");
	}

	@Test(expectedExceptions = {InvalidInstance.class})
	public void invalidContainsMissingForType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type A; type B; B test { A test2; }");
	}

	@Test(enabled = false)
	public void validContainsOnceForType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type A; type B @containOnce(A) contains A; B test { A test2; }");
	}

	@Test(enabled = false, expectedExceptions = {InvalidInstance.class})
	public void invalidContainsOnceForType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type A; type B @containOnce(A) contains A; B test { A test2; A test3; }");
	}

	@Test
	public void validContainRangedForType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type A; type B @contain(A, 1, 2) contains A; B test { A test2; } B test3 { A test4; A test5; }");
	}

	@Test(expectedExceptions = {InvalidInstance.class})
	public void invalidContainRangedForType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type A; type B @contain(A, 1, 2) contains A; B test { A test2; } B test3 { A test4; A test5; A test6; }");
	}

	@Test
	public void validContainsForParentType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type A; type B contains A; type C extends B; C test { A test2; }");
	}

	@Test
	public void validContainsForParentTypeOfParentType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type A; type B contains A; type C extends B; type D extends A; C test { D test2; }");
	}

	@Test(enabled = false)
	public void validComplexGenericTypesAndDataInFile() throws Exception
	{
		DLCore core = new DefaultCore();
		core.parse("de/s42/dl/types/validComplexTypeContainsInFile.dl");
	}

}
