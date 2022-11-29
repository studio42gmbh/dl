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
import de.s42.dl.DLInstance;
import de.s42.dl.core.BaseDLCore;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.core.resolvers.StringCoreResolver;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidType;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 *
 * @author Benjamin Schiller
 */
public class DeclareTypesTest
{

	private final static Logger log = LogManager.getLogger(DeclareTypesTest.class.getName());

	@Test
	public void validDeclaration() throws DLException
	{
		BaseDLCore core = new BaseDLCore();
		core.addResolver(new StringCoreResolver(core));
		core.setAllowDefineTypes(true);
		core.parse("validDeclaration", "declare type T;");
	}

	@Test
	public void validMultipleDeclaration() throws DLException
	{
		BaseDLCore core = new BaseDLCore();
		core.addResolver(new StringCoreResolver(core));
		core.setAllowDefineTypes(true);
		core.parse("validDeclaration", "declare type T; declare type T; declare type T;");
	}
	
	@Test
	public void validDeclarationAfterDefinition() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("validDeclaration", "type T { int x; } declare type T;");
	}
	
	@Test
	public void validDeclarationAndDefinition() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("validDeclarationAndDefinition", "declare type T; type T { int x; } T t { x : 42; }");
	}

	@Test
	public void validDependentDeclarationAndDefinition() throws DLException
	{
		DLCore core = new DefaultCore();				
		core.parse("validDependentDeclarationAndDefinition",
			"declare type T;"
			+ "declare type T2;"
			+ "type T { T2 other; int x; }"
			+ "type T2 { T other; float y; }"
			+ "T t { x : 42; }"
			+ "T2 t2 @export { other : $t; y : 3.1415; }"
		);
		
		DLInstance t2 = core.getExported("t2").orElseThrow();		
		Assert.assertEquals((float)t2.get("y"), 3.1415f);
		
		DLInstance other = t2.get("other");		
		Assert.assertEquals((int)other.get("x"), 42);		
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidDeclarationWithBody() throws DLException
	{
		BaseDLCore core = new BaseDLCore();
		core.addResolver(new StringCoreResolver(core));
		core.setAllowDefineTypes(true);
		core.parse("invalidDeclarationWithBody", "declare type T {}");
	}
}
