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
package de.s42.dl.annotations.reflect;

import de.s42.dl.DLCore;
import de.s42.dl.DLType;
import de.s42.dl.annotations.reflect.TypeNameDLAnnotation.typeName;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.exceptions.InvalidType;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.nio.file.Path;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 *
 * @author Benjamin Schiller
 */
public class TypeNameDLAnnotationTest
{

	private final static Logger log = LogManager.getLogger(TypeNameDLAnnotationTest.class.getName());

	@typeName(pattern = ".*Action")
	public static class TypeNameAction
	{

		public Path x;
	}
	
	public static class DerivedTypeNameAction extends TypeNameAction
	{
		
	}

	@Test
	public void validJavaTypeName() throws DLException
	{
		DefaultCore core = new DefaultCore();
		DLType type = core.defineType(TypeNameAction.class);
		DLType derivedType = core.defineType(DerivedTypeNameAction.class);
		//log.debug("DLType:\n", DLHelper.describe(type));
		Assert.assertTrue(
			type.hasAnnotation(TypeNameDLAnnotation.class),
			"@typeName should be mapped for type TypeNameAction"
		);
	}
	
	@Test
	public void validTypeName() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("validTypeName", "type T @typeName(pattern : \"^T.*\") {}");
		DLType T = core.getType("T").orElseThrow();
		Assert.assertTrue(
			T.hasAnnotation(TypeNameDLAnnotation.class),
			"@typeName should be mapped for type T"
		);
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidTypeName() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidTypeName", "type T @typeName(pattern : \"^R.*\") {}");
	}

	@Test
	public void validInheritedTypeName() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("validInheritedTypeName", "abstract type B @typeName(pattern : \"^T.*\", ignoreAbstract : true) {} type T extends B {}");
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidInheritedTypeName() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidInheritedTypeName", "abstract type B @typeName(pattern : \"^R.*\", ignoreAbstract : true) {} type T extends B {}");
	}

	@Test(expectedExceptions = InvalidAnnotation.class)
	public void invalidTypeNamePattern() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidTypeNamePattern", "abstract type T @typeName(pattern : \"^[R.*\") {}");
	}	
}
