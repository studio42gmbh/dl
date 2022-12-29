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

/**
 *
 * @author Benjamin Schiller
 */
public class StringCoreResolver implements DLCoreResolver
{

	protected final DLCore core;

	public StringCoreResolver(DLCore core)
	{
		assert core != null;

		this.core = core;
	}

	@Override
	public boolean canParse(String moduleId)
	{
		return false;
	}

	@Override
	public boolean canParse(String moduleId, String data)
	{
		return (moduleId != null) && (data != null);
	}

	@Override
	public DLModule parse(String moduleId, String data) throws DLException
	{
		assert moduleId != null;
		assert data != null;

		return DLHrfParsing.parse(core, moduleId, data);
	}

	@Override
	public DLModule parse(String moduleId) throws InvalidModule
	{
		throw new InvalidModule("Error can just load module with string");
	}

	public DLCore getCore()
	{
		return core;
	}
}
