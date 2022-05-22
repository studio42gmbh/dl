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

import de.s42.dl.DLAnnotation;
import de.s42.dl.DLCore;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidAnnotation;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 *
 * @author Benjamin Schiller
 */
public class DLAnnotationTest
{

	public static class TestAnnotation extends TagDLAnnotation
	{

		public TestAnnotation()
		{
			super("test");
		}
	}

	/**
	 * Just extern annotations are allowed
	 *
	 * @throws DLException
	 * @throws RuntimeException expected -> "extraneous input 'annotation' expecting {<EOF>, 'type', 'extern',
	 * 'require', 'enum', 'abstract', 'alias', 'final', 'pragma', SYMBOL}"
	 */
	@Test(expectedExceptions = {RuntimeException.class})
	public void invalidInternalAnnotationNotAllowed() throws DLException, RuntimeException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "annotation T;");
	}

	@Test(expectedExceptions = {InvalidAnnotation.class})
	public void invalidParametersEmptyBracketsAnnotationForJavaType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T @contain();");
	}

	@Test(expectedExceptions = {InvalidAnnotation.class})
	public void invalidParametersNoneAnnotationForJavaType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T @contain;");
	}

	@Test
	public void validJavaDefinedAnnotation() throws DLException
	{
		DLCore core = new DefaultCore();
		core.defineAnnotation(new TestAnnotation());
		core.parse("Anonymous", "type T @test;");
		core.parse("Anonymous2", "type T2 @test();");
	}

	@Test
	public void validExternAnnotation() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "extern annotation de.s42.dl.annotations.DLAnnotationTest$TestAnnotation;");
		core.parse("Anonymous2", "type T @de.s42.dl.annotations.DLAnnotationTest$TestAnnotation;");
		core.parse("Anonymous3", "type T2 @test;");
	}

	@Test(expectedExceptions = InvalidAnnotation.class)
	public void invalidUndefinedExternAnnotation() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "extern annotation notDefined;");
	}
	
	@Test
	public void validExternAliasAnnotation() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "extern annotation de.s42.dl.annotations.DLAnnotationTest$TestAnnotation alias U, V;");
		DLAnnotation annotationU = core.getAnnotation("U").orElseThrow();
		Assert.assertEquals(annotationU.getName(), "test");
		DLAnnotation annotationV = core.getAnnotation("V").orElseThrow();
		Assert.assertEquals(annotationV.getName(), "test");
	}
}
