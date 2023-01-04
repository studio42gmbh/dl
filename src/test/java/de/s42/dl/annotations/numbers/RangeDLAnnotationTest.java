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
package de.s42.dl.annotations.numbers;

import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.exceptions.InvalidValue;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class RangeDLAnnotationTest
{

	@Test
	public void simpleRangeAnnotation() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("simpleRangeAnnotation",
			"type T { double v @range(min : 5, max : 20); } T t { v : 7.5654; }"
		);
	}

	@Test(expectedExceptions = InvalidInstance.class)
	public void invalidGreaterAnnotationBelowMin() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidGreaterAnnotationBelowMin",
			"type T { double v @range(10.0, 20.0); } T t { v : 9.9; }"
		);
	}

	@Test
	public void derivedLongWithRangeAnnotationType() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("derivedLongWithRangeAnnotationType",
			"type RLong @range(10, 20) extends Long; RLong t : 12;"
		);
	}

	@Test(expectedExceptions = InvalidValue.class)
	public void invalidDerivedLongWithRangeAnnotationTypeAboveMax() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidDerivedLongWithRangeAnnotationTypeAboveMax",
			"type RLong @range(10, 20) extends Long; RLong t : 22;"
		);
	}
}
