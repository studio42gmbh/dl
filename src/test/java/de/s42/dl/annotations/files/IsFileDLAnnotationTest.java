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
import de.s42.dl.annotations.files.IsFileDLAnnotation.isFile;
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
public class IsFileDLAnnotationTest
{

	private final static Logger log = LogManager.getLogger(IsFileDLAnnotationTest.class.getName());

	public static class IsFileClass
	{

		@isFile
		public Path x;
	}

	@Test
	public void validIsFileJavaType() throws DLException
	{
		DefaultCore core = new DefaultCore();
		DLType type = core.defineType(IsFileClass.class);
		//log.debug("DLType:\n", DLHelper.describe(type));
		DLAttribute x = type.getAttribute("x").orElseThrow();
		Assert.assertTrue(
			x.hasAnnotation(IsFileDLAnnotation.class),
			"@isFile should be mapped for attribute x in type from IsFileClass"
		);
	}

	@Test
	public void validIsFileType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("validIsFileType", "type T { Path x @isFile; }");
		DLType T = core.getType("T").orElseThrow();
		DLAttribute x = T.getAttribute("x").orElseThrow();
		Assert.assertTrue(
			x.hasAnnotation(IsFileDLAnnotation.class),
			"@isFile should be mapped for attribute x in type T"
		);
	}

	@Test
	public void validIsFileAttributeInInstance() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("validIsFileAttributeInInstance", "type T { Path x @isFile; } T t { x : \"pom.xml\"; }");
	}

	@Test(expectedExceptions = InvalidInstance.class)
	public void invalidIsFileAttributeInInstance() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidIsFileAttributeInInstance", "type T { Path x @isFile; } T t { x : \"wrong/directory/42%$!\"; }");
	}

	@Test
	public void validIsFileAttributeNullInInstance() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidIsFileAttributeNullInInstance", "type T { Path x @isFile; } T t {}");
	}

	@Test(expectedExceptions = InvalidAnnotation.class)
	public void invalidIsFileWithFlatParameter() throws DLException
	{
		DLCore core = new DefaultCore();

		core.parse("invalidIsFileWithFlatParameter", "type T { int x @isFile(wrong); }");
	}

	@Test(expectedExceptions = InvalidAnnotation.class)
	public void invalidIsFileWithNamedParameter() throws DLException
	{
		DLCore core = new DefaultCore();

		core.parse("invalidIsFileWithNamedParameter", "type T { int x @isFile(wrong : true); }");
	}
}
