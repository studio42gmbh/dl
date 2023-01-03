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

	@Override
	public String getContent(DLCore core, String resolvedModuleId, String data) throws InvalidModule, IOException
	{
		assert core != null;
		assert resolvedModuleId != null;

		Optional<String> res = ResourceHelper.getResourceAsString(resolvedModuleId);

		if (res.isEmpty()) {
			throw new InvalidModule("Resource " + resolvedModuleId + " could not be loaded");
		}

		return res.orElseThrow();
	}
	
	@Override
	public String resolveModuleId(DLCore core, String moduleId)
	{
		assert moduleId != null;
		
		return moduleId;
	}

	@Override
	public boolean canParse(DLCore core, String moduleId, String data)
	{
		if (core == null) {
			return false;
		}

		if (moduleId == null) {
			return false;
		}

		if (data != null) {
			return false;
		}

		return ResourceHelper.hasResource(resolveModuleId(core, moduleId));
	}

	@Override
	public DLModule parse(DLCore core, String resolvedModuleId, String data) throws DLException
	{
		assert core != null;
		assert resolvedModuleId != null;

		try {

			//log.debug("Parsing resource " + resolvedModuleId);

			Optional<String> res = ResourceHelper.getResourceAsString(resolvedModuleId);

			if (res.isEmpty()) {
				throw new InvalidModule("Resource " + resolvedModuleId + " could not be loaded");
			}

			return DLHrfParsing.parse(core, resolvedModuleId, res.get());
		} catch (IOException ex) {
			throw new InvalidModule("Error loading module from resource - " + ex.getMessage(), ex);
		}
	}
}
