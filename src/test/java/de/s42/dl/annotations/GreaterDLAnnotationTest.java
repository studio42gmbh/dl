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
import de.s42.dl.annotations.attributes.GreaterDLAnnotation;
import de.s42.dl.annotations.attributes.GreaterDLAnnotation.greater;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 *
 * @author Benjamin Schiller
 */
public class GreaterDLAnnotationTest
{

	private final static Logger log = LogManager.getLogger(GreaterDLAnnotationTest.class.getName());

	public static class GreaterTestClass
	{

		@greater(other = "min")
		public int max;

		public int min;
	}

	@Test
	public void validContainJavaType() throws DLException
	{
		DefaultCore core = new DefaultCore();
		DLType type = core.defineType(GreaterTestClass.class);
		//log.debug("DLType:\n", DLHelper.describe(type));

		DLAttribute max = type.getAttribute("max").orElseThrow();

		Assert.assertTrue(max.hasAnnotation(GreaterDLAnnotation.class), "@greater should be mapped for type GreaterTestClass");

		GreaterDLAnnotation annotation = (GreaterDLAnnotation) max.getAnnotation(GreaterDLAnnotation.class).orElseThrow();

		Assert.assertEquals(annotation.getOther(), "min");
	}

	@Test
	public void validGreaterAnnotations() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("validGreaterAnnotations", "type T { double min; double max @greater(\"min\"); } T t { min : 1.0; max : 2.0; }");
		core.parse("validGreaterAnnotations2", "type T2 { float min; float max @greater(\"min\"); } T2 t2 { min : 1.0; max : 2.0; }");
		core.parse("validGreaterAnnotations3", "type T3 { int min; int max @greater(\"min\"); } T3 t3 { min : 1; max : 2; }");
		core.parse("validGreaterAnnotations4", "type T4 { long min; long max @greater(\"min\"); } T4 t4 { min : 1; max : 2; }");
		core.parse("validGreaterAnnotations5", "type T5 { short min; short max @greater(\"min\"); } T5 t5 { min : 1; max : 2; }");
		core.parse("validGreaterAnnotations6", "type T6 { String min; String max @greater(\"min\"); } T6 t6 { min : aa; max : bb; }");
		core.parse("validGreaterAnnotations7", "type T7 { String min; String max @greater(other : \"min\"); } T7 t7 { min : aa; max : bb; }");
	}

	@Test(expectedExceptions = InvalidAnnotation.class)
	public void invalidGreaterAnnotationsInvalidNamedParameter() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidGreaterAnnotationsInvalidNamedParameter", "type T { double min; double max @greater(wrong :\"min\"); } T t { min : 1.0; max : 2.0; }");
	}

	@Test(expectedExceptions = InvalidInstance.class)
	public void invalidGreaterAnnotationsLesserDoubles() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidGreaterAnnotationsLesserDoubles", "type T { double min; double max @greater(\"min\"); } T t { min : 2.0; max : 1.0; }");
	}

	@Test(expectedExceptions = InvalidInstance.class)
	public void invalidGreaterAnnotationsEqualDoubles() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidGreaterAnnotationsEqualDoubles", "type T { double min; double max @greater(\"min\"); } T t { min : 1.0; max : 1.0; }");
	}

	@Test(expectedExceptions = InvalidInstance.class)
	public void invalidGreaterAnnotationsStrings() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidGreaterAnnotationsStrings", "type T { String min; String max @greater(\"min\"); } T t { min : bb; max : aa; }");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void invalidGreaterAnnotationsBooleans() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidGreaterAnnotationsBooleans", "type T { boolean min; boolean max @greater(\"min\"); } T t { min : false; max : true; }");
	}
}
