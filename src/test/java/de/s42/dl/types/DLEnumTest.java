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
import de.s42.dl.DLEnum;
import de.s42.dl.DLInstance;
import de.s42.dl.DLType;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidType;
import de.s42.dl.exceptions.InvalidValue;
import de.s42.dl.parser.DLHrfParsingException;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 *
 * @author Benjamin Schiller
 */
public class DLEnumTest
{

	public enum Status
	{
		New,
		InProgress,
		Done,
		Error,
	}

	@Test
	public void validExternEnumDefined() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("validExternEnumDefined", "extern enum de.s42.dl.types.DLEnumTest$Status;");
		core.parse("validExternEnumDefined2", "alias Status de.s42.dl.types.DLEnumTest$Status;");
		core.parse("validExternEnumDefined3", "type T { Status status : New; }");
		core.parse("validExternEnumDefined4", "T t @export; T t2 @export { status : Done; }");

		DLInstance t = core.getExported("t").orElseThrow();
		DLInstance t2 = core.getExported("t2").orElseThrow();

		Assert.assertEquals(t.get("status"), Status.New);
		Assert.assertEquals(t2.get("status"), Status.Done);
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidExternEnumNotDefined() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("Anonymous", "extern enum NotDefined;");
	}

	@Test
	public void validDefineEnum() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("validDefineEnum", "enum Status @noGenerics { New, InProgress, Done, Error, } type T { Status status : New; }");
	}

	@Test(expectedExceptions = InvalidValue.class)
	public void invalidDefineInvalidEnumDuplicateEnumValue() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidDefineInvalidEnumDuplicateEnumValue", "enum Status { New, InProgress, Done, Error, New }");
	}

	@Test
	public void validUseEnumValueAsDefaultInType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.defineType(core.createEnum("Status", Status.class));
		core.parse("validUseEnumValueAsDefaultInType", "type UseEnum { Status status : New; }");
	}

	@Test(expectedExceptions = DLHrfParsingException.class)
	public void invalidUseIncorrectEnumValueAsDefaultInType() throws DLException
	{
		DLCore core = new DefaultCore();
		core.defineType(core.createEnum("Status", Status.class));
		core.parse("invalidUseIncorrectEnumValueAsDefaultInType", "type UseEnum { Status status : NotInEnum; }");
	}

	@Test(expectedExceptions = InvalidValue.class)
	public void invalidValueNotInCodeDefinedEnumTypedef() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidValueNotInCodeDefinedEnumTypedef", "enum Status { New, InProgress, Done, Error, New } type Test { Status t : NotInEnum; }");
	}

	@Test(expectedExceptions = InvalidValue.class)
	public void invalidValueNotInCodeDefinedEnumInstancedef() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidValueNotInCodeDefinedEnumInstancedef", "enum Status { New, InProgress, Done, Error, New } type Test { Status t; } Test { t : NotInEnum; }");
	}

	@Test
	public void validJavaType() throws DLException
	{
		DLCore core = new DefaultCore();
		DLEnum status = core.createEnum("Status", Status.class);

		Assert.assertEquals(status.valueOf("New"), Status.New);
		Assert.assertEquals(status.valueOf("Done"), Status.Done);
		Assert.assertEquals(status.valueOf("Error"), Status.Error);
		Assert.assertEquals(status.valueOf("InProgress"), Status.InProgress);
	}

	@Test(expectedExceptions = InvalidValue.class)
	public void invalidNotContainedInJavaType() throws DLException
	{
		DLCore core = new DefaultCore();
		DLEnum status = core.createEnum("Status", Status.class);

		status.valueOf("NotContained");
	}
	
	@Test
	public void validAliasEnum() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("validAliasEnum", "enum T alias U, V { A, B }");
		DLType typeU = core.getType("U").orElseThrow();
		Assert.assertEquals(typeU.getName(), "T");
		DLType typeV = core.getType("V").orElseThrow();
		Assert.assertEquals(typeV.getName(), "T");
	}

	@Test
	public void validExternAliasEnum() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("validExternAliasEnum", "extern enum de.s42.dl.types.DLEnumTest$Status alias U, V;");
		DLType typeU = core.getType("U").orElseThrow();
		Assert.assertEquals(typeU.getName(), "de.s42.dl.types.DLEnumTest$Status");
		DLType typeV = core.getType("V").orElseThrow();
		Assert.assertEquals(typeV.getName(), "de.s42.dl.types.DLEnumTest$Status");
	}	
}
