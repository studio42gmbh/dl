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
package de.s42.dl.pragmas.integration;

import de.s42.dl.DLCore;
import de.s42.dl.exceptions.InvalidPragma;
import de.s42.dl.pragmas.AbstractDLPragma;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

/**
 *
 * @author Benjamin Schiller
 */
public class LinkPragma extends AbstractDLPragma
{

	//private final static Logger log = LogManager.getLogger(LinkPragma.class.getName());
	public final static String DEFAULT_IDENTIFIER = "link";

	public LinkPragma()
	{
		super(DEFAULT_IDENTIFIER);
	}

	public LinkPragma(String identifier)
	{
		super(identifier);
	}

	@Override
	public void doPragma(DLCore core, Object... parameters) throws InvalidPragma
	{
		assert core != null;

		parameters = validateParameters(parameters, new Class[]{Path.class});

		final Path linkPath = (Path) parameters[0];
		Path resolvedLinkPath = core.getPathResolver().resolveExists(linkPath).orElseThrow(() -> {
			return new InvalidPragma("Could not resolve path '" + linkPath + "'");
		}).toAbsolutePath().normalize();

		try {
			URLClassLoader classLoader = new URLClassLoader(new URL[]{resolvedLinkPath.toUri().toURL()}, core.getClassLoader());
			core.setClassLoader(classLoader);
		} catch (IOException ex) {
			throw new InvalidPragma("Error loading lib '" + linkPath + "' - " + ex.getMessage(), ex);
		}
	}
}
