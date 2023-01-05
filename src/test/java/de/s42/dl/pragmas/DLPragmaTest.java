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
package de.s42.dl.pragmas;

import de.s42.dl.exceptions.InvalidPragma;
import de.s42.dl.*;
import de.s42.dl.annotations.persistence.DontPersistDLAnnotation.dontPersist;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 *
 * @author Benjamin Schiller
 */
public class DLPragmaTest
{

	@dontPersist
	public static class TestPragma extends AbstractDLPragma
	{

		public int counter;

		public TestPragma()
		{
			super("test");
		}

		@Override
		public void doPragma(DLCore core, Object... parameters) throws InvalidPragma
		{
			Object[] params = validateParameters(parameters, new Class[]{int.class, int.class});

			int a = (int) params[0];
			int b = (int) params[1];

			counter += a * b;
		}
	}

	@Test
	public void validDefineAndUsePragma() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("validDefineAndUsePragma",
			"extern pragma de.s42.dl.pragmas.DLPragmaTest$TestPragma @noGenerics;"
			+ "pragma test(6, 2);"
			+ "pragma test(\"9\", \"4\");"
		);
		TestPragma testPragma = (TestPragma) core.getPragma("test").orElseThrow();

		Assert.assertEquals(48, testPragma.counter);
	}

	@Test
	public void validPragmaDefinedInJava() throws DLException
	{
		DLCore core = new DefaultCore();
		TestPragma testPragma = new TestPragma();
		core.definePragma(testPragma);
		core.parse("validPragmaDefinedInJava", "pragma test(6, 2); pragma test(\"9\", \"4\");");
		Assert.assertEquals(testPragma.counter, 48);
	}

	@Test(expectedExceptions = InvalidPragma.class)
	public void invalidPragmaNotDefined() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidPragmaNotDefined", "pragma notDefined;");
	}

	@Test(expectedExceptions = InvalidPragma.class)
	public void invalidCustomPragmaNoParameters() throws DLException
	{
		DLCore core = new DefaultCore();
		TestPragma testPragma = new TestPragma();
		core.definePragma(testPragma);
		core.parse("invalidCustomPragmaNoParameters", "pragma test;");
	}

	@Test(expectedExceptions = InvalidPragma.class)
	public void invalidCustomPragmaParameterCount() throws DLException
	{
		DLCore core = new DefaultCore();
		TestPragma testPragma = new TestPragma();
		core.definePragma(testPragma);
		core.parse("invalidCustomPragmaParameterCount", "pragma test(6);");
	}

	@Test(expectedExceptions = InvalidPragma.class)
	public void invalidCustomPragmaParameterType() throws DLException
	{
		DLCore core = new DefaultCore();
		TestPragma testPragma = new TestPragma();
		core.definePragma(testPragma);
		core.parse("invalidCustomPragmaParameterType", "pragma test(6, true);");
	}

	@Test(expectedExceptions = InvalidPragma.class)
	public void invalidPragmaUseAnnotation() throws DLException
	{
		DLCore core = new DefaultCore();
		TestPragma testPragma = new TestPragma();
		core.definePragma(testPragma);
		core.parse("invalidPragmaUseAnnotation", "pragma test @noGenerics;");
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void invalidPragmaDefineWithParameters() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidPragmaDefineWithParameters",
			"extern pragma de.s42.dl.pragmas.DLPragmaTest$TestPragma(6) @noGenerics;");
	}
}
