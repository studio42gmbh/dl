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

import de.s42.dl.DLCore;
import de.s42.dl.exceptions.UndefinedType;
import de.s42.dl.exceptions.InvalidType;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import java.io.IOException;
import java.net.URISyntaxException;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class DLTypesGenericTest
{

	@Test
	public void validDefinitionOfGenericTypeForType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type Generic {} type Test { Array<Generic> text; }");
	}

	@Test(expectedExceptions = {UndefinedType.class})
	public void invalidDefinitionOfGenericTypeNotDefined() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type Test { Array<Generic> text; }");
	}

	@Test(expectedExceptions = {UndefinedType.class})
	public void invalidSecondGenericTypeNotDefined() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type Generic {} type Test { Array<Generic, Second> text; }");
	}

	@Test
	public void validCustomGenericType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type Generic @generic {} type Test { Generic<String> text; }");
	}

	@Test(expectedExceptions = {InvalidType.class})
	public void invalidNonGenericType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type Generic {} type Test { String<Generic> text; }");
	}

	@Test
	public void validComplexGenericTypesAndDataInFile() throws IOException, URISyntaxException, DLException
	{
		DLCore core = new DefaultCore();
		core.parse("de/s42/dl/types/validComplexGenericTypesAndDataInFile.dl");
	}
}
