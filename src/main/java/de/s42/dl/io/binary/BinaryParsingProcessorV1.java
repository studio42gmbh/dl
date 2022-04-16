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
package de.s42.dl.io.binary;

import de.s42.dl.parser.DLParsingProcessor;
import de.s42.dl.parser.Opcode;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Benjamin Schiller
 */
public class BinaryParsingProcessorV1 implements DLParsingProcessor
{

	public final static int VERSION = 1;

	protected final OutputStream out;

	protected Map<Integer, String> symbolsById = new HashMap<>();
	protected Map<String, Integer> symbolsBySymbol = new HashMap<>();

	private final ByteBuffer buf4 = ByteBuffer.wrap(new byte[4]);
	private final ByteBuffer buf8 = ByteBuffer.wrap(new byte[8]);
	private final ByteBuffer buf9 = ByteBuffer.wrap(new byte[9]);
	private final ByteBuffer buf12 = ByteBuffer.wrap(new byte[12]);
	private final ByteBuffer buf16 = ByteBuffer.wrap(new byte[16]);

	public BinaryParsingProcessorV1(OutputStream out)
	{
		assert out != null;

		this.out = out;
	}

	@Override
	public void beginModule() throws IOException
	{
		ByteBuffer buf = buf8;
		buf.rewind();
		buf.putInt(Opcode.BeginModule.code);
		buf.putInt(VERSION);
		out.write(buf.array());
	}

	@Override
	public void endModule() throws IOException
	{
		ByteBuffer buf = buf4;
		buf.rewind();
		buf.putInt(Opcode.EndModule.code);
		out.write(buf.array());
	}

	@Override
	public void defineSymbol(int id, String symbol) throws IOException
	{
		assert id > 0;
		assert symbol != null;

		if (symbolsById.put(id, symbol) != null) {
			throw new RuntimeException("Symbol " + id + " is already defined");
		}

		if (symbolsBySymbol.put(symbol, id) != null) {
			throw new RuntimeException("Symbol " + symbol + " is already defined");
		}

		byte[] symbolData = symbol.getBytes();
		ByteBuffer buf = buf12;
		buf.rewind();
		buf.putInt(Opcode.DefineSymbol.code);
		buf.putInt(id);
		buf.putInt(symbolData.length);
		out.write(buf.array());
		out.write(symbolData);
	}

	@Override
	public void beginAnonymousInstance(int type) throws IOException
	{
		assert type > 0;

		ByteBuffer buf = buf8;
		buf.rewind();
		buf.putInt(Opcode.BeginAnonymousInstance.code);
		buf.putInt(type);
		out.write(buf.array());
	}

	@Override
	public void beginInstance(int type, String name) throws IOException
	{
		assert type > 0;
		assert name != null;

		byte[] symbolData = name.getBytes();
		ByteBuffer buf = buf12;
		buf.rewind();
		buf.putInt(Opcode.BeginInstance.code);
		buf.putInt(type);
		buf.putInt(symbolData.length);
		out.write(buf.array());
		out.write(symbolData);
	}

	@Override
	public void endInstance() throws IOException
	{
		ByteBuffer buf = buf4;
		buf.rewind();
		buf.putInt(Opcode.EndInstance.code);
		out.write(buf.array());
	}

	@Override
	public void setStringAttribute(int name, String value) throws IOException
	{
		assert name > 0;
		assert value != null : "Value for attribute " + name + " is null";

		byte[] valueData = value.getBytes();
		ByteBuffer buf = buf12;
		buf.rewind();
		buf.putInt(Opcode.SetStringAttribute.code);
		buf.putInt(name);
		buf.putInt(valueData.length);
		out.write(buf.array());
		out.write(valueData);
	}

	@Override
	public void setBooleanAttribute(int name, boolean value) throws IOException
	{
		assert name > 0;

		ByteBuffer buf = buf9;
		buf.rewind();
		buf.putInt(Opcode.SetBooleanAttribute.code);
		buf.putInt(name);
		buf.put(value ? (byte) 1 : (byte) 0);
		out.write(buf.array());
	}

	@Override
	public void setDoubleAttribute(int name, double value) throws IOException
	{
		assert name > 0;

		ByteBuffer buf = buf16;
		buf.rewind();
		buf.putInt(Opcode.SetDoubleAttribute.code);
		buf.putInt(name);
		buf.putDouble(value);
		out.write(buf.array());
	}

	@Override
	public void setFloatAttribute(int name, float value) throws IOException
	{
		assert name > 0;

		ByteBuffer buf = buf12;
		buf.rewind();
		buf.putInt(Opcode.SetFloatAttribute.code);
		buf.putInt(name);
		buf.putFloat(value);
		out.write(buf.array());
	}

	@Override
	public void setIntAttribute(int name, int value) throws IOException
	{
		assert name > 0;

		ByteBuffer buf = buf12;
		buf.rewind();
		buf.putInt(Opcode.SetIntAttribute.code);
		buf.putInt(name);
		buf.putInt(value);
		out.write(buf.array());
	}

	@Override
	public void setLongAttribute(int name, long value) throws IOException
	{
		assert name > 0;

		ByteBuffer buf = buf16;
		buf.rewind();
		buf.putInt(Opcode.SetLongAttribute.code);
		buf.putInt(name);
		buf.putLong(value);
		out.write(buf.array());
	}

	@Override
	public void setBinaryAttribute(int name, byte[] value) throws IOException
	{
		assert name > 0;
		assert value != null;

		setBinaryAttribute(name, value, 0, value.length);
	}

	@Override
	public void setBinaryAttribute(int name, byte[] value, int index, int length) throws IOException
	{
		assert name > 0;
		assert index >= 0;
		assert length > 0;
		assert index + length <= value.length;
		assert value != null;

		ByteBuffer buf = buf12;
		buf.rewind();
		buf.putInt(Opcode.SetBinaryAttribute.code);
		buf.putInt(name);
		buf.putInt(length);
		out.write(buf.array());
		out.write(value, index, length);
	}

	public int getSymbolId(String symbol)
	{
		assert symbol != null;

		Integer id = symbolsBySymbol.get(symbol);

		if (id == null) {
			return -1;
		}

		return id;
	}

	public Map<Integer, String> getSymbolsById()
	{
		return Collections.unmodifiableMap(symbolsById);
	}

	public Map<String, Integer> getSymbolsBySymbol()
	{
		return Collections.unmodifiableMap(symbolsBySymbol);
	}
}
