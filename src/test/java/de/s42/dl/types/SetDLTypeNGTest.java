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
import java.util.HashSet;
import java.util.Set;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class SetDLTypeNGTest
{

	@Test
	public void validSetInDL() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { Set data; } T t @export { data : 1, a, true; }");
		DLInstance instance = core.getExported("t").orElseThrow();
		Set data = instance.get("data");
		Assert.assertEquals(data, Set.of(1L, "a", true));
	}

	@Test
	public void validSetGenericsInDL() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { Set<Integer> data; } T t @export { data : 1, 2, 3; }");
		DLInstance instance = core.getExported("t").orElseThrow();
		Set<Integer> data = instance.get("data");
		Assert.assertEquals(data, Set.of(1, 2, 3));
	}

	@Test
	public void validSetGenericsInJava() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.addExported("setData", new HashSet<>(Set.of(1, 2, 3)));
		core.parse("validSetGenericsInJava", "type T { Set data; } T t @export { data : $setData; }");
		DLInstance instance = core.getExported("t").orElseThrow();
		Set<Integer> data = (Set)((ComplexTypeDLInstance)instance.get("data")).getData();
		Assert.assertEquals(data, Set.of(1, 2, 3));
	}

	@Test(expectedExceptions = InvalidValue.class)
	public void invalidSetWrongData() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { Set<Integer> data; } T t @export { data : a, b, c; }");
	}

	@Test(expectedExceptions = UndefinedType.class)
	public void invalidListUndefinedGenericType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { Set<NotDefined> data; }");
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidListWrongNumberOfGenericTypes() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { Set<Integer, Integer> data; }");
	}
}
