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

import de.s42.base.uuid.UUIDHelper;
import de.s42.dl.DLCore;
import de.s42.dl.DLInstance;
import de.s42.dl.DLPragma;
import de.s42.dl.DLType;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.io.DLWriter;
import de.s42.dl.util.DLHelper;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Benjamin Schiller
 */
// @todo https://github.com/studio42gmbh/dl/issues/15 BinaryDLWriter finish first complete version (types, pragmas, requires, annotations, ...)
public class BinaryDLWriter implements DLWriter
{

	private final static Logger log = LogManager.getLogger(BinaryDLWriter.class.getName());

	protected final OutputStream out;
	protected final DLCore core;
	protected final BinaryParsingProcessorV1 processor;

	protected final Map<String, Integer> symbols = new HashMap<>();
	protected int nextSymbolId = 0;

	public BinaryDLWriter(Path file, DLCore core) throws IOException
	{
		this(file, core, true);
	}

	public BinaryDLWriter(Path file, DLCore core, boolean compress) throws IOException
	{
		{
			assert file != null;
			assert Files.isRegularFile(file);
			assert core != null;

			this.core = core;

			if (compress) {
				out = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(file)));
				((ZipOutputStream) out).putNextEntry(new ZipEntry("data.dlb"));
				// @improvement do we need to allow setting the compression level?
				// https://docs.oracle.com/javase/8/docs/api/java/util/zip/Deflater.html
				//((ZipOutputStream)out).setLevel(compressionLevel);
			} else {
				out = new BufferedOutputStream(Files.newOutputStream(file));
			}

			writeSignature(out);

			processor = new BinaryParsingProcessorV1(out);
			processor.beginModule();

		}
	}

	public BinaryDLWriter(OutputStream out, DLCore core) throws IOException
	{
		assert out != null;
		assert core != null;

		this.core = core;
		this.out = out;

		writeSignature(out);

		processor = new BinaryParsingProcessorV1(out);
		processor.beginModule();
	}

	private void writeSignature(OutputStream out) throws IOException
	{
		assert out != null;
		
		//write file header
		ByteBuffer buf = ByteBuffer.wrap(new byte[4]);
		buf.putInt(DLHelper.BIN_SIGNATURE);
		out.write(buf.array());
	}

	@Override
	public void write(DLPragma pragma) throws IOException
	{
		assert pragma != null;
	}

	@Override
	public void write(DLType type) throws IOException
	{
		assert type != null;
	}

	@Override
	public void write(Object instance) throws IOException
	{
		assert instance != null;

		try {
			write(core.convertFromJavaObject(instance));
		} catch (DLException ex) {
			throw new IOException("Error writing - " + ex.getMessage(), ex);
		}
	}

	protected int getOrMapSymbol(String symbol) throws IOException
	{
		assert symbol != null;
		
		Integer symbolId = symbols.get(symbol);

		// symbol is mapped - just return
		if (symbolId != null) {
			return symbolId;
		}

		// if symbol is not mapped -> map a new one
		nextSymbolId++;
		processor.defineSymbol(nextSymbolId, symbol);
		symbols.put(symbol, nextSymbolId);
		return nextSymbolId;
	}

	@Override
	public void write(DLInstance instance) throws IOException
	{
		assert instance != null;

		//log.debug("write", instance);
		String typeName = instance.getType().getName();
		int nameId = getOrMapSymbol(typeName);

		String instanceName = instance.getName();
		if (instanceName != null) {
			processor.beginInstance(nameId, instanceName);
		} else {
			processor.beginAnonymousInstance(nameId);
		}

		for (String attributeName : instance.getAttributeNames()) {

			int attributeNameId = getOrMapSymbol(attributeName);

			Object value = instance.get(attributeName);

			if (value != null) {

				if (value instanceof Float) {
					processor.setFloatAttribute(attributeNameId, (Float) value);
				} else if (value instanceof Double) {
					processor.setDoubleAttribute(attributeNameId, (Double) value);
				} else if (value instanceof Integer) {
					processor.setIntAttribute(attributeNameId, (Integer) value);
				} else if (value instanceof Long) {
					processor.setLongAttribute(attributeNameId, (Long) value);
				} else if (value instanceof Boolean) {
					processor.setBooleanAttribute(attributeNameId, (Boolean) value);
				} else if (value instanceof UUID) {
					processor.setBinaryAttribute(attributeNameId, UUIDHelper.toBytes((UUID) value));
				} else {
					processor.setStringAttribute(attributeNameId, "" + value);
				}
			}
		}

		processor.endInstance();
	}

	@Override
	public void close() throws IOException
	{
		try (out) {
			processor.endModule();
		}
	}

	@Override
	public void flush() throws IOException
	{
		out.flush();
	}
}
