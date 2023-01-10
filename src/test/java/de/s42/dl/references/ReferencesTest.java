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
package de.s42.dl.references;

import de.s42.dl.*;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.parser.DLHrfReferenceResolver;
import java.util.Optional;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * This shows some of the potential of expressions in DL
 * See https://github.com/studio42gmbh/dl/issues/20
 *
 * @author Benjamin Schiller
 */
public class ReferencesTest
{

	public static class Data
	{

		public int x;
		public String y;
		public Object z;
	}

	@Test
	public void simpleReference() throws DLException
	{
		DefaultCore core = new DefaultCore();

		DLModule module = core.parse("simpleReference",
			"type T { Integer val; }"
			+ "T t { val : 42; }"
			+ "T tr : $t;"
		);

		assertEquals(module.getInstance("tr").getType().getName(), "T");
	}

	@Test
	public void simpleReferencePath() throws DLException
	{
		DefaultCore core = new DefaultCore();

		DLModule module = core.parse("simpleReferencePath",
			"type T { Integer val; }"
			+ "T t { val : 42; }"
			+ "Integer p : $t.val;"
		);

		assertEquals(module.getInt("p"), 42);
	}

	@Test
	public void referencePath() throws DLException
	{
		DefaultCore core = new DefaultCore();

		DLModule module = core.parse("referencePath",
			"type T { Integer val; }"
			+ "T t { val : 42; }"
			+ "Integer p : $t.val;"
			+ "Integer p2 : $t.val.?test.t2;"
		);

		assertEquals(module.get("p"), 42);
		assertEquals(module.get("p2"), null);
	}
	
	@Test
	public void pathResolver() throws DLException
	{
		DefaultCore core = new DefaultCore();

		DLModule module = core.parse("referencePath",
			"type T contains T { Integer val; }"
			+ "T t { val : 42; }"
			+ "T deep { val : 1; T deeper  { val : 2; T bottom { val : 3; } } }"
		);
		
		DLReferenceResolver resolver = new DLHrfReferenceResolver();

		// Variations of ? optional
		Object val = resolver.resolve(module, "$t.val").orElse(null);				
		assertEquals(val, 42);

		val = resolver.resolve(module, "$?t.val").orElse(null);				
		assertEquals(val, 42);
		
		val = resolver.resolve(module, "$t.?val").orElse(null);				
		assertEquals(val, 42);
		
		val = resolver.resolve(module, "$?t.?val").orElse(null);				
		assertEquals(val, 42);

		// Resolve bean property
		val = resolver.resolve(module, "$t.val.class").orElse(null);				
		assertEquals(val, Integer.class);

		val = resolver.resolve(module, "$deep.val").orElse(null);				
		assertEquals(val, 1);
		val = resolver.resolve(module, "$deep.deeper.val").orElse(null);				
		assertEquals(val, 2);
		val = resolver.resolve(module, "$deep.deeper.bottom.val").orElse(null);				
		assertEquals(val, 3);
		val = resolver.resolve(module, "$deep.deeper.bottom.val.class").orElse(null);				
		assertEquals(val, Integer.class);

		// Undefined optional
		val = resolver.resolve(module, "$t.?undefined").orElse(null);				
		assertEquals(val, null);
		val = resolver.resolve(module, "$deep.deeper.bottom.?undefined").orElse(null);				
		assertEquals(val, null);

	}
	
}
