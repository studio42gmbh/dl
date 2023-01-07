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
package de.s42.dl;

import de.s42.dl.annotations.attributes.RequiredDLAnnotation.required;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidCore;
import de.s42.dl.exceptions.InvalidInstance;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class DLBasicTest
{

	public static class TestData
	{

		protected String name;

		@required
		protected int id;

		@required
		protected String login;

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public int getId()
		{
			return id;
		}

		public void setId(int id)
		{
			this.id = id;
		}

		public String getLogin()
		{
			return login;
		}

		public void setLogin(String login)
		{
			this.login = login;
		}
	}

	@Test
	public void validEmptyModule() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("validEmptyModule", "");
	}

	@Test(expectedExceptions = InvalidCore.class)
	public void invalidDisallowedDefineTypes() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.setAllowDefineTypes(false);
		core.parse("invalidDisallowedDefineTypes", "type A;");
	}

	@Test
	public void validSimpleDataFromJavaClassType() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.defineType(core.createType(TestData.class));

		DLModule module = core.parse("validSimpleDataFromJavaClassType", 
			"de.s42.dl.DLBasicTest$TestData data1 { login: \"TestName\"; id : 14; }"
		);
		
		TestData data1 = (TestData) module.getChildAsJavaObject("data1").get();

		Assert.assertEquals(data1.getName(), "data1");
		Assert.assertEquals(data1.getLogin(), "TestName");
		Assert.assertEquals(data1.getId(), 14);
	}

	@Test(expectedExceptions = InvalidInstance.class)
	public void invalidSimpleDataFromJavaClassTypeMissingRequiredAttributeLogin() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.defineType(core.createType(TestData.class));

		// attribute login missing
		core.parse("invalidSimpleDataFromJavaClassTypeMissingRequiredAttributeLogin",
			"de.s42.dl.DLBasicTest$TestData data1 { id : 14; }"
		);
	}

	/**
	 * See https://github.com/studio42gmbh/dl/issues/26
	 *
	 * @throws DLException
	 */
	@Test
	public void validExpressionIntHexadecimal() throws DLException
	{
		DefaultCore core = new DefaultCore();
		
		DLModule module = core.parse("validExpressionIntHexadecimal", 
			"Integer t : 0x35;"
		);
		
		Assert.assertEquals(module.getInt("t"), 0x35);
	}

	/**
	 * See https://github.com/studio42gmbh/dl/issues/26
	 *
	 * @throws DLException
	 */
	@Test
	public void validExpressionIntOctal() throws DLException
	{
		DefaultCore core = new DefaultCore();
		
		DLModule module = core.parse("validExpressionIntOctal", 
			"Integer t : 035;"
		);
		
		Assert.assertEquals(module.getInt("t"), 035);
	}

	/**
	 * See https://github.com/studio42gmbh/dl/issues/26
	 *
	 * @throws DLException
	 */
	@Test
	public void validExpressionIntBinary() throws DLException
	{
		DefaultCore core = new DefaultCore();
		
		DLModule module = core.parse("validExpressionIntBinary", 
			"Integer t : 0b1011010;"
		);
		
		Assert.assertEquals(module.getInt("t"), 0b1011010);
	}
}
