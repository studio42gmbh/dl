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
import de.s42.dl.io.DLReader;
import de.s42.dl.parser.DLHrfParsing;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

/**
 *
 * @author Benjamin Schiller
 */
public class HrfDLReader implements DLReader
{

	protected Iterator<DLInstance> instances;
	protected DLModule module;
	protected final DLCore core;
	protected final Path file;

	/**
	 * This reader is reading the DL Human Readable Format.
	 *
	 * @param file file to read the content from
	 * @param core core as context
	 *
	 */
	public HrfDLReader(Path file, DLCore core)
	{
		assert file != null;
		assert core != null;

		this.core = core;
		this.file = file;
	}

	protected synchronized void readIntern() throws IOException, DLException
	{
		if (module != null) {
			return;
		}

		// Use default parser for parsing the file contents
		module = DLHrfParsing.parse(
			core,
			file.toAbsolutePath().normalize().toString(),
			file
		);
	}

	protected synchronized void readInternIterator() throws IOException, DLException
	{
		readIntern();

		instances = module.getChildren().iterator();
	}

	@Override
	public Object readObject() throws IOException, DLException
	{
		readIntern();

		return ((DLInstance) read()).toJavaObject();
	}

	@Override
	public <DLEntityType extends DLEntity> DLEntityType read() throws IOException, DLException
	{
		readInternIterator();

		return (DLEntityType) instances.next();
	}

	@Override
	public boolean ready() throws IOException, DLException
	{
		readInternIterator();

		return instances.hasNext();
	}

	@Override
	public void close() throws IOException
	{
		// nothing to do -> the file was closed after reading into a String
	}

	public DLCore getCore()
	{
		return core;
	}

	@Override
	public DLModule readModule() throws IOException, DLException
	{
		readIntern();

		return module;
	}
}
