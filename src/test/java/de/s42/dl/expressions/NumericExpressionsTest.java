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
import de.s42.dl.exceptions.ParserException;
import java.util.List;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * This shows some of the potential of expressions in DL
 * See https://github.com/studio42gmbh/dl/issues/20
 *
 * @author Benjamin Schiller
 */
public class NumericExpressionsTest
{

	@Test
	public void integerHex() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("integerHex", "Integer t : 0xA1;");
		assertEquals(module.getInt("t"), 0xA1);
	}

	@Test
	public void integerBinary() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("integerBinary", "Integer t : 0b110101;");
		assertEquals(module.getInt("t"), 0b110101);
	}

	@Test
	public void integerOctal() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("integerOctal", "Integer t : 01425;");
		assertEquals(module.getInt("t"), 01425);
	}

	@Test
	public void expressionInteger() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("expressionInteger", "Integer t : (6 * (7 + 3) - 5 * (5 - 3)) / 3;");
		assertEquals(module.getInt("t"), (6 * (7 + 3) - 5 * (5 - 3)) / 3);
	}

	@Test
	public void expressionDouble() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("expressionDouble", "Double t : (6.5 * (7.12 + -3) - 5.3E2 * (5 - 3)) / 3.0;");
		assertEquals(module.getDouble("t"), (6.5 * (7.12 + -3) - 5.3E2 * (5 - 3)) / 3.0);
	}

	@Test
	public void expressionDoubleRef() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("expressionDoubleRef", "Double v : 1.33; Double t : (6.5 * (7.12 + -$v) - 5.3E2 * (5 - 3)) / 3.0;");
		assertEquals(module.getDouble("t"), (6.5 * (7.12 + (-1.33)) - 5.3E2 * (5 - 3)) / 3.0);
	}

	@Test
	public void expressionIntBitwiseAndOr() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("expressionIntBitwiseAndOr", "Integer t : 0b1000101 & 4 | 0x35;");
		assertEquals(module.getInt("t"), 0b1000101 & 4 | 0x35);
	}

	@Test
	public void expressionIntPow() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("expressionIntPow", "Integer t : 5 ^ 4;");
		assertEquals(module.getInt("t"), (int) Math.pow(5, 4));
	}

	@Test
	public void expressionDoublePow() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("expressionDoublePow", "Double t : 5.123 ^ 4.234;");
		assertEquals(module.getDouble("t"), Math.pow(5.123, 4.234));
	}

	@Test
	public void expressionExpressionsInMultiAssignment() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("expressionExpressionsInMultiAssignment", "List<Integer> t : 3 * 2, 3 + 2, 3 - 2, 3 / 2, 3 ^ 2, 3 & 2, 3 | 2;");
		assertEquals(module.get("t"), List.of(3 * 2, 3 + 2, 3 - 2, 3 / 2, (int) Math.pow(3, 2), 3 & 2, 3 | 2));
	}

	@Test
	public void invalidIntegerHex() throws DLException
	{
		try {
			DLCore core = new DefaultCore();
			core.parse("invalidIntegerHex", "Integer t : 0x1G;");
		} catch (ParserException ex) {
			assertEquals(ex.getStartOffset(), 15);
			assertEquals(ex.getEndOffset(), 15);
		}
	}

	@Test
	public void invalidIntegerBinary() throws DLException
	{
		try {
			DLCore core = new DefaultCore();
			core.parse("invalidIntegerBinary", "Integer t : 0b112;");
		} catch (ParserException ex) {
			assertEquals(ex.getStartOffset(), 16);
			assertEquals(ex.getEndOffset(), 16);
		}
	}

	@Test
	public void invalidIntegerOctal() throws DLException
	{
		try {
			DLCore core = new DefaultCore();
			core.parse("invalidIntegerOctal", "Integer t : 015268;");
		} catch (ParserException ex) {
			assertEquals(ex.getStartOffset(), 17);
			assertEquals(ex.getEndOffset(), 17);
		}
	}
}
