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
import de.s42.dl.exceptions.InvalidInstance;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class GreaterEqualDLAnnotationTest
{

	@Test
	public void validGreaterAnnotations() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { double min; double max @greaterEqual(\"min\"); } T t { min : 1.0; max : 2.0; }");
		core.parse("Anonymous2", "type T2 { float min; float max @greaterEqual(\"min\"); } T2 t2 { min : 1.0; max : 2.0; }");
		core.parse("Anonymous3", "type T3 { int min; int max @greaterEqual(\"min\"); } T3 t3 { min : 1; max : 2; }");
		core.parse("Anonymous4", "type T4 { long min; long max @greaterEqual(\"min\"); } T4 t4 { min : 1; max : 2; }");
		core.parse("Anonymous4", "type T5 { short min; short max @greaterEqual(\"min\"); } T5 t5 { min : 1; max : 2; }");
		core.parse("Anonymous5", "type T6 { String min; String max @greaterEqual(\"min\"); } T6 t6 { min : aa; max : bb; }");
		core.parse("Anonymous6", "type T7 { double min; double max @greaterEqual(\"min\"); } T7 t7 { min : 1.0; max : 1.0; }");
		core.parse("Anonymous7", "type T8 { float min; float max @greaterEqual(\"min\"); } T8 t8 { min : 1.0; max : 1.0; }");
		core.parse("Anonymous8", "type T9 { int min; int max @greaterEqual(\"min\"); } T9 t9 { min : 1; max : 1; }");
		core.parse("Anonymous9", "type T10 { long min; long max @greaterEqual(\"min\"); } T10 t10 { min : 1; max : 1; }");
		core.parse("Anonymous10", "type T11 { short min; short max @greaterEqual(\"min\"); } T11 t11 { min : 1; max : 1; }");
		core.parse("Anonymous11", "type T12 { String min; String max @greaterEqual(\"min\"); } T12 t12 { min : aa; max : aa; }");
	}

	@Test(expectedExceptions = InvalidInstance.class)
	public void invalidGreaterAnnotationsLesserDoubles() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { double min; double max @greaterEqual(\"min\"); } T t { min : 2.0; max : 1.0; }");
	}
	
	@Test(expectedExceptions = InvalidInstance.class)
	public void invalidGreaterAnnotationsStrings() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { String min; String max @greaterEqual(\"min\"); } T t { min : bb; max : aa; }");
	}		
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void invalidGreaterAnnotationsBooleans() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { boolean min; boolean max @greaterEqual(\"min\"); } T t { min : false; max : true; }");
	}			
}
