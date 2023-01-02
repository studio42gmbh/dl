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
import de.s42.dl.language.DLFileType;
import static de.s42.dl.util.DLHelper.recognizeFileType;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 *
 * @author Benjamin Schiller
 */
public class FileCoreResolver implements DLCoreResolver
{

	public final static String LOCAL_PATH_CONFIG_KEY = "de.s42.dl.core.resolvers.FileCoreResolver.localPath";

	private final static Logger log = LogManager.getLogger(FileCoreResolver.class.getName());

	public Path getLocalPathInCore(DLCore core)
	{
		assert core != null;
		
		return (Path) core.getConfig(LOCAL_PATH_CONFIG_KEY, null);
	}

	/**
	 * This method allows to preapre the core so that the local path lookup can use this path
	 *
	 * @param core
	 * @param path
	 */
	public void setLocalPathInCore(DLCore core, Path path)
	{
		assert core != null;

		core.setConfig(LOCAL_PATH_CONFIG_KEY, path);
	}

	public String getContent(DLCore core, String moduleId) throws InvalidModule, IOException
	{
		assert core != null;
		assert moduleId != null;

		return FilesHelper.getFileAsString(resolveModulePath(core, moduleId).orElseThrow());
	}

	public Optional<Path> resolveModulePath(DLCore core, String moduleId)
	{
		assert core != null;
		assert moduleId != null;

		Path filePath = Path.of(moduleId);

		// Look relative to current path
		Path localPath = getLocalPathInCore(core);
		if (localPath != null) {
			Path modulePath = localPath.resolve(filePath);

			if (Files.isRegularFile(modulePath)) {
				return Optional.of(modulePath);
			}
		}

		// Look relative to core base path
		Path basePath = core.getBasePath();
		if (basePath != null) {
			Path modulePath = basePath.resolve(filePath);

			if (Files.isRegularFile(modulePath)) {
				return Optional.of(modulePath);
			}
		}

		// Look relative to working dir
		if (Files.isRegularFile(filePath)) {
			return Optional.of(filePath);
		}

		return Optional.empty();
	}

	@Override
	public String resolveModuleId(DLCore core, String moduleId)
	{
		assert core != null;
		assert moduleId != null;

		return resolveModulePath(core, moduleId).orElseThrow().toAbsolutePath().normalize().toString();
	}

	@Override
	public boolean canParse(DLCore core, String moduleId, String data)
	{
		if (core == null) {
			return false;
		}

		if (moduleId == null) {
			return false;
		}

		if (data != null) {
			return false;
		}

		return resolveModulePath(core, moduleId).isPresent();
	}

	@Override
	public DLModule parse(DLCore core, String resolvedModuleId, String data) throws DLException
	{
		assert core != null;
		assert resolvedModuleId != null;
		assert data == null;

		Path modulePath = Path.of(resolvedModuleId);

		// Get the old path
		Path oldLocalPath = getLocalPathInCore(core);

		// Change config path to be relative to the new parsed file
		setLocalPathInCore(core, modulePath.getParent());

		try {

			DLFileType fileType = recognizeFileType(modulePath);

			/*log.debug(FilesHelper.createMavenNetbeansFileConsoleLink("Parsing file of type " + fileType,
				modulePath.getFileName().toString(),
				modulePath.toAbsolutePath().toString(), 1, 1, false));
			 */
			// Use HRF reader for HRF formats
			if (fileType == DLFileType.HRF || fileType == DLFileType.HRFMIN) {
				try (DLReader reader = new HrfDLReader(modulePath, core)) {
					return reader.readModule();
				}
			}

			// Use BIN reader for BIN formats						
			if (fileType == DLFileType.BIN || fileType == DLFileType.BINCOMPRESSED) {
				try (DLReader reader = new BinaryDLReader(modulePath, core)) {
					return reader.readModule();
				}
			}

			throw new InvalidModule("\"Error loading module from file '" + modulePath + "' - Unrecognized file type " + fileType);
		} catch (IOException ex) {
			throw new InvalidModule("Error loading module from file '" + modulePath + "' - " + ex.getMessage(), ex);
		} finally {
			// Set config back to old path
			setLocalPathInCore(core, oldLocalPath);
		}
	}
}
