// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 * 
 * Copyright 2023 Studio 42 GmbH ( https://www.s42m.de ).
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

import de.s42.dl.DLPathResolver;
import de.s42.dl.exceptions.InvalidValue;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 *
 * @author Benjamin Schiller
 */
public class DefaultDLPathResolver implements DLPathResolver
{

	private final static Logger log = LogManager.getLogger(DefaultDLPathResolver.class.getName());

	protected final List<Path> directories = new ArrayList<>();

	@Override
	public Optional<Path> resolve(Path path, Predicate<Path> predicate)
	{
		assert path != null;
		assert predicate != null;

		try {
			// Return first resolved in add order of directories
			for (Path directory : directories) {
				Path resolved = directory.resolve(path);
				if (predicate.test(resolved)) {
					return Optional.of(resolved);
				}
			}

			// Test relative to working directory -> return unchanged
			if (predicate.test(path)) {
				return Optional.of(path);
			}
		} catch (InvalidPathException ex) {
			log.error("Error resolving path '" + path + "'" + ex.getMessage());
		}

		return Optional.empty();
	}

	@Override
	public boolean addResolveDirectory(Path directory) throws InvalidValue
	{
		assert directory != null;

		if (directories.contains(directory)) {
			return false;
		}

		if (!Files.isDirectory(directory)) {
			throw new InvalidValue("Path '" + directory + "' is not a valid directory");
		}

		return directories.add(directory);
	}

	@Override
	public boolean removeResolveDirectory(Path directory)
	{
		assert directory != null;

		return directories.remove(directory);
	}

	@Override
	public List<Path> getResolveDirectories()
	{
		return Collections.unmodifiableList(directories);
	}

	@Override
	public void clearResolveDirectories()
	{
		directories.clear();
	}
}
