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
package de.s42.dl.types;

import de.s42.dl.DLInstance;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidType;
import de.s42.dl.exceptions.InvalidValue;
import de.s42.dl.util.DLHelper;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class ArrayDLTypeNGTest
{

	private final static Logger log = LogManager.getLogger(ArrayDLTypeNGTest.class.getName());

	public static enum State
	{
		NEW, PROGRESS, DONE
	}

	@Test
	public void validArrayOneElements() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("validArrayOneElements",
			"type A { Array<A> data; } A test1; A test2 { data : $test1; }"
		);
	}

	@Test
	public void validArrayMultipleElements() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("validArrayMultipleElements",
			"type A { Array<A> data; } A test1; A test2; A test3 { data : $test1, $test2; }"
		);
	}

	@Test(expectedExceptions = InvalidValue.class)
	public void invalidArrayWrongElementType() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidArrayWrongElementType",
			"type A { Array<A> data; } type B; A test1; B test2; A test3 { data : $test1, $test2; }"
		);
	}

	@Test
	public void validArrayWithGenericTypeString() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("validArrayWithGenericTypeString",
			"type A { Array<String> data; } A test @export { data : a, b, c; }"
		);
		DLInstance instance = core.getExported("test").orElseThrow();
		Assert.assertEquals(instance.get("data"), new String[]{"a", "b", "c"});
	}

	@Test
	public void validArrayWithGenericTypeLong() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("validArrayWithGenericTypeLong",
			"type A { Array<Long> data; } A test @export { data : 1, 2, 3; }"
		);
		DLInstance instance = core.getExported("test").orElseThrow();
		Assert.assertEquals(instance.get("data"), new Long[]{1L, 2L, 3L});
	}

	// https://github.com/studio42gmbh/dl/issues/9 properly convert the elements into Integer
	@Test
	public void validArrayWithGenericTypeInteger() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("validArrayWithGenericTypeInteger",
			"type A { Array<Integer> data; } A test @export { data : 1, 2, 3; }"
		);
		DLInstance instance = core.getExported("test").orElseThrow();
		Assert.assertEquals(instance.get("data"), new Integer[]{1, 2, 3});
	}

	@Test
	public void validArrayWithGenericTypeEnum() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.defineType(core.createEnum(State.class), "State");
		core.defineArrayType(State.class);
		core.parse("validArrayWithGenericTypeEnum",
			"type A { Array<State> data; } A test @export { data : NEW, PROGRESS, NEW; }"
		);
		DLInstance instance = core.getExported("test").orElseThrow();
		Assert.assertEquals(instance.get("data"), new State[]{State.NEW, State.PROGRESS, State.NEW});
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidArrayWrongNumberOfGenericTypes() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidArrayWrongNumberOfGenericTypes",
			"type T { Array<Integer, Integer> data; }"
		);
	}

	public static class TestData
	{

		protected String name;
		protected String[] data;

		public String[] getData()
		{
			return data;
		}

		public void setData(String[] data)
		{
			this.data = data;
		}

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}
	}

	@Test
	public void serializeAndDeserializeArrayData() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.defineType(TestData.class);

		TestData t1 = new TestData();
		t1.setData(new String[]{"A", "B", "C"});
		t1.setName("t1");

		DLInstance t1Instance = core.convertFromJavaObject(t1);

		//log.warn(Arrays.toString((String[])t1Instance.get("data")));
		String t1Str = DLHelper.toString(t1Instance, true, 2);

		//log.warn(t1Str);
	}
}
