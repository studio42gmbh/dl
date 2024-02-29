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

import de.s42.base.files.FilesHelper;
import de.s42.base.zip.ZipHelper;
import de.s42.dl.DLCore;
import de.s42.dl.DLEntity;
import de.s42.dl.DLInstance;
import de.s42.dl.DLModule;
import de.s42.dl.io.DLReader;
import de.s42.dl.parser.DefaultParsingProcessor;
import de.s42.dl.parser.Opcode;
import static de.s42.dl.parser.Opcode.*;
import de.s42.dl.util.DLHelper;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.file.Path;
import java.util.Arrays;

/**
 *
 * @author Benjamin Schiller
 */
public class BinaryDLReader implements DLReader
{

	/**
	 * This is the length of files in bytes where smaller files will be loaded completely before parsing
	 */
	public final static long LOAD_TOTAL_FILE_MAX_SIZE = 1000000L;

	protected final ByteBuffer in;
	protected final DefaultParsingProcessor processor;
	protected final DLCore core;

	protected final byte[] buf65536 = new byte[65536];

	public BinaryDLReader(Path file, DLCore core) throws IOException
	{
		assert file != null;
		assert core != null;

		in = getDLBFileAsByteBuffer(file);

		validateSignature(in);

		processor = new DefaultParsingProcessor(core);
		this.core = core;
	}

	public BinaryDLReader(ByteBuffer in, DLCore core) throws IOException
	{
		assert in != null;
		assert core != null;

		this.in = in;

		validateSignature(in);

		processor = new DefaultParsingProcessor(core);
		this.core = core;
	}

	private void validateSignature(ByteBuffer in) throws IOException
	{
		int signature = in.getInt();
		if (signature != DLHelper.BIN_SIGNATURE) {
			throw new IOException("Invalid file signature " + signature + " has to be " + DLHelper.BIN_SIGNATURE);
		}
	}

	public static ByteBuffer getDLBFileAsByteBuffer(Path file) throws IOException
	{
		assert file != null;

		ByteBuffer result;

		if (ZipHelper.isArchive(file)) {
			result = ByteBuffer.wrap(FilesHelper.getZippedSingleFileAsByteArray(file));
		} else {
			// make sure to either load the file total for smaller files or stream it memory mapped
			if (file.toFile().length() > LOAD_TOTAL_FILE_MAX_SIZE) {
				result = FilesHelper.getFileAsMappedByteBuffer(file);
			} else {
				result = FilesHelper.getFileAsByteBuffer(file);
			}
		}

		return result;
	}

	protected int readNextInt() throws IOException
	{
		int bc = in.remaining();

		// end of stream reached
		if (bc == 0) {
			return -1;
		}

		if (bc < 4) {
			throw new IOException("Source corrupted - could not read enough bytes for next int");
		}

		return in.getInt();
	}

	protected float readNextFloat() throws IOException
	{
		int bc = in.remaining();

		// end of stream reached
		if (bc == 0) {
			return -1;
		}

		if (bc < 4) {
			throw new IOException("Source corrupted - could not read enough bytes for next int");
		}

		return in.getFloat();
	}

	protected long readNextLong() throws IOException
	{
		int bc = in.remaining();

		// end of stream reached
		if (bc == 0) {
			return -1;
		}

		if (bc < 8) {
			throw new IOException("Source corrupted - could not read enough bytes for next int");
		}

		return in.getLong();
	}

	protected double readNextDouble() throws IOException
	{
		int bc = in.remaining();

		// end of stream reached
		if (bc == 0) {
			return -1;
		}

		if (bc < 8) {
			throw new IOException("Source corrupted - could not read enough bytes for next int");
		}

		return in.getDouble();
	}

	protected boolean readNextBoolean() throws IOException
	{
		int bc = in.remaining();

		// end of stream reached
		if (bc == 0) {
			return false;
		}

		if (bc < 1) {
			throw new IOException("Source corrupted - could not read enough bytes for next int");
		}

		return in.get() == (byte) 1;
	}

	protected String readNextString() throws IOException
	{
		int length = readNextInt();

		if (length >= buf65536.length) {
			throw new IOException("String is to long max is " + buf65536.length);
		}

		int bc = in.remaining();

		if (bc < length) {
			throw new IOException("Source corrupted - could not read enough bytes for next string " + bc + " instead of " + length);
		}

		in.get(buf65536, 0, length);
		return new String(buf65536, 0, length);
	}

