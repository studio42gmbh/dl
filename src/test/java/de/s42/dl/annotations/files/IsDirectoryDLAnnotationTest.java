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
package de.s42.dl.annotations.files;

import de.s42.dl.DLAttribute;
import de.s42.dl.DLCore;
import de.s42.dl.DLType;
import de.s42.dl.annotations.files.IsDirectoryDLAnnotation.isDirectory;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.nio.file.Path;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 *
 * @author Benjamin Schiller
 */
public class IsDirectoryDLAnnotationTest
{

	private final static Logger log = LogManager.getLogger(IsDirectoryDLAnnotationTest.class.getName());

	public static class IsDirectoryClass
	{

		@isDirectory
		public Path x;
	}

	@Test
	public void validIsDirectoryJavaType() throws DLException
	{
		DefaultCore core = new DefaultCore();
		DLType type = core.defineType(IsDirectoryClass.class);
		//log.debug("DLType:\n", DLHelper.describe(type));
		DLAttribute x = type.getAttribute("x").orElseThrow();
		Assert.assertTrue(
			x.hasAnnotation(IsDirectoryDLAnnotation.class),
			"@isDirectory should be mapped for attribute x in type from IsFileClass"
		);
	}

	@Test
	public void validIsDirectoryType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("validIsDirectoryType", "type T { Path x @isDirectory; }");
		DLType T = core.getType("T").orElseThrow();
		DLAttribute x = T.getAttribute("x").orElseThrow();
		Assert.assertTrue(
			x.hasAnnotation(IsDirectoryDLAnnotation.class),
			"@isDirectory should be mapped for attribute x in type T"
		);
	}

	@Test
	public void validIsDirectoryAttributeInInstance() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("validIsDirectoryAttributeInInstance", "type T { Path x @isDirectory; } T t { x : \"./\"; }");
	}

	@Test(expectedExceptions = InvalidInstance.class)
	public void invalidIsDirectoryAttributeInInstance() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidIsDirectoryAttributeInInstance", "type T { Path x @isDirectory; } T t { x : \"wrong/directory/42%$!\"; }");
	}

	@Test(expectedExceptions = InvalidInstance.class)
	public void invalidIsDirectoryNullAttributeInInstance() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidIsDirectoryNullAttributeInInstance", "type T { Path x @isDirectory; } T t {}");
	}

	@Test(expectedExceptions = InvalidAnnotation.class)
	public void invalidIsDirectoryWithFlatParameter() throws DLException
	{
		DLCore core = new DefaultCore();

		core.parse("invalidIsDirectoryWithFlatParameter", "type T { int x @isFile(wrong); }");
	}

	@Test(expectedExceptions = InvalidAnnotation.class)
	public void invalidIsDirectoryWithNamedParameter() throws DLException
	{
		DLCore core = new DefaultCore();

		core.parse("invalidIsDirectoryWithNamedParameter", "type T { int x @isFile(wrong : true); }");
	}
}
