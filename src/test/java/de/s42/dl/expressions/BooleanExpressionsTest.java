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
	public void simpleEquals() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("simpleEquals",
			"boolean t : 7 == 3 + 4 ;"
			+ "boolean t2 : 7 == 13 - 4 ;");
		Assert.assertTrue(module.getBoolean("t"));
		Assert.assertFalse(module.getBoolean("t2"));
	}

	@Test
	public void simpleNotEquals() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("simpleNotEquals",
			"boolean t : 1.34 != 1.42 ;"
			+ "boolean t2 : 1.42 != 1.42 ;");
		Assert.assertTrue(module.getBoolean("t"));
		Assert.assertFalse(module.getBoolean("t2"));
	}

	@Test
	public void simpleLike() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("simpleLike",
			"boolean t : \"Star\" ~= \"^S.*r$\" ;"
			+ "boolean t2 : \"Star\" ~= \"^T$\" ;");
		Assert.assertTrue(module.getBoolean("t"));
		Assert.assertFalse(module.getBoolean("t2"));
	}

	@Test
	public void simpleLesser() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("simpleLesser",
			"boolean t : 9 < 11 ;"
			+ "boolean t2 : 19 < 11 ;");
		Assert.assertTrue(module.getBoolean("t"));
		Assert.assertFalse(module.getBoolean("t2"));
	}

	@Test
	public void simpleLesserEquals() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("simpleLesserEquals",
			"boolean t : 9 <= 11 ;"
			+ "boolean t2 : 11 <= 11 ;"
			+ "boolean t3 : 13 <= 11 ;");
		Assert.assertTrue(module.getBoolean("t"));
		Assert.assertTrue(module.getBoolean("t2"));
		Assert.assertFalse(module.getBoolean("t3"));
	}

	@Test
	public void simpleGreater() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("simpleGreater",
			"boolean t : 19 > 11 ;"
			+ "boolean t2 : 9 > 11 ;");
		Assert.assertTrue(module.getBoolean("t"));
		Assert.assertFalse(module.getBoolean("t2"));
	}

	@Test
	public void simpleGreaterEquals() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("simpleGreaterEquals",
			"boolean t : 19 >= 11 ;"
			+ "boolean t2 : 11 >= 11 ;"
			+ "boolean t3 : 8 >= 11 ;");
		Assert.assertTrue(module.getBoolean("t"));
		Assert.assertTrue(module.getBoolean("t2"));
		Assert.assertFalse(module.getBoolean("t3"));
	}

	@Test
	public void expressionBoolean() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("expressionBoolean",
			"boolean t : true; "
			+ "boolean t2 : ( true & $t ) | ( $t == false ) ;");
		boolean t = false;
		Assert.assertEquals(module.getBoolean("t2"), (true & t) | (t == false));
	}

	@Test
	public void expressionStringCompareEqual() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("expressionStringCompareEqual",
			"String t : apple; "
			+ "boolean t2 : $t == apple ;");
		Assert.assertTrue(module.getBoolean("t2"));
	}

	@Test
	public void expressionStringCompareTypeNotEqual() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("expressionStringCompareTypeNotEqual",
			"String t : apple; "
			+ "boolean t2 : $t != 7.345 ;");
		Assert.assertTrue(module.getBoolean("t2"));
	}

	@Test
	public void expressionStringCompareTwoRefs() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("expressionStringCompareTwoRefs",
			"String t : apple; "
			+ "String t2 : orange; "
			+ "boolean t3 : $t != $t2 ;");
		Assert.assertTrue(module.getBoolean("t3"));
	}
}
