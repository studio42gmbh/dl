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
package de.s42.dl;

import de.s42.dl.exceptions.InvalidValue;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 *
 * @author Benjamin Schiller
 */
public interface DLPathResolver
{

	public Optional<Path> resolve(Path path, Predicate<Path> predicate);

	default public Optional<Path> resolveExists(Path path)
	{
		return resolve(path, (resolved) -> (Files.isRegularFile(resolved) || Files.isDirectory(resolved)));
	}

	default public Optional<Path> resolveDirectory(Path path)
	{
		return resolve(path, (resolved) -> Files.isDirectory(resolved));
	}

	default public Optional<Path> resolveFile(Path path)
	{
		return resolve(path, (resolved) -> Files.isRegularFile(resolved));
	}

	default public Optional<Path> resolveReadableFile(Path path)
	{
		return resolve(path, (resolved) -> Files.isReadable(resolved));
	}

	default public Optional<Path> resolveWritableFile(Path path)
	{
		return resolve(path, (resolved) -> Files.isWritable(resolved));
	}

	public boolean addResolveDirectory(Path directory) throws InvalidValue;

	public boolean removeResolveDirectory(Path directory);

	public List<Path> getResolveDirectories();

	public void clearResolveDirectories();
}
