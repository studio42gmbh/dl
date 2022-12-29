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
package de.s42.dl.core.resolvers;

import de.s42.base.files.FilesHelper;
import de.s42.dl.DLCore;
import de.s42.dl.DLModule;
import de.s42.dl.core.DLCoreResolver;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidModule;
import de.s42.dl.io.DLReader;
import de.s42.dl.io.binary.BinaryDLReader;
import de.s42.dl.io.hrf.HrfDLReader;
import de.s42.dl.util.DLHelper.DLFileType;
import static de.s42.dl.util.DLHelper.recognizeFileType;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author Benjamin Schiller
 */
public class FileCoreResolver implements DLCoreResolver
{

	private final static Logger log = LogManager.getLogger(FileCoreResolver.class.getName());

	protected final DLCore core;

	public FileCoreResolver(DLCore core)
	{
		assert core != null;

		this.core = core;
	}

	@Override
	public boolean canParse(String moduleId)
	{
		if (moduleId == null) {
			return false;
		}
		
		return Files.isRegularFile(Path.of(moduleId));
	}

	@Override
	public boolean canParse(String moduleId, String data)
	{
		return false;
	}

	@Override
	public DLModule parse(String moduleId) throws DLException
	{
		assert moduleId != null;
		
		try {

			Path filePath = Path.of(moduleId);
			Path basePath = core.getBasePath();
			Path modulePath;

			if (basePath != null) {
				modulePath = basePath.resolve(filePath).normalize().toAbsolutePath();
			} else {
				modulePath = filePath.normalize().toAbsolutePath();
			}

			DLFileType fileType = recognizeFileType(modulePath);

			log.debug(FilesHelper.createMavenNetbeansFileConsoleLink("Parsing file of type " + fileType,
				modulePath.getFileName().toString(),
				modulePath.toAbsolutePath().toString(), 1, 1, false));

			DLModule module = null;

			if (fileType == DLFileType.HRF || fileType == DLFileType.HRFMIN) {
				try (DLReader reader = new HrfDLReader(modulePath, core)) {

					module = reader.readModule();
				}
			} else if (fileType == DLFileType.BIN || fileType == DLFileType.BINCOMPRESSED) {
				try (DLReader reader = new BinaryDLReader(modulePath, core)) {

					module = reader.readModule();
				}
			} else {
				throw new IOException("Unrecognized file type " + fileType);
			}

			return module;
		} catch (IOException ex) {
			throw new InvalidModule("Error loading module from file - " + ex.getMessage(), ex);
		}
	}

	@Override
	public DLModule parse(String moduleId, String data) throws InvalidModule
	{
		throw new InvalidModule("Error can just load module from file");
	}

	public DLCore getCore()
	{
		return core;
	}
}
