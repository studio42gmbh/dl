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
package de.s42.dl.core.resolvers;

import de.s42.dl.DLCore;

/**
 *
 * @author Benjamin Schiller
 */
public class LibraryCoreResolver extends ResourceCoreResolver
{

	public final static String LIB_PREFIX = "dl:";
	public final static String LIB_BASE_PATH = "de/s42/dl/lib/";

	//private final static Logger log = LogManager.getLogger(LibraryCoreResolver.class.getName());
	@Override
	public String resolveModuleId(DLCore core, String moduleId)
	{
		assert moduleId != null;

		return moduleId.replace(LIB_PREFIX, LIB_BASE_PATH);
	}

	@Override
	public boolean canParse(DLCore core, String moduleId, String data)
	{
		if (moduleId == null) {
			return false;
		}

		// Just parse if prefix is given
		if (!moduleId.startsWith(LIB_PREFIX)) {
			return false;
		}

		return super.canParse(core, moduleId, data);
	}
}
