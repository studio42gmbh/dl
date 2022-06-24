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
import de.s42.dl.exceptions.UndefinedType;
import de.s42.dl.instances.ComplexTypeDLInstance;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class ListDLTypeNGTest
{

	@Test
	public void validListInDL() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { List data; } T t @export { data : 1, a, true; }");
		DLInstance instance = core.getExported("t").orElseThrow();
		List data = instance.get("data");
		Assert.assertEquals(data, List.of(1L, "a", true));
	}

	@Test
	public void validListGenericsInDL() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { List<Integer> data; } T t @export { data : 1, 2, 3; }");
		DLInstance instance = core.getExported("t").orElseThrow();
		List data = instance.get("data");
		Assert.assertEquals(data, List.of(1, 2, 3));
	}

	@Test
	public void validListGenericsInJava() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.defineAliasForType(
			"java.util.ImmutableCollections$ListN",
			core.getType(List.class).orElseThrow());
		core.addExported("listData", List.of(1, 2, 3));
		core.parse("Anonymous", "type T { List data; } T t @export { data : $listData; }");
		DLInstance instance = core.getExported("t").orElseThrow();
		// https://github.com/studio42gmbh/dl/issues/13 avoid sketchy SimpleTypeDLInstance wrappings when getting values from instances that were added with addExported
		List data = (List)((ComplexTypeDLInstance)instance.get("data")).getData();
		Assert.assertEquals(data, List.of(1, 2, 3));
	}

	@Test(expectedExceptions = InvalidValue.class)
	public void invalidListWrongData() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { List<Integer> data; } T t @export { data : a, b, c; }");
	}

	@Test(expectedExceptions = UndefinedType.class)
	public void invalidListUndefinedGenericType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { List<NotDefined> data; }");
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidListWrongNumberOfGenericTypes() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { List<Integer, Integer> data; }");
	}
}
