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
package de.s42.dl.parser;

import de.s42.dl.DLCore;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.ReservedKeyword;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class ReservedKeywordsTest
{
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseNew() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int new : 42;";
		core.parse("invalidUseNew", data);
	}

	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseCopy() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int copy : 42;";
		core.parse("invalidUseCopy", data);
	}
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseScope() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int scope : 42;";
		core.parse("invalidUseScope", data);
	}
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUsePackage() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int package : 42;";
		core.parse("invalidUsePackage", data);
	}

	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseModule() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int module : 42;";
		core.parse("invalidUseModule", data);
	}
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseNamespace() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int namespace : 42;";
		core.parse("invalidUseNamespace", data);
	}
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseDefine() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int define : 42;";
		core.parse("invalidUseDefine", data);
	}
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseUndef() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int undef : 42;";
		core.parse("invalidUseUndef", data);
	}
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseIn() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int in : 42;";
		core.parse("invalidUseIn", data);
	}
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseContained() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int contained : 42;";
		core.parse("invalidUseContained", data);
	}
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseAnd() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int and : 42;";
		core.parse("invalidUseAnd", data);
	}
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseOr() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int or : 42;";
		core.parse("invalidUseOr", data);
	}
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseNot() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int not : 42;";
		core.parse("invalidUseNot", data);
	}
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseNand() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int nand : 42;";
		core.parse("invalidUseNand", data);
	}
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseXor() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int xor : 42;";
		core.parse("invalidUseXor", data);
	}

	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseVolatile() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int volatile : 42;";
		core.parse("invalidUseVolatile", data);
	}
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseAtomic() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int atomic : 42;";
		core.parse("invalidUseAtomic", data);
	}
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseUnion() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int union : 42;";
		core.parse("invalidUseUnion", data);
	}
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseStruct() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int struct : 42;";
		core.parse("invalidUseStruct", data);
	}
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseTemplate() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int template : 42;";
		core.parse("invalidUseTemplate", data);
	}	

	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseClass() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int class : 42;";
		core.parse("invalidUseClass", data);
	}	

	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseInterface() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int interface : 42;";
		core.parse("invalidUseInterface", data);
	}	

	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseImplements() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int implements : 42;";
		core.parse("invalidUseImplements", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseContract() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int contract : 42;";
		core.parse("invalidUseContract", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseConcept() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int concept : 42;";
		core.parse("invalidUseConcept", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseIf() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int if : 42;";
		core.parse("invalidUseIf", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUsePrivate() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int private : 42;";
		core.parse("invalidUsePrivate", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseThis() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int this : 42;";
		core.parse("invalidUseThis", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseThrow() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int throw : 42;";
		core.parse("invalidUseThrow", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseElse() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int else : 42;";
		core.parse("invalidUseElse", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseImport() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int import : 42;";
		core.parse("invalidUseImport", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUsePublic() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int public : 42;";
		core.parse("invalidUsePublic", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseProtected() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int protected : 42;";
		core.parse("invalidUseProtected", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseThrows() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int throws : 42;";
		core.parse("invalidUseThrows", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseBreak() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int break : 42;";
		core.parse("invalidUseBreak", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseReturn() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int return : 42;";
		core.parse("invalidUseReturn", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseCase() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int case : 42;";
		core.parse("invalidUseCase", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseStatic() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int static : 42;";
		core.parse("invalidUseStatic", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseTry() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int try : 42;";
		core.parse("invalidUseTry", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseCatch() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int catch : 42;";
		core.parse("invalidUseCatch", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseVoid() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int void : 42;";
		core.parse("invalidUseVoid", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseNull() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int null : 42;";
		core.parse("invalidUseNull", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseLambda() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int lambda : 42;";
		core.parse("invalidUseLambda", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseDo() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int do : 42;";
		core.parse("invalidUseDo", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseWhile() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int while : 42;";
		core.parse("invalidUseWhile", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseFor() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int for : 42;";
		core.parse("invalidUseFor", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseDefault() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int default : 42;";
		core.parse("invalidUseDefault", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseInstanceof() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int instanceof : 42;";
		core.parse("invalidUseInstanceof", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseAssert() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int assert : 42;";
		core.parse("invalidUseAssert", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseUse() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int use : 42;";
		core.parse("invalidUseUse", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseUnuse() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int unuse : 42;";
		core.parse("invalidUseUnuse", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseCall() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int call : 42;";
		core.parse("invalidUseCall", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseClone() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int clone : 42;";
		core.parse("invalidUseClone", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseSelect() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int select : 42;";
		core.parse("invalidUseSelect", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseWhen() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int when : 42;";
		core.parse("invalidUseWhen", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseSwitch() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int switch : 42;";
		core.parse("invalidUseSwitch", data);
	}	
	
	@Test(expectedExceptions = ReservedKeyword.class)
	public void invalidUseDistinct() throws Exception
	{
		DLCore core = new DefaultCore();
		String data = "int distinct : 42;";
		core.parse("invalidUseDistinct", data);
	}	
}
