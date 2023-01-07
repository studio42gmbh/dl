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
package de.s42.dl.annotations.attributes;

import de.s42.dl.DLModule;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author Benjamin Schiller
 */
public class RequiredDLAnnotationTest
{

	private final static Logger log = LogManager.getLogger(RequiredDLAnnotationTest.class.getName());

	@Test
	public void simpleRequireType() throws DLException
	{
		DefaultCore core = new DefaultCore();

		DLModule module = core.parse("simpleRequire",
			"type RInt @required extends Integer;"
				+ "type T { RInt x; } T t { x  : 42; }"
		);

		assertEquals(module.getChild("t").orElseThrow().get("x"), 42);
	}
/*
	@Test(expectedExceptions = InvalidValue.class)
	public void invalidIsDirectoryAsTypeWrongPath() throws DLException
	{
		DefaultCore core = new DefaultCore();

		core.parse("invalidIsDirectoryAsTypeWrongPath",
			"type T @isDirectory extends Path; T t : \"wrong/directory/42%$!\";"
		);
	}
*/
}
