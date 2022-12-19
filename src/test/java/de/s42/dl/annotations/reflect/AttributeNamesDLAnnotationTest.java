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
import de.s42.dl.annotations.reflect.AttributeNamesDLAnnotation.attributeNames;
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
public class AttributeNamesDLAnnotationTest
{

	private final static Logger log = LogManager.getLogger(AttributeNamesDLAnnotationTest.class.getName());

	@attributeNames(pattern = "m_.+", typePattern = "^Path$")
	@attributeNames(pattern = "s_.+", typePattern = "^String$")
	public static class AttributeNamesClass
	{

		public Path m_x;
		public String s_str;
	}
	
	public static class DerivedAttributeNamesClass extends AttributeNamesClass
	{
		public Path m_path; // is validated because of parent class annotations
	}
	
	@Test
	public void validJavaAttributeNames() throws DLException
	{
		DefaultCore core = new DefaultCore();
		DLType type = core.defineType(AttributeNamesClass.class);
		DLType derivedType = core.defineType(DerivedAttributeNamesClass.class);
		Assert.assertTrue(
			type.hasAnnotation(AttributeNamesDLAnnotation.class),
			"@attributeNames should be mapped for type AttributeNamesClass"
		);
	}

	@Test
	public void validAttributeNames() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("validAttributeNames",
			"type T @attributeNames(pattern : \"^m_.+\") { int m_x; int m_y; }");
		DLType T = core.getType("T").orElseThrow();
		Assert.assertTrue(
			T.hasAnnotation(AttributeNamesDLAnnotation.class),
			"@attributeNames should be mapped for type T"
		);
	}

	@Test
	public void validAttributeNamesMultiplePartial() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("validAttributeNamesMultiplePartial",
			"type T "
			+ "@attributeNames(\"^m_.+\", \"^Integer$\") "
			+ "@attributeNames(typePattern : \"^String$\", pattern : \"^s_.+\") "
			+ "@attributeNames(pattern : \"^[a-z_]+$\") "
			+ "{ int m_x; String s_display; int m_y; String s_description; }");
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidAttributeNames() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidAttributeNames",
			"type T @attributeNames(pattern : \"^m_.+\") { int mx; }");
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidAttributeNamesPartial() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidAttributeNamesPartial",
			"type T @attributeNames(pattern : \"^s_.+\", typePattern : \"^String$\") { int x; String mStr; }");
	}

	@Test
	public void validInheritedAttributeNames() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("validInheritedAttributeNames",
			"abstract type B @attributeNames(pattern : \"^m_.+\") {} "
			+ "type T extends B { int m_x; int m_y; }");
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidInheritedAttributeNames() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidInheritedAttributeNames",
			"abstract type B @attributeNames(pattern : \"^m_.+\") {} "
			+ "type T extends B { int mx; }");
	}

	@Test(expectedExceptions = InvalidAnnotation.class)
	public void invalidAttributeNamesPattern() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidTypeNamePattern",
			"abstract type T @attributeNames(pattern : \"^[m_.+\") {}");
	}

	@Test(expectedExceptions = InvalidAnnotation.class)
	public void invalidAttributeNamesTypePattern() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidAttributeNamesTypePattern",
			"abstract type T @attributeNames(pattern : \"^m_.+\", typePattern : \"^[int.+\") {}");
	}
}