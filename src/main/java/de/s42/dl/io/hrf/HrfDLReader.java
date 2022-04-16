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

import de.s42.base.files.FilesHelper;
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

	protected final Iterator<DLInstance> instances;
	protected final DLModule module;
	protected final DLCore core;

	/**
	 * This reader is reading the DL Human Readable Format.ATTENTION: The file is loaded as String before parsing.
	 * Be aware to have enough RAM for large files.
	 *
	 * @param file file to read the content from
	 * @param core core as context
	 *
	 * @throws IOException if file can not be read
	 * @throws de.s42.dl.exceptions.DLException if the content can not be parsed
	 */
	public HrfDLReader(Path file, DLCore core) throws IOException, DLException
	{
		assert file != null;
		assert core != null;

		// @todo DL should get replaced later by a streaming approach
		module = DLHrfParsing.parse(
			core,
			file.toString(),
			FilesHelper.getFileAsString(file)
		);

		instances = module.getChildren().iterator();

		this.core = core;
	}

	@Override
	public <ObjectType> ObjectType readObject() throws IOException
	{
		return ((DLInstance) read()).toJavaObject(core);
	}

	@Override
	public <DLEntityType extends DLEntity> DLEntityType read() throws IOException
	{
		return (DLEntityType) instances.next();
	}

	@Override
	public boolean ready() throws IOException
	{
		return instances.hasNext();
	}

	@Override
	public void close() throws IOException
	{
		// nothing to do -> the file was closed after reading into a String
	}

	public DLModule getModule()
	{
		return module;
	}

	public DLCore getCore()
	{
		return core;
	}

	@Override
	public DLModule readModule() throws IOException
	{
		return getModule();
	}
}
