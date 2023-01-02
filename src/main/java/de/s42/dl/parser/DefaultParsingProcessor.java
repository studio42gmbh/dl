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

import de.s42.dl.DLCore;
import de.s42.dl.DLInstance;
import de.s42.dl.DLModule;
import de.s42.dl.DLType;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Benjamin Schiller
 */
public class DefaultParsingProcessor implements DLParsingProcessor
{

	private final static Logger log = LogManager.getLogger(DefaultParsingProcessor.class.getName());

	protected final DLCore core;

	protected DLModule module;

	protected DLInstance currentInstance;
	protected DLInstance lastInstance;

	protected final Map<String, Integer> idBySymbol = new HashMap<>();
	protected final Map<Integer, String> symbolById = new HashMap<>();

	public DefaultParsingProcessor(DLCore core)
	{
		assert core != null;

		this.core = core;
	}

	@Override
	public void beginModule() throws IOException
	{
		try {
			module = core.createModule();
			idBySymbol.clear();
			symbolById.clear();
		} catch (DLException ex) {
			throw new IOException("Could not begin module - " + ex.getMessage(), ex);
		}
	}

	@Override
	public void endModule() throws IOException
	{
		// do nothing
	}

	@Override
	public void defineSymbol(int id, String symbol) throws IOException
	{
		assert id > 0;
		assert symbol != null;

		if (symbolById.put(id, symbol) != null) {
			throw new IOException("Symbol " + id + " is already defined");
		}

		if (idBySymbol.put(symbol, id) != null) {
			throw new IOException("Symbol " + symbol + " is already defined");
		}
	}

	protected String getSymbol(int id) throws IOException
	{
		assert id > 0;

		String symbol = symbolById.get(id);

		if (symbol == null) {
			throw new IOException("Symbol with id " + id + " is not defined");
		}

		return symbol;
	}

	@Override
	public void beginAnonymousInstance(int typeId) throws IOException
	{
		assert typeId > 0;

		endInstance();

		String typeName = getSymbol(typeId);

		if (!core.hasType(typeName)) {
			throw new IOException("Type " + typeName + " is not contained in core");
		}

		DLType type = core.getType(typeName).orElseThrow(() -> {
			return new IOException("Error getting type '" + typeName + "'");
		});

		currentInstance = core.createInstance(type);
	}

	@Override
	public void beginInstance(int typeId, String name) throws IOException
	{
		assert typeId > 0;
		assert name != null;

		endInstance();

		String typeName = symbolById.get(typeId);

		if (!core.hasType(typeName)) {
			throw new IOException("Type " + typeName + " is not contained in core");
		}

		DLType type = core.getType(typeName).orElseThrow(() -> {
			return new IOException("Error getting type '" + typeName + "'");
		});

		currentInstance = core.createInstance(type, name);
	}

	@Override
	public void endInstance() throws IOException
	{
		if (currentInstance != null) {
			try {
				module.addChild(currentInstance);
			} catch (InvalidInstance ex) {
				throw new IOException(ex);
			}
			lastInstance = currentInstance;
			currentInstance = null;
		}
	}

	@Override
	public void setStringAttribute(int nameId, String value) throws IOException
	{
		assert nameId > 0;
		assert value != null;

		if (currentInstance == null) {
			throw new IOException("No current instance active");
		}

		String name = getSymbol(nameId);

		currentInstance.set(name, value);
	}

	@Override
	public void setBooleanAttribute(int nameId, boolean value) throws IOException
	{
		assert nameId > 0;

		if (currentInstance == null) {
			throw new IOException("No current instance active");
		}

		String name = getSymbol(nameId);

		currentInstance.set(name, value);
	}

	@Override
	public void setDoubleAttribute(int nameId, double value) throws IOException
	{
		assert nameId > 0;

		if (currentInstance == null) {
			throw new IOException("No current instance active");
		}

		String name = getSymbol(nameId);

		currentInstance.set(name, value);
	}

	@Override
	public void setFloatAttribute(int nameId, float value) throws IOException
	{
		assert nameId > 0;

		if (currentInstance == null) {
			throw new IOException("No current instance active");
		}

		String name = getSymbol(nameId);

		currentInstance.set(name, value);
	}

	@Override
	public void setIntAttribute(int nameId, int value) throws IOException
	{
		assert nameId > 0;

		if (currentInstance == null) {
			throw new IOException("No current instance active");
		}

		String name = getSymbol(nameId);

		currentInstance.set(name, value);
	}

	@Override
	public void setLongAttribute(int nameId, long value) throws IOException
	{
		assert nameId > 0;

		if (currentInstance == null) {
			throw new IOException("No current instance active");
		}

		String name = getSymbol(nameId);

		currentInstance.set(name, value);
	}

	@Override
	public void setBinaryAttribute(int nameId, byte[] value) throws IOException
	{
		assert nameId > 0;
		assert value != null;

		if (currentInstance == null) {
			throw new IOException("No current instance active");
		}

		String name = getSymbol(nameId);

		currentInstance.set(name, value);
	}

	@Override
	public void setBinaryAttribute(int nameId, byte[] value, int index, int length) throws IOException
	{
		assert nameId > 0;
		assert value != null;
		assert index >= 0;
		assert length > 0;

		if (currentInstance == null) {
			throw new IOException("No current instance active");
		}

		String name = getSymbol(nameId);

		value = Arrays.copyOfRange(value, index, index + length);

		currentInstance.set(name, value);
	}

	public DLModule getModule()
	{
		return module;
	}

	public DLInstance getLastInstance()
	{
		return lastInstance;
	}

	public void setLastInstance(DLInstance lastInstance)
	{
		this.lastInstance = lastInstance;
	}
}
