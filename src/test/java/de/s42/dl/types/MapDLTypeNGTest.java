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

import de.s42.dl.DLCore;
import de.s42.dl.DLInstance;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidType;
import de.s42.dl.exceptions.InvalidValue;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class MapDLTypeNGTest
{

	@Test
	public void validMapInDL() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { Map data; } T t @export { data : a, 1, b, 3; }");
		DLInstance instance = core.getExported("t").orElseThrow();
		Map data = instance.get("data");

		Assert.assertEquals(data, Map.of("a", 1L, "b", 3L));
	}

	@Test
	public void validMapWithGenericsStringIntegerInDL() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { Map<String, Integer> data; } T t @export { data : a, 1, b, 3; }");
		DLInstance instance = core.getExported("t").orElseThrow();
		Map data = instance.get("data");

		Assert.assertEquals(data, Map.of("a", 1, "b", 3));
	}

	@Test(expectedExceptions = InvalidValue.class)
	public void invalidMapWithWrongNumberOfInput() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { Map<String, Integer> data; } T t @export { data : a, 1, b; }");
	}

	@Test(expectedExceptions = InvalidValue.class)
	public void invalidMapWithWrongTypesOfInput() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { Map<Integer, Integer> data; } T t @export { data : a, 1; }");
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidMapWithWrongCountOfGenerics1() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { Map<Integer> data; }");
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidMapWithWrongCountOfGenerics3() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { Map<Integer, Integer, Integer> data; }");
	}
}
