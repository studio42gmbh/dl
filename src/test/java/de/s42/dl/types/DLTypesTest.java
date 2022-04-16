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
package de.s42.dl.types;

import de.s42.dl.DLCore;
import de.s42.dl.core.BaseDLCore;
import de.s42.dl.exceptions.UndefinedAnnotation;
import de.s42.dl.exceptions.UndefinedType;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.exceptions.InvalidType;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class DLTypesTest
{

	@Test
	public void externTypeDefined() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "extern type String;");
	}

	@Test(expectedExceptions = UndefinedType.class)
	public void externTypeNotDefined() throws DLException
	{
		DLCore core = new BaseDLCore();
		core.parse("Anonymous", "extern type String;");
	}

	@Test(expectedExceptions = InvalidAnnotation.class)
	public void externTypeNoAnnotationAllowed() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "extern type String @dynamic;");
	}

	@Test(expectedExceptions = InvalidType.class)
	public void externTypeAbstractNotAllowed() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "extern abstract type String;");
	}

	@Test(expectedExceptions = UndefinedType.class)
	public void typeParentNotDefined() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T extends ParentType;");
	}

	@Test
	public void typeAnnotationDefined() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T @dynamic;");
	}

	@Test(expectedExceptions = UndefinedAnnotation.class)
	public void typeAnnotationNotDefined() throws DLException
	{
		DLCore core = new BaseDLCore();
		core.parse("Anonymous", "type T @dynamic;");
	}

	@Test
	public void typeAttributeTypeDefined() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { UUID id; }");
	}

	@Test(expectedExceptions = UndefinedType.class)
	public void typeAttributeTypeNotDefined() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { A id; }");
	}

	/*@Test(expectedExceptions = InvalidType.class)
	public void invalidComplexTypeAssigned()
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous","type A; type B { A value; } B test : Hallo;");
	}*/
	@Test(expectedExceptions = InvalidType.class)
	public void invalidAbstractTypeAssigned() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "abstract type A; A test : Hallo;");
	}

	@Test
	public void validSimpleTypeAssigned() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type A; A test : Hallo;");
	}
}
