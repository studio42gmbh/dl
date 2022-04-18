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
package de.s42.dl.alias;

import de.s42.dl.DLCore;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidCore;
import de.s42.dl.exceptions.InvalidType;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class DLAliasTest
{

	@Test
	public void validAliasType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type A; alias B A; B test;");
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidAliasAlreadyDefinedType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type A; type B; alias B A; B test;");
	}

	@Test(expectedExceptions = InvalidCore.class)
	public void invalidAliasUndefinedType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "alias B A;");
	}

	@Test
	public void validAliasContainsType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type A; alias B A; type C contains A; C test { B test2; }");
	}

	@Test
	public void validAliasExternType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "Integer value : 4; alias i Integer; type C { i val; } C test { val : $value; }");
	}
	
	@Test
	public void validAliasAnnotation() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "alias test dynamic;");
		core.parse("Anonymous2", "type T @test;");
	}
	
	@Test(expectedExceptions = InvalidCore.class)
	public void invalidAliasAnnotation() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "alias test notDefined;");
	}
		
	@Test
	public void validAliasPragma() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "alias test disableDefineTypes;");
		core.parse("Anonymous2", "pragma test;");
	}
	
	@Test(expectedExceptions = InvalidCore.class)
	public void invalidAliasPragma() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "alias test notDefined;");
	}
	
}