	protected byte[] readNextBinary() throws IOException
	{
		int length = readNextInt();

		if (length >= buf65536.length) {
			throw new IOException("Binary is to long max is " + buf65536.length);
		}

		int bc = in.remaining();

		if (bc < length) {
			throw new IOException("Source corrupted - could not read enough bytes for next binary " + bc + " instead of " + length);
		}

		in.get(buf65536, 0, length);
		return Arrays.copyOf(buf65536, length);
	}

	public boolean readNextOpcode() throws IOException
	{
		int code = readNextInt();

		// end of stream reached
		if (code == -1) {
			return false;
		}

		Opcode opcode = Opcode.valueOf(code);

		if (opcode == BeginModule) {
			int version = readNextInt();
			processor.beginModule();
		} else if (opcode == EndModule) {
			processor.endModule();
		} else if (opcode == DefineSymbol) {
			int id = readNextInt();
			String symbol = readNextString();
			processor.defineSymbol(id, symbol);
		} else if (opcode == BeginInstance) {
			int type = readNextInt();
			String name = readNextString();
			processor.beginInstance(type, name);
		} else if (opcode == BeginAnonymousInstance) {
			int type = readNextInt();
			processor.beginAnonymousInstance(type);
		} else if (opcode == EndInstance) {
			processor.endInstance();
		} else if (opcode == SetBinaryAttribute) {
			int name = readNextInt();
			byte[] value = readNextBinary();
			processor.setBinaryAttribute(name, value);
		} else if (opcode == SetStringAttribute) {
			int name = readNextInt();
			String value = readNextString();
			processor.setStringAttribute(name, value);
		} else if (opcode == SetFloatAttribute) {
			int name = readNextInt();
			float value = readNextFloat();
			processor.setFloatAttribute(name, value);
		} else if (opcode == SetDoubleAttribute) {
			int name = readNextInt();
			double value = readNextDouble();
			processor.setDoubleAttribute(name, value);
		} else if (opcode == SetIntAttribute) {
			int name = readNextInt();
			int value = readNextInt();
			processor.setIntAttribute(name, value);
		} else if (opcode == SetLongAttribute) {
			int name = readNextInt();
			long value = readNextLong();
			processor.setLongAttribute(name, value);
		} else if (opcode == SetBooleanAttribute) {
			int name = readNextInt();
			boolean value = readNextBoolean();
			processor.setBooleanAttribute(name, value);
		} else {
			throw new IOException("Source corrupted - unknown opcode " + code);
		}

		return true;
	}

	@Override
	public <DLEntityType extends DLEntity> DLEntityType read() throws IOException
	{
		DLInstance instance = processor.getLastInstance();
		if (instance != null) {
			processor.setLastInstance(null);
			return (DLEntityType) instance;
		}

		while (readNextOpcode()
			&& (instance == null)) {
			instance = processor.getLastInstance();
		}

		if (instance != null) {
			processor.setLastInstance(null);
			return (DLEntityType) instance;
		}

		return null;
	}

	@Override
	public DLModule readModule() throws IOException
	{
		while (readNextOpcode()) {
		}

		return processor.getModule();
	}

	@Override
	public <ObjectType> ObjectType readObject() throws IOException
	{
		DLInstance instance = read();

		if (instance != null) {
			return (ObjectType) instance.toJavaObject();
		}

		return null;
	}

	@Override
	public boolean ready() throws IOException
	{
		DLInstance instance = processor.getLastInstance();
		if (instance != null) {
			return true;
		}

		while (readNextOpcode()
			&& (instance == null)) {
			instance = processor.getLastInstance();
		}

		return instance != null;
	}

	@Override
	public void close() throws IOException
	{
		// good to know for handling mapped byte buffers - https://stackoverflow.com/questions/25238110/how-to-properly-close-mappedbytebuffer
		if (in instanceof MappedByteBuffer) {
			try {
				Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
				Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
				unsafeField.setAccessible(true);
				Object unsafe = unsafeField.get(null);
				Method invokeCleaner = unsafeClass.getMethod("invokeCleaner", ByteBuffer.class);
				invokeCleaner.invoke(unsafe, in);
			} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
				throw new IOException("Error closing MappedByteBuffer - " + ex.getMessage(), ex);
			}
		}
	}
}
