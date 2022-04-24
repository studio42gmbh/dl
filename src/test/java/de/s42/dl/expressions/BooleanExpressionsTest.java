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
package de.s42.dl.expressions;

import de.s42.dl.*;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidValue;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * This shows some of the potential of expressions in DL
 * See https://github.com/studio42gmbh/dl/issues/20
 *
 * @author Benjamin Schiller
 */
public class BooleanExpressionsTest
{

	@Test
	public void validExpressionBoolean() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("Anonymous",
			"boolean t : true; "
			+ "boolean t2 : ( true & $t ) | ( $t == false ) ;");
		boolean t = false;
		Assert.assertEquals(module.getBoolean("t2"), (true & t) | (t == false));
	}

	@Test
	public void validExpressionStringCompareEqual() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("Anonymous",
			"String t : apple; "
			+ "boolean t2 : $t == apple ;");
		Assert.assertTrue(module.getBoolean("t2"));
	}

	@Test
	public void validExpressionStringCompareTypeNotEqual() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("Anonymous",
			"String t : apple; "
			+ "boolean t2 : $t != 7.345 ;");
		Assert.assertTrue(module.getBoolean("t2"));
	}

	@Test
	public void validExpressionStringCompareTwoRefs() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("Anonymous",
			"String t : apple; "
			+ "String t2 : orange; "
			+ "boolean t3 : $t != $t2 ;");
		Assert.assertTrue(module.getBoolean("t3"));
	}
}
