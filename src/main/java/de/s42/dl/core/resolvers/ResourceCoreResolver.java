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
public class ResourceCoreResolver implements DLCoreResolver
{

	private final static Logger log = LogManager.getLogger(ResourceCoreResolver.class.getName());

	protected final DLCore core;

	public ResourceCoreResolver(DLCore core)
	{
		assert core != null;

		this.core = core;
	}

	@Override
	public boolean canParse(String moduleId)
	{
		return ResourceHelper.hasResource(moduleId);
	}

	@Override
	public boolean canParse(String moduleId, String data)
	{
		return false;
	}

	@Override
	public DLModule parse(String moduleId) throws DLException
	{
		try {
			assert moduleId != null;

			log.debug("Parsing resource " + moduleId);

			Optional<String> res = ResourceHelper.getResourceAsString(moduleId);

			if (res.isEmpty()) {
				throw new InvalidModule("Resource " + moduleId + " could not be loaded");
			}

			return DLHrfParsing.parse(core, moduleId, res.get());
		} catch (IOException ex) {
			throw new InvalidModule("Error loading module from resource - " + ex.getMessage(), ex);
		}
	}

	@Override
	public DLModule parse(String moduleId, String data) throws InvalidModule
	{
		throw new InvalidModule("Error can just load module from resource");
	}

	public DLCore getCore()
	{
		return core;
	}
}
