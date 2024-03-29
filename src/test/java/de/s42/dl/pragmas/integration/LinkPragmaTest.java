// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 * 
 * Copyright 2023 Studio 42 GmbH ( https://www.s42m.de ).
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
package de.s42.dl.pragmas.integration;

import de.s42.dl.DLType;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.util.DLHelper;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author Benjamin Schiller
 */
public class LinkPragmaTest
{

	private final static Logger log = LogManager.getLogger(LinkPragmaTest.class.getName());

	@Test(enabled = false)
	public void simpleLinkPragma() throws Exception
	{
		
		for (int i = 0;i<10;++i) {
		
			log.start("simpleLinkPragma");
			
			DefaultCore core = new DefaultCore();

			core.parse("simpleLinkPragma",
				"pragma link(\"../../../../jenomics/development/ag/ag-lib/target/ag-lib-0.1.jar\");"
				+ "extern type java.awt.Insets alias Insets;"
				+ "extern type de.jenomics.ag.nodes.editor.EditorCell alias EditorCell;"
			);

			log.stopInfo("simpleLinkPragma");

			DLType type = core.getType("EditorCell").orElseThrow();

			log.info(DLHelper.describe(type));

			assertEquals(type.getCanonicalName(), "de.jenomics.ag.nodes.editor.EditorCell");
		}

	}
}
