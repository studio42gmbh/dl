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
package de.s42.dl.io.hrf;

import de.s42.dl.*;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.io.DLWriter;
import de.s42.dl.util.DLHelper;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author Benjamin Schiller
 */
// @todo https://github.com/studio42gmbh/dl/issues/16 HrfDLWriter finish first complete version (types, pragmas, requires, annotations, ...)
public class HrfDLWriter implements DLWriter
{

	//private final static Logger log = LogManager.getLogger(HrfDLWriter.class.getName());
	//protected final Writer file;
	protected final OutputStream out;
	protected final DLCore core;
	protected final boolean prettyPrint;

	public final static Charset UTF8 = Charset.forName("UTF-8");

	public HrfDLWriter(Path file, DLCore core) throws IOException
	{
		this(file, core, false);
	}

	public HrfDLWriter(Path file, DLCore core, boolean prettyPrint) throws IOException
	{
		assert file != null;
		assert core != null;

		this.core = core;
		this.prettyPrint = prettyPrint;

		out = new BufferedOutputStream(Files.newOutputStream(file));
	}

	@Override
	public void write(DLPragma pragma) throws IOException
	{
		assert pragma != null;

		// @todo
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public void write(DLType type) throws IOException
	{
		assert type != null;

		// write type
		String instStr = DLHelper.toString(type, prettyPrint);

		if (prettyPrint) {
			out.write("\n".getBytes(UTF8));
			out.write(instStr.getBytes(UTF8));
			out.write("\n".getBytes(UTF8));
		} else {
			out.write(instStr.getBytes(UTF8));
		}

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

	@Override
	public void write(DLInstance instance) throws IOException
	{
		assert instance != null;

		String instStr = DLHelper.toString(instance, prettyPrint);

		if (prettyPrint) {
			out.write("\n".getBytes(UTF8));
			out.write(instStr.getBytes(UTF8));
			out.write("\n".getBytes(UTF8));
		} else {
			out.write(instStr.getBytes(UTF8));
		}
	}

	@Override
	public void close() throws IOException
	{
		out.close();
	}

	@Override
	public void flush() throws IOException
	{
		out.flush();
	}
}
