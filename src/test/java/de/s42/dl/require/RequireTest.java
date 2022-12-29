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
package de.s42.dl.require;

import de.s42.dl.core.BaseDLCore;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.core.resolvers.LibraryCoreResolver;
import de.s42.dl.core.resolvers.StringCoreResolver;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class RequireTest
{

	private final static Logger log = LogManager.getLogger(RequireTest.class.getName());

	@Test
	public void requireBaseLibraryInDefaultDLCore() throws Exception
	{
		DefaultCore core = new DefaultCore();
		core.parse("requireBaseLibraryInDefaultDLCore",
			"require \"dl:standard/base.dl\";"
		);
	}

	@Test
	public void requireBaseLibraryInBaseDLCore() throws Exception
	{
		BaseDLCore core = new BaseDLCore(true);
		core.addResolver(new StringCoreResolver(core));
		core.addResolver(new LibraryCoreResolver(core));
		core.parse("requireBaseLibraryInBaseDLCore",
			"require \"dl:standard/base.dl\";"
		);
	}
}
