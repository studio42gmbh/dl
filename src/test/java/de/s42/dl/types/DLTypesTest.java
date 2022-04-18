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

import de.s42.base.testing.AssertHelper;
import de.s42.dl.DLCore;
import de.s42.dl.DLInstance;
import de.s42.dl.DLModule;
import de.s42.dl.core.BaseDLCore;
import de.s42.dl.exceptions.UndefinedAnnotation;
import de.s42.dl.exceptions.UndefinedType;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.exceptions.InvalidType;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.util.DLHelper;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class DLTypesTest
{
	private final static Logger log = LogManager.getLogger(DLTypesTest.class.getName());
	
	public static class TestClass
	{

		protected double doubleValue;

		public double getDoubleValue()
		{
			return doubleValue;
		}

		public void setDoubleValue(double doubleValue)
		{
			this.doubleValue = doubleValue;
		}
	}	

	@Test(expectedExceptions = InvalidType.class)
	public void invalidExternTypeDefined() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "extern type String;");
	}

	@Test
	public void validExternTypeDefinition() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "extern type de.s42.dl.types.DLTypesTest$TestClass;");
		log.debug(DLHelper.toString(core.getType(TestClass.class).orElseThrow()));
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidExternTypeNoAbstractAllowed() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "extern abstract type de.s42.dl.types.DLTypesTest$TestClass;");
	}
	
	@Test(expectedExceptions = InvalidType.class)
	public void invalidExternTypeNoBodyAllowed() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "extern type de.s42.dl.types.DLTypesTest$TestClass {}");
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidExternTypeNoExtendsAllowed() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "extern type de.s42.dl.types.DLTypesTest$TestClass extends Object;");
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidExternTypeNoContainsAllowed() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "extern type de.s42.dl.types.DLTypesTest$TestClass contains Object;");
	}
	
	@Test(expectedExceptions = InvalidAnnotation.class)
	public void invalidExternTypeNoAnnotationAllowed() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "extern type de.s42.dl.types.DLTypesTest$TestClass @dynamic;");
	}

	@Test(expectedExceptions = UndefinedType.class)
	public void invalidTypeParentNotDefined() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T extends ParentType;");
	}

	@Test
	public void validFinalType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "final type T;");
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidDeriveFinalType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "final type T; type U extends T;");
	}

	@Test
	public void validAbstractType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "abstract type T;");
	}

	@Test(expectedExceptions = InvalidInstance.class)
	public void invalidInstantiateAbstractType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "abstract type T; T test;");
	}

	@Test
	public void validTypeAnnotationDefined() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T @dynamic;");
	}

	@Test(expectedExceptions = UndefinedAnnotation.class)
	public void invalidTypeAnnotationNotDefined() throws DLException
	{
		DLCore core = new BaseDLCore();
		core.parse("Anonymous", "type T @dynamic;");
	}

	@Test
	public void validTypeAttributeTypeDefined() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { UUID id; }");
	}

	@Test(expectedExceptions = UndefinedType.class)
	public void invalidTypeAttributeTypeNotDefined() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "type T { A id; }");
	}

	// @todo raise exception when trying to assign into a complex type
	@Test(expectedExceptions = InvalidType.class, enabled = false)
	public void invalidComplexTypeAssigned() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous","type A; type B { A value; } B test : Hallo;");
	}
	
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
		DLModule module = core.parse("Anonymous", "type A; A test : Hallo; A test2 : 1.34;");
		Object value = module.get("test");
		Double value2 = module.get("test2");
		Assert.assertEquals(value, "Hallo");
		AssertHelper.assertEpsilonEquals(value2, 1.34);
	}
}
