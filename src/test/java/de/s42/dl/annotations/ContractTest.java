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

import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.exceptions.InvalidValue;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class ContractTest
{

	private final static Logger log = LogManager.getLogger(ContractTest.class.getName());

	@Test
	public void simpleContract() throws DLException, RuntimeException
	{
		DefaultCore core = new DefaultCore();
		core.parse("simpleContract",
			"annotation evenRange42 : @range(min : 0, max : 42);"
			+ "type Long42 @evenRange42 extends Long;"
			+ "Long42 i42 : 21;"
		);
	}

	@Test(expectedExceptions = {InvalidValue.class})
	public void invalidSimpleContract() throws DLException, RuntimeException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidSimpleContract",
			"annotation evenRange42 : @range(min : 0, max : 42);"
			+ "type Long42 @evenRange42 extends Long;"
			+ "Long42 i42 : 43;"
		);
	}

	@Test
	public void simpleNotContract() throws DLException, RuntimeException
	{
		DefaultCore core = new DefaultCore();
		core.parse("simpleNotContract",
			"annotation evenRange42 : !@range(min : 0, max : 42);"
			+ "type Long42 @evenRange42 extends Long;"
			+ "Long42 i42 : -3;"
		);
	}

	@Test(expectedExceptions = {InvalidValue.class})
	public void invalidSimpleNotContract() throws DLException, RuntimeException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidSimpleNotContract",
			"annotation evenRange42 : !@range(min : 0, max : 42);"
			+ "type Long42 @evenRange42 extends Long;"
			+ "Long42 i42 : 21;"
		);
	}

	@Test
	public void simpleAndContract() throws DLException, RuntimeException
	{
		DefaultCore core = new DefaultCore();
		core.parse("simpleAndContract",
			"annotation evenRange42 : @range(min : 0, max : 42) & @even;"
			+ "type Long42 @evenRange42 extends Long;"
			+ "Long42 i42 : 0;"
		);
	}

	@Test(expectedExceptions = {InvalidAnnotation.class})
	public void invalidSimpleAndContract() throws DLException, RuntimeException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidSimpleAndContract",
			"annotation evenRange42 : @range(min : 0, max : 42) & @undefined;"
		);
	}

	@Test(expectedExceptions = {InvalidValue.class})
	public void invalidInstanceDoesNotHoldSimpleAndContract() throws DLException, RuntimeException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidInstanceDoesNotHoldSimpleAndContract",
			"annotation evenRange42 : @range(min : 0, max : 42) & @even;"
			+ "type Long42 @evenRange42 extends Long;"
			+ "Long42 i42 : -1;"
		);
	}

	@Test
	public void simpleOrContract() throws DLException, RuntimeException
	{
		DefaultCore core = new DefaultCore();
		core.parse("simpleOrContract",
			"annotation evenRange42 : @range(min : 0, max : 42) | @even;"
			+ "type Long42 @evenRange42 extends Long;"
			+ "Long42 i1 : 1;"
			+ "Long42 i2 : 2;"
			+ "Long42 i3 : 44;"
		);
	}

	@Test(expectedExceptions = {InvalidAnnotation.class})
	public void invalidSimpleOrContract() throws DLException, RuntimeException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidSimpleOrContract",
			"annotation evenRange42 : @range(min : 0, max : 42) | @undefined;"
		);
	}

	@Test(expectedExceptions = {InvalidValue.class})
	public void invalidInstanceDoesNotHoldSimpleOrContract() throws DLException, RuntimeException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidInstanceDoesNotHoldSimpleOrContract",
			"annotation evenRange42 : @range(min : 0, max : 42) | @even;"
			+ "type Long42 @evenRange42 extends Long;"
			+ "Long42 i42 : -1;"
		);
	}

	@Test
	public void simpleEqualsContract() throws DLException, RuntimeException
	{
		DefaultCore core = new DefaultCore();
		core.parse("simpleEqualsContract",
			"annotation evenRange42 : @range(min : 0, max : 42) == @even;"
			+ "type Long42 @evenRange42 extends Long;"
			+ "Long42 i1 : -1;"
			+ "Long42 i2 : 2;"
			+ "Long42 i3 : 43;"
		);
	}

	@Test(expectedExceptions = {InvalidAnnotation.class})
	public void invalidSimpleEqualsContract() throws DLException, RuntimeException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidSimpleEqualsContract",
			"annotation evenRange42 : @range(min : 0, max : 42) == @undefined;"
		);
	}

	@Test(expectedExceptions = {InvalidValue.class})
	public void invalidInstanceDoesNotHoldSimpleEqualsContract() throws DLException, RuntimeException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidInstanceDoesNotHoldSimpleEqualsContract",
			"annotation evenRange42 : @range(min : 0, max : 42) == @even;"
			+ "type Long42 @evenRange42 extends Long;"
			+ "Long42 i42 : 1;"
		);
	}

	@Test
	public void simpleXorContract() throws DLException, RuntimeException
	{
		DefaultCore core = new DefaultCore();
		core.parse("simpleXorContract",
			"annotation evenRange42 : @range(min : 0, max : 42) != @even;"
			+ "type Long42 @evenRange42 extends Long;"
			+ "Long42 i1 : -2;"
			+ "Long42 i2 : 3;"
			+ "Long42 i3 : 41;"
			+ "Long42 i4 : 44;"
		);
	}

	@Test(expectedExceptions = {InvalidAnnotation.class})
	public void invalidSimpleXorContract() throws DLException, RuntimeException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidSimpleXorContract",
			"annotation evenRange42 : @range(min : 0, max : 42) != @undefined;"
		);
	}

	@Test(expectedExceptions = {InvalidValue.class})
	public void invalidInstanceDoesNotHoldSimpleXorContract() throws DLException, RuntimeException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidInstanceDoesNotHoldSimpleXorContract",
			"annotation evenRange42 : @range(min : 0, max : 42) != @even;"
			+ "type Long42 @evenRange42 extends Long;"
			+ "Long42 i42 : 2;"
		);
	}
		
	@Test
	public void complexContract() throws DLException, RuntimeException
	{
		DefaultCore core = new DefaultCore();
		core.parse("complexContract",
			"annotation evenRange42 : (@range(min : 0, max : 42) & ( @even | @range(min : 0, max : 21) )) | @range(min : 23, max : 23);\n"
			+ "type LongComplicated @evenRange42 extends Long;\n"
			+ "LongComplicated i1 : 21;\n"
			+ "LongComplicated i2 : 23;\n"
			+ "LongComplicated i3 : 42;\n"
			+ "LongComplicated i4 : 1;\n"
			+ "type LCContainer { LongComplicated val; }\n"
			+ "LCContainer lcc { val : 1; }"
		);
		//DLType type = core.getType("LongComplicated").orElseThrow();
		//log.info(DLHelper.describe(type));
	}
}
