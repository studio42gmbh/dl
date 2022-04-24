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
package de.s42.dl.instances;

import de.s42.dl.DLModule;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidType;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class SimpleTypeDLInstanceNGTest
{

	public static class ComplexType
	{

		public String val;
	}

	@Test
	public void validDoubleType() throws DLException
	{
		DefaultCore core = new DefaultCore();
		SimpleTypeDLInstance<Double> value = core.addExported("value", 1.23);
		DLModule module = core.parse("Anonymous", "Double dlValue : $value;");
		Assert.assertEquals((double) module.getDouble("dlValue"), (double) value.getData());
	}

	/**
	 * Even list types with generics can easily be added and assigned properly also without unboxing of
	 * SimpleTypeDLInstance
	 * See https://github.com/studio42gmbh/dl/issues/13
	 *
	 * @throws DLException not thrown
	 */
	@Test
	public void validListType() throws DLException
	{
		DefaultCore core = new DefaultCore();
		List<String> value = core.addExported("value", new ArrayList<>(List.of("a", "b"))).getData();
		DLModule module = core.parse("Anonymous", "List<String> dlValue : $value;");
		Assert.assertEquals(module.get("dlValue"), value);
	}

	/**
	 * @throws DLException expected -> "Can not be created for complex type like
	 * de.s42.dl.instances.SimpleTypeDLInstanceNGTest$ComplexType"
	 */
	@Test(expectedExceptions = InvalidType.class)
	public void invalidComplexType() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.defineType(ComplexType.class);
		core.addExported("value", new ComplexType());
	}

}
