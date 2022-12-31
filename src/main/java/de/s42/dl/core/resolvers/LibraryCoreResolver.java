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

import de.s42.base.resources.ResourceHelper;
import de.s42.dl.DLCore;
import de.s42.dl.DLModule;
import de.s42.dl.core.DLCoreResolver;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidModule;
import de.s42.dl.parser.DLHrfParsing;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.io.IOException;
import java.util.Optional;

/**
 *
 * @author Benjamin Schiller
 */
public class LibraryCoreResolver implements DLCoreResolver
{

	public final static String LIB_PREFIX = "dl:";
	public final static String LIB_BASE_PATH = "de/s42/dl/lib/";

	private final static Logger log = LogManager.getLogger(LibraryCoreResolver.class.getName());

	protected final DLCore core;

	public LibraryCoreResolver(DLCore core)
	{
		assert core != null;

		this.core = core;
	}

	public String getContent(String moduleId) throws InvalidModule, IOException
	{
		String libraryModule = resolveModule(moduleId);

		Optional<String> res = ResourceHelper.getResourceAsString(libraryModule);

		if (res.isEmpty()) {
			throw new InvalidModule("Resource " + libraryModule + " could not be loaded");
		}

		return res.orElseThrow();
	}

	public String resolveModule(String moduleId)
	{
		assert moduleId != null;

		return moduleId.replace(LIB_PREFIX, LIB_BASE_PATH);
	}

	@Override
	public boolean canParse(String moduleId)
	{
		assert moduleId != null;

		// Just parse if prefix is given
		if (!moduleId.startsWith(LIB_PREFIX)) {
			return false;
		}

		String libraryModule = resolveModule(moduleId);

		return ResourceHelper.hasResource(libraryModule);
	}

	@Override
	public boolean canParse(String moduleId, String data)
	{
		return false;
	}

	@Override
	public DLModule parse(String moduleId) throws DLException
	{
		assert moduleId != null;

		try {

			String content = getContent(moduleId);

			return DLHrfParsing.parse(core, moduleId, content);
		} catch (IOException ex) {
			throw new InvalidModule("Error loading module from resource - " + ex.getMessage(), ex);
		}
	}

	@Override
	public DLModule parse(String moduleId, String data) throws InvalidModule
	{
		throw new InvalidModule("Error can just load module from lib resource");
	}

	public DLCore getCore()
	{
		return core;
	}
}
