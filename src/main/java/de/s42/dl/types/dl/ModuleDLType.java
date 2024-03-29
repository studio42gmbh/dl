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
package de.s42.dl.types.dl;

import de.s42.dl.DLCore;
import de.s42.dl.DLType;
import de.s42.dl.types.DefaultDLType;
import java.util.HashMap;

/**
 *
 * @author Benjamin Schiller
 */
public class ModuleDLType extends DefaultDLType
{

	public final static String DEFAULT_SYMBOL = "Module";

	public ModuleDLType()
	{
		this(DEFAULT_SYMBOL);
	}

	public ModuleDLType(DLType parent)
	{
		this(DEFAULT_SYMBOL);

		addParent(parent);
	}

	public ModuleDLType(String name)
	{
		super(name);
	}

	public ModuleDLType(DLCore core)
	{
		this(core, DEFAULT_SYMBOL);
	}

	public ModuleDLType(DLCore core, DLType parent)
	{
		this(core, DEFAULT_SYMBOL);

		addParent(parent);
	}

	public ModuleDLType(DLCore core, String name)
	{
		super(core, name);
	}
	
	@Override
	public Class getJavaDataType()
	{
		return HashMap.class;
	}

	@Override
	public boolean mayContainType(DLType type)
	{
		return true;
	}

	@Override
	public boolean isDynamic()
	{
		return true;
	}
	
	@Override
	public void setDynamic(boolean dynamic)
	{
		if (!dynamic) {
			throw new UnsupportedOperationException("May not set not dynamic");
		}
	}
}
