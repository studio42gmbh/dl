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
import de.s42.dl.instances.SimpleTypeDLInstance;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * This shows some of the potential of expressions in DL
 * See https://github.com/studio42gmbh/dl/issues/20
 *
 * @author Benjamin Schiller
 */
public class ReferenceExpressionsTest
{

	@Test
	public void validExpressionWithJavaSetData() throws DLException
	{
		DefaultCore core = new DefaultCore();
		SimpleTypeDLInstance<Integer> width = (SimpleTypeDLInstance<Integer>)core.addExported("width", 640);
		SimpleTypeDLInstance<Integer> height = (SimpleTypeDLInstance<Integer>)core.addExported("height", 400);
		DLModule module = core.parse("Anonymous", "Integer pixels : $width * $height;");
		Assert.assertEquals(module.getInt("pixels"), width.getData() * height.getData());
	}

	@Test
	public void validExpressionWithComplexTypeAndStatic() throws DLException
	{
		DLCore core = new DefaultCore();
		DLModule module = core.parse("Anonymous",
			"type T { Integer val; }"
			+ "T t { val : 123; }"
			+ "Integer p : $t.val * 654.5;");
		Assert.assertEquals(module.getInt("p"), (int)(123 * 654.5));
	}
}
