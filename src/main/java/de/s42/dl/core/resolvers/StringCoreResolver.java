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
import de.s42.dl.DLModule;
import de.s42.dl.core.DLCoreResolver;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidModule;
import de.s42.dl.parser.DLHrfParsing;
import java.io.IOException;

/**
 *
 * @author Benjamin Schiller
 */
public class StringCoreResolver implements DLCoreResolver
{

	@Override
	public String getContent(DLCore core, String moduleId, String data) throws InvalidModule, IOException
	{
		assert data != null;

		return data;
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
		return (core != null) && (moduleId != null) && (data != null);
	}

	@Override
	public DLModule parse(DLCore core, String moduleId, String data) throws DLException
	{
		assert core != null;
		assert moduleId != null;
		assert data != null;

		return DLHrfParsing.parse(core, moduleId, data);
	}
}
