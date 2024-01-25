// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 * 
 * Copyright 2024 Studio 42 GmbH ( https://www.s42m.de ).
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
package de.s42.dl.instances.base;

import de.s42.dl.DLModule;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidValue;
import de.s42.dl.exceptions.ParserException;
import java.util.Map;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author Benjamin Schiller
 */
public class EnvironmentTest
{
	//private final static Logger log = LogManager.getLogger(EnvironmentTest.class.getName());

	@Test
	public void retrievePropsFromEnv() throws DLException
	{
		DefaultCore core = new DefaultCore();

		DLModule module = core.parse("retrievePropsFromEnv",
			"Object t1 : $env.props;"
		);

		Object t1 = module.get("t1");

		assertTrue(t1 instanceof Map);
	}

	@Test
	public void invalidRetrieveUnsetEnvVariable() throws DLException
	{
		DefaultCore core = new DefaultCore();

		try {
			core.parse("invalidRetrieveUnsetEnvVariable",
				"String t1 : $env.props.NOT_SET_1234;"
			);
		} catch (InvalidValue ex) {
			assertTrue(ex.getCause() instanceof ParserException);
			ParserException pEx = (ParserException) ex.getCause();
			assertEquals(pEx.getStartOffset(), 11);
			assertEquals(pEx.getEndOffset(), 23);
		}
	}
}
