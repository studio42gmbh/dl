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

import de.s42.dl.DLAnnotated.DLMappedAnnotation;
import de.s42.dl.DLCore;
import de.s42.dl.DLInstance;
import de.s42.dl.annotations.GreaterDLAnnotation.greater;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.exceptions.InvalidInstance;
import java.util.List;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 *
 * @author Benjamin Schiller
 */
public class GreaterDLAnnotationTest
{

	@Test
	public void validGreaterAnnotations() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { double min; double max @greater(\"min\"); } T t { min : 1.0; max : 2.0; }");
		core.parse("Anonymous2", "type T2 { float min; float max @greater(\"min\"); } T2 t2 { min : 1.0; max : 2.0; }");
		core.parse("Anonymous3", "type T3 { int min; int max @greater(\"min\"); } T3 t3 { min : 1; max : 2; }");
		core.parse("Anonymous4", "type T4 { long min; long max @greater(\"min\"); } T4 t4 { min : 1; max : 2; }");
		core.parse("Anonymous5", "type T5 { short min; short max @greater(\"min\"); } T5 t5 { min : 1; max : 2; }");
		core.parse("Anonymous6", "type T6 { String min; String max @greater(\"min\"); } T6 t6 { min : aa; max : bb; }");
		core.parse("Anonymous7", "type T7 { String min; String max @greater(other : \"min\"); } T7 t7 { min : aa; max : bb; }");
	}

	@Test(expectedExceptions = InvalidAnnotation.class)
	public void invalidGreaterAnnotationsInvalidNamedParameter() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { double min; double max @greater(wrong :\"min\"); } T t { min : 1.0; max : 2.0; }");
	}

	@Test(expectedExceptions = InvalidInstance.class)
	public void invalidGreaterAnnotationsLesserDoubles() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { double min; double max @greater(\"min\"); } T t { min : 2.0; max : 1.0; }");
	}

	@Test(expectedExceptions = InvalidInstance.class)
	public void invalidGreaterAnnotationsEqualDoubles() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { double min; double max @greater(\"min\"); } T t { min : 1.0; max : 1.0; }");
	}

	@Test(expectedExceptions = InvalidInstance.class)
	public void invalidGreaterAnnotationsStrings() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { String min; String max @greater(\"min\"); } T t { min : bb; max : aa; }");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void invalidGreaterAnnotationsBooleans() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { boolean min; boolean max @greater(\"min\"); } T t { min : false; max : true; }");
	}

	// Intuitive and typed dl annotations in java! :)
	public static class MyAnnotatedClass
	{

		@greater(other = "min")
		public int max;

		public int min;
	}

	@Test
	public void validCustomAnnotationInJava() throws Exception
	{
		// Find annotations on classes
		List<DLMappedAnnotation> mappedAnnotations = DLAnnotationHelper.getDLAnnotations(MyAnnotatedClass.class.getField("max").getAnnotations());

		Assert.assertEquals(mappedAnnotations.size(), 1);
		Assert.assertTrue(mappedAnnotations.get(0).getAnnotation() instanceof GreaterDLAnnotation);

		DLMappedAnnotation mappedAnnotation = mappedAnnotations.get(0);
		GreaterDLAnnotation annotation = mappedAnnotation.getAnnotation();

		Assert.assertEquals(mappedAnnotation.getParameters()[0], "min");
		Assert.assertEquals(annotation.getOther(mappedAnnotation), "min");

		DefaultCore core = new DefaultCore();
		core.defineType(MyAnnotatedClass.class);

		MyAnnotatedClass javaInstance = new MyAnnotatedClass();
		javaInstance.max = 1;

		DLInstance instance = core.convertFromJavaObject(javaInstance);

		// Load special annotations in BaseDL when convertFromJavaObject
		Assert.assertTrue(instance.getType().getAttribute("max").orElseThrow().getAnnotation(GreaterDLAnnotation.class).isPresent());

		DLMappedAnnotation mappedAnnotation2 = instance.getType().getAttribute("max").orElseThrow().getAnnotation(GreaterDLAnnotation.class).orElseThrow();
		GreaterDLAnnotation annotation2 = mappedAnnotation.getAnnotation();

		Assert.assertEquals(annotation2.getOther(mappedAnnotation2), "min");
	}
}
