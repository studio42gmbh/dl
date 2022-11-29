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
package de.s42.dl.annotations;

import de.s42.dl.DLAttribute;
import de.s42.dl.DLCore;
import de.s42.dl.DLType;
import de.s42.dl.annotations.DontPersistDLAnnotation.dontPersist;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 *
 * @author Benjamin Schiller
 */
public class DontPersistDLAnnotationTest
{

	private final static Logger log = LogManager.getLogger(DontPersistDLAnnotationTest.class.getName());

	@dontPersist
	public static class DontPersistClass
	{

	}

	@Test
	public void validDontPersistJavaType() throws DLException
	{
		DefaultCore core = new DefaultCore();
		DLType type = core.defineType(DontPersistClass.class);
		//log.debug("DontPersistClass DLType:\n", DLHelper.describe(type));
		Assert.assertTrue(type.hasAnnotation(DontPersistDLAnnotation.class), "@dontPersist should be mapped for type DontPersistTest");
	}

	@Test
	public void validDontPersistType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("validDontPersistType", "type T @dontPersist ;");
		DLType T = core.getType("T").orElseThrow();
		Assert.assertTrue(T.hasAnnotation(DontPersistDLAnnotation.class), "@dontPersist should be mapped for type T");
	}

	@Test
	public void validDontPersistAttribute() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("validDontPersistAttribute", "type T { int x @dontPersist ; }");
		DLType T = core.getType("T").orElseThrow();
		DLAttribute x = T.getAttribute("x").orElseThrow();
		Assert.assertTrue(x.hasAnnotation(DontPersistDLAnnotation.class), "@dontPersist should be mapped for attribute x int type T");
	}

	@Test(expectedExceptions = InvalidAnnotation.class)
	public void invalidDontPersistWithFlatParameter() throws DLException
	{
		DLCore core = new DefaultCore();

		core.parse("invalidDontPersistWithParameter", "type T { int x @dontPersist(wrong); }");
	}

	@Test(expectedExceptions = InvalidAnnotation.class)
	public void invalidDontPersistWithNamedParameter() throws DLException
	{
		DLCore core = new DefaultCore();

		core.parse("invalidDontPersistWithParameter", "type T { int x @dontPersist(wrong : true); }");
	}
}
