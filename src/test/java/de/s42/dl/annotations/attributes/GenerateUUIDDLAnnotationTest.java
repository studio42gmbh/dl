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
import java.util.UUID;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author Benjamin Schiller
 */
public class GenerateUUIDDLAnnotationTest
{

	private final static Logger log = LogManager.getLogger(GenerateUUIDDLAnnotationTest.class.getName());

	@Test
	public void simpleGenerateUUID() throws DLException
	{
		DefaultCore core = new DefaultCore();

		DLModule module = core.parse("simpleGenerateUUID",
			"type T { UUID id @generateUUID @required; }"
			+ "T t {}"
			+ "T t2 { id : \"fd5a44e4-0158-4cfa-ba65-3064660891dc\"; }"
		);

		//log.info(module.getChild("t").orElseThrow().get("x"));
		assertTrue(module.getChild("t").orElseThrow().get("id") instanceof UUID);
		assertEquals(module.getChild("t2").orElseThrow().get("id"), UUID.fromString("fd5a44e4-0158-4cfa-ba65-3064660891dc"));
	}
}
