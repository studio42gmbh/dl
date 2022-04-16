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

import de.s42.dl.DLCore;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.UndefinedAnnotation;
import de.s42.dl.exceptions.InvalidAnnotation;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class DLAnnotationsTest
{

	public static class TestAnnotation extends AbstractDLAnnotation
	{

		public TestAnnotation()
		{
			super("test");
		}
	}

	@Test(expectedExceptions = {InvalidAnnotation.class})
	public void invalidParametersEmptyBracketsAnnotationForJavaType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type Java @contain();");
	}

	@Test(expectedExceptions = {InvalidAnnotation.class})
	public void invalidParametersNoneAnnotationForJavaType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type Java @contain;");
	}

	@Test
	public void validExternAnnotation() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "extern annotation required;");
	}

	@Test(expectedExceptions = UndefinedAnnotation.class)
	public void invalidUndefinedExternAnnotation() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "extern annotation notDefined;");
	}

	// @todo DL allow to define custom annotations within dl
	@Test(enabled = false)
	public void validAnnotationDefinition() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "annotation ;");
	}

}
