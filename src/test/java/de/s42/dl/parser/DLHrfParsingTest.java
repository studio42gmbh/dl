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
package de.s42.dl.parser;

import de.s42.dl.DLCore;
import de.s42.dl.DLModule;
import de.s42.dl.core.DefaultCore;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author Benjamin Schiller
 */
public class DLHrfParsingTest
{

	@Test
	public void validEscapeNewline() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "String s : \"\\n\";";
		DLModule result = DLHrfParsing.parse(core, "validEscapeNewline", data);
		String sVal = result.getString("s");
		assertEquals(sVal, "\n");
	}

	@Test
	public void validEscapeQuotes() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "String s : \"\\\"\";";
		DLModule result = DLHrfParsing.parse(core, "validEscapeQuotes", data);
		String sVal = result.getString("s");
		assertEquals(sVal, "\"");
	}

	@Test
	public void validEscapeBackslash() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "String s : \"\\\\\";";
		DLModule result = DLHrfParsing.parse(core, "validEscapeBackslash", data);
		String sVal = result.getString("s");
		assertEquals(sVal, "\\");
	}

	@Test
	public void validEscapeBackslashFollowedByNewline() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "String s : \"\\\\n\";";
		DLModule result = DLHrfParsing.parse(core, "validEscapeBackslashFollowedByNewline", data);
		String sVal = result.getString("s");
		assertEquals(sVal, "\\n");
	}

	@Test
	public void validCompbinedEscapes() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "String s : \"\\\\n\\n\\\\\\n\\\\\\\\\\n\\\\\\\\\\\\n\\n\\\"\";";
		DLModule result = DLHrfParsing.parse(core, "validCompbinedEscapes", data);
		String sVal = result.getString("s");
		assertEquals(sVal, "\\n\n\\\n\\\\\n\\\\\\n\n\"");
	}
}
