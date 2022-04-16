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
package de.s42.dl.attributes;

import de.s42.dl.DLAttribute;
import de.s42.dl.DLCore;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.types.StringDLType;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class DLAttributesTest
{

	private final static Logger log = LogManager.getLogger(DLAttributesTest.class.getName());

	@Test
	public void validComplexNestedInstanceAssignment() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type C { String d; } type A { C e @required; C c @required; } type B { A b @required; } B test { b : A a1 { e : C c1 { d : YAY1; }; c : C c2 { d : YAY2; }; }; }");
	}

	@Test
	public void validAttributeToString()
	{
		DLCore core = new DefaultCore();
		DLAttribute attribute = new DefaultDLAttribute("testAttribute", core.getType(StringDLType.DEFAULT_SYMBOL).get());
		String toString = attribute.toString();
		log.debug("toString", toString);
	}
}
