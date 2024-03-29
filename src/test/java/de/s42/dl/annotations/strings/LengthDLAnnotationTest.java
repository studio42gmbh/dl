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
package de.s42.dl.annotations.strings;

import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.exceptions.InvalidInstance;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class LengthDLAnnotationTest
{

	@Test
	public void simpleLengthAnnotations() throws DLException
	{
		DefaultCore core = new DefaultCore();

		core.parse("simpleLengthAnnotations",
			"type T { String v @length(max : 20); }"
			+ "T t { v : \"LongEnough\"; }"
			+ "type T2 { String v @length(5, 20); }"
			+ "T2 t2 { v : \"LongEnough\"; }"
			+ "type T3 { String v @length(max : 20, min : 5); }"
			+ "T3 t3 { v : \"LongEnough\"; }"
		);
	}

	@Test(
		expectedExceptions = InvalidInstance.class,
		expectedExceptionsMessageRegExp = "Error validating instance.*Value has to be min 10 chars but is 8 in @length.*"
	)
	public void invalidLengthAnnotations() throws DLException
	{
		DefaultCore core = new DefaultCore();

		core.parse("invalidLengthAnnotations",
			"type T { String v @length(10, 20); }"
			+ "T t { v : \"TooShort\"; }"
		);
	}

	@Test(
		expectedExceptions = InvalidAnnotation.class,
		expectedExceptionsMessageRegExp = "Error binding annotation @length to attribute 'v' - min : '20' > max : '10'.*"
	)
	public void invalidLengthAnnotationParametersMinGreaterMax() throws DLException
	{
		DefaultCore core = new DefaultCore();

		core.parse("invalidLengthAnnotationParametersMinGreaterMax",
			"type T { String v @length(20, 10); }"
		);
	}

	@Test(
		expectedExceptions = InvalidAnnotation.class,
		expectedExceptionsMessageRegExp = "Parameters are not valid for annotation @length.*Parameter 'min' validation IsGreaterEqual0 failed.*"
	)
	public void invalidLengthAnnotationParametersMinSmaller0() throws DLException
	{
		DefaultCore core = new DefaultCore();

		core.parse("invalidLengthAnnotationParametersMinSmaller0",
			"type T { String v @length(-1, 10); }"
		);
	}
}
