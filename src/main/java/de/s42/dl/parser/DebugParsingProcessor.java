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
package de.s42.dl.parser;

import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author Benjamin Schiller
 */
public class DebugParsingProcessor implements DLParsingProcessor
{

	private final static Logger log = LogManager.getLogger(DebugParsingProcessor.class.getName());

	public DebugParsingProcessor()
	{
	}

	@Override
	public void beginModule() throws IOException
	{
		log.debug("BeginModule");
	}

	@Override
	public void endModule() throws IOException
	{
		log.debug("EndModule");
	}

	@Override
	public void defineSymbol(int id, String symbol) throws IOException
	{
		log.debug("DefineSymbol", id, symbol);
	}

	@Override
	public void beginAnonymousInstance(int type) throws IOException
	{
		log.debug("BeginAnonymousInstance", type);
	}

	@Override
	public void beginInstance(int type, String name) throws IOException
	{
		log.debug("BeginInstance", type, name);
	}

	@Override
	public void endInstance() throws IOException
	{
		log.debug("EndInstance");
	}

	@Override
	public void setStringAttribute(int name, String value) throws IOException
	{
		log.debug("SetStringAttribute", name, value);
	}

	@Override
	public void setBooleanAttribute(int name, boolean value) throws IOException
	{
		log.debug("SetBooleanAttribute", name, value);
	}

	@Override
	public void setDoubleAttribute(int name, double value) throws IOException
	{
		log.debug("SetDoubleAttribute", name, value);
	}

	@Override
	public void setFloatAttribute(int name, float value) throws IOException
	{
		log.debug("SetFloatAttribute", name, value);
	}

	@Override
	public void setIntAttribute(int name, int value) throws IOException
	{
		log.debug("setIntAttribute", name, value);
	}

	@Override
	public void setLongAttribute(int name, long value) throws IOException
	{
		log.debug("SetLongAttribute", name, value);
	}

	@Override
	public void setBinaryAttribute(int name, byte[] value) throws IOException
	{
		setBinaryAttribute(name, value, 0, value.length);
	}

	@Override
	public void setBinaryAttribute(int name, byte[] value, int index, int length) throws IOException
	{
		log.debug("setBinaryAttribute", name, Arrays.toString(value), index, length);
	}
}
