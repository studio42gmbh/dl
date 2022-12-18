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
package de.s42.dl.instances;

import de.s42.dl.*;
import de.s42.dl.types.dl.ModuleDLType;
import java.io.File;
import java.util.Optional;

/**
 *
 * @author Benjamin Schiller
 */
public class DefaultDLModule extends DefaultDLInstance implements DLModule
{

	public DefaultDLModule()
	{
		super(new ModuleDLType());
	}

	public DefaultDLModule(String name)
	{
		super(new ModuleDLType(), name);
	}

	@Override
	public String getShortName()
	{
		String shortName = getName();

		if (!shortName.contains(File.separator)) {
			return shortName;
		}

		return shortName.substring(shortName.lastIndexOf(File.separator) + 1);
	}

	@Override
	public Optional<?> resolveReference(DLCore core, String path)
	{
		assert core != null;
		assert path != null;

		Object exportedOpt = core.resolveExportedPath(path);

		if (exportedOpt != null) {
			return Optional.of(exportedOpt);
		}

		return resolvePath(path);
	}
}
