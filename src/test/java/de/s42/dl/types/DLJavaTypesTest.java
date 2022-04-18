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
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.exceptions.InvalidValue;
import de.s42.dl.*;
import de.s42.dl.DLAnnotation.AnnotationDL;
import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.annotations.DynamicDLAnnotation;
import de.s42.dl.annotations.LengthDLAnnotation;
import de.s42.dl.annotations.RequiredDLAnnotation;
import de.s42.dl.annotations.UniqueDLAnnotation;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.util.UUID;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class DLJavaTypesTest
{

	private final static Logger log = LogManager.getLogger(DLJavaTypesTest.class.getName());

	@AnnotationDL(DynamicDLAnnotation.DEFAULT_SYMBOL)
	public static class TestClass
	{

		protected String name;

		@AttributeDL(required = true)
		protected double doubleVal;

		@AttributeDL(defaultValue = "42")
		protected int intVal;

		@AnnotationDL(value = LengthDLAnnotation.DEFAULT_SYMBOL, parameters = {"10", "20"})
		@AnnotationDL(UniqueDLAnnotation.DEFAULT_SYMBOL)
		protected String stringVal;

		protected UUID uuidVal;

		public double getDoubleVal()
		{
			return doubleVal;
		}

		public void setDoubleVal(double doubleVal)
		{
			this.doubleVal = doubleVal;
		}

		public int getIntVal()
		{
			return intVal;
		}

		public void setIntVal(int intVal)
		{
			this.intVal = intVal;
		}

		public String getStringVal()
		{
			return stringVal;
		}

		public void setStringVal(String stringVal)
		{
			this.stringVal = stringVal;
		}

		public UUID getUuidVal()
		{
			return uuidVal;
		}

		public void setUuidVal(UUID uuidVal)
		{
			this.uuidVal = uuidVal;
		}

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}
	}

	protected static class TestType extends DefaultDLType
	{

		public final static String DEFAULT_SYMBOL = "Test";
		public final static String ATTRIBUTE_VALUE = "value";

		public TestType(DLCore core) throws DLException
		{
			super(DEFAULT_SYMBOL);

			init(core);
		}

		private void init(DLCore core) throws DLException
		{
			assert core != null;

			// extends
			addParent(core.getType(ObjectDLType.DEFAULT_SYMBOL).get());

			// contains
			addContainedType(this);

			// attributes - value
			DLAttribute valueAttrib = core.createAttribute(ATTRIBUTE_VALUE, FloatDLType.DEFAULT_SYMBOL);
			addAttribute(valueAttrib);

			//add annotation @required to attribute value
			core.addAnnotationToAttribute(this, valueAttrib, RequiredDLAnnotation.DEFAULT_SYMBOL);
		}
	}

	protected DLCore createCore() throws DLException
	{
		DLCore core = new DefaultCore();
		core.defineType(new TestType(core));
		core.defineType(core.createType(TestClass.class), "TestC");

		return core;
	}

	@Test
	public void validJavaTypeDefined() throws DLException
	{
		DLCore core = createCore();
		core.parse("Anonymous", "Test { value : 1.0; Test { value : 2.0; } }");
	}

	@Test(expectedExceptions = InvalidValue.class)
	public void invalidJavaTypeDefinedAttributeData() throws DLException
	{
		DLCore core = createCore();
		core.parse("Anonymous", "Test { value : \"Test\"; }");
	}

	@Test(expectedExceptions = InvalidInstance.class)
	public void invalidJavaTypeContained() throws DLException
	{
		DLCore core = createCore();
		core.parse("Anonymous", "type B; Test { value : 1.0; B; }");
	}

	@Test(expectedExceptions = InvalidInstance.class)
	public void invalidJavaTypeDefinedAttributeMissing() throws DLException
	{
		DLCore core = createCore();
		core.parse("Anonymous", "Test;");
	}

	@Test
	public void validTypeFromClass() throws DLException
	{
		DLCore core = createCore();

		//log.debug("\n", DLHelper.describe(core.getType("TestC")));
		DLModule module = core.parse("Anonymous",
			"TestC test1 { "
			+ "uuidVal : \"16cfc033-3597-49fe-a991-24b533dbfeb6\"; "
			+ "stringVal : \"String long enough\";"
			+ "doubleVal : 1.2345; "
			+ "}");
		TestClass test1 = module.getChild(0).toJavaObject(core);
		Assert.assertEquals(test1.getName(), "test1");
		Assert.assertEquals(test1.getUuidVal(), UUID.fromString("16cfc033-3597-49fe-a991-24b533dbfeb6"));
		Assert.assertEquals(test1.getIntVal(), 42); // retrieved from default value
		AssertHelper.assertEpsilonEquals(test1.getDoubleVal(), 1.2345);
	}

	@Test(expectedExceptions = InvalidInstance.class)
	public void invalidTypeFromClassMissingRequiredAttribute() throws DLException
	{
		DLCore core = createCore();
		core.parse("Anonymous", "TestC test1 {}");
	}

	@Test(expectedExceptions = InvalidInstance.class)
	public void invalidTypeFromClassInvalidLengthAttribute() throws DLException
	{
		DLCore core = createCore();
		core.parse("Anonymous",
			"TestC test1 { "
			+ "uuidVal : \"16cfc033-3597-49fe-a991-24b533dbfeb6\"; "
			+ "stringVal : \"String long enough\";"
			+ "}");
	}

	@Test(expectedExceptions = InvalidInstance.class)
	public void invalidTypeFromClassInvalidUniqueAttribute() throws DLException
	{
		DLCore core = createCore();
		core.parse("Anonymous",
			"TestC test1 { "
			+ "stringVal : \"ShouldBeUnique\";"
			+ "doubleVal : 1.2345; "
			+ "}");
		core.parse("Anonymous2",
			"TestC test2 { "
			+ "stringVal : \"ShouldBeUnique\";" // is not unique
			+ "doubleVal : 1.2345; "
			+ "}");
	}
}
