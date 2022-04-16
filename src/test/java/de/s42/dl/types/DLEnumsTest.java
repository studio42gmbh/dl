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
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.UndefinedEnum;
import de.s42.dl.exceptions.InvalidEnumValue;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class DLEnumsTest
{

	public enum Status
	{
		New,
		InProgress,
		Done,
		Error,
	}

	@Test
	public void externEnumDefined() throws DLException
	{
		DLCore core = new DefaultCore();
		core.defineType(core.createEnum("Status", Status.class));
		core.parse("Anonymous", "extern enum Status;");
	}

	@Test(expectedExceptions = {UndefinedEnum.class})
	public void externEnumNotDefined() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "extern enum Status;");
	}

	@Test
	public void defineEnum() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "enum Status { New, InProgress, Done, Error, }");
	}

	@Test(expectedExceptions = {InvalidEnumValue.class})
	public void defineInvalidEnumDuplicateEnumValue() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "enum Status { New, InProgress, Done, Error, New }");
	}

	@Test
	public void useEnumValueAsDefaultInType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.defineType(core.createEnum("Status", Status.class));
		core.parse("Anonymous", "type UseEnum { Status status : New; }");
	}

	@Test(expectedExceptions = {InvalidEnumValue.class})
	public void useIncorrectEnumValueAsDefaultInType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.defineType(core.createEnum("Status", Status.class));
		core.parse("Anonymous", "type UseEnum { Status status : NotInEnum; }");
	}

	@Test(expectedExceptions = {InvalidEnumValue.class})
	public void invalidValueNotInCodeDefinedEnumTypedef() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "enum Status { New, InProgress, Done, Error, New } type Test { Status t : NotInEnum; }");
	}

	@Test(expectedExceptions = {InvalidEnumValue.class})
	public void invalidValueNotInCodeDefinedEnumInstancedef() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "enum Status { New, InProgress, Done, Error, New } type Test { Status t; } Test { t : NotInEnum; }");
	}
}
