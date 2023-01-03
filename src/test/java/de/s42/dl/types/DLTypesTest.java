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

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.DLInstance;
import de.s42.dl.DLModule;
import de.s42.dl.DLType;
import de.s42.dl.core.BaseDLCore;
import de.s42.dl.exceptions.UndefinedAnnotation;
import de.s42.dl.exceptions.UndefinedType;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.exceptions.InvalidType;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidAttribute;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.exceptions.InvalidValue;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

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

	/**
	 * Tests https://github.com/studio42gmbh/dl/issues/3
	 *
	 * @throws de.s42.dl.exceptions.DLException never thrown here
	 */
	@Test
	public void externDLTypeDefinition() throws DLException
	{
		BaseDLCore core = new BaseDLCore();
		core.addResolver(DefaultCore.STRING_RESOLVER);
		core.setAllowDefineTypes(true);
		core.parse("externDLTypeDefinition",
			"extern type de.s42.dl.types.primitive.StringDLType;"
		);
		core.parse("externDLTypeDefinition2",
			"type T { de.s42.dl.types.primitive.StringDLType val; }"
		);
		core.parse("externDLTypeDefinition3",
			"type T2 { String val; }"
		);
	}

	@Test
	public void externTypeRedefined() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("externTypeRedefined",
			"extern type String;"
		);
	}

	@Test
	public void externTypeDefinition() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("externTypeDefinition",
			"extern type de.s42.dl.types.DLTypesTest$TestClass; "
			+ "de.s42.dl.types.DLTypesTest$TestClass t;"
		);
	}

	/**
	 * extern and abstract not allowed in parser
	 *
	 * @throws DLException
	 * @throws RuntimeException expected -> "no viable alternative at input 'externabstract'"
	 */
	@Test(expectedExceptions = RuntimeException.class)
	public void invalidExternTypeNoAbstractAllowed() throws DLException, RuntimeException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidExternTypeNoAbstractAllowed",
			"extern abstract type de.s42.dl.types.DLTypesTest$TestClass;"
		);
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidExternTypeNoBodyAllowed() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidExternTypeNoBodyAllowed",
			"extern type de.s42.dl.types.DLTypesTest$TestClass {}"
		);
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidExternTypeNoExtendsAllowed() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidExternTypeNoExtendsAllowed",
			"extern type de.s42.dl.types.DLTypesTest$TestClass extends Object;"
		);
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidExternTypeNoContainsAllowed() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidExternTypeNoContainsAllowed",
			"extern type de.s42.dl.types.DLTypesTest$TestClass contains Object;"
		);
	}

	@Test(expectedExceptions = InvalidAnnotation.class)
	public void invalidExternTypeNoAnnotationAllowed() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidExternTypeNoAnnotationAllowed",
			"extern type de.s42.dl.types.DLTypesTest$TestClass @dynamic;"
		);
	}

	@Test(expectedExceptions = UndefinedType.class)
	public void invalidTypeParentNotDefined() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidTypeParentNotDefined",
			"type T extends ParentType;"
		);
	}

	@Test
	public void finalTypeDefined() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("finalTypeDefined",
			"final type T;"
		);
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidDeriveFinalType() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidDeriveFinalType",
			"final type T; type U extends T;"
		);
	}

	@Test
	public void abstractTypeDefined() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("abstractTypeDefined",
			"abstract type T;"
		);
	}

	@Test(expectedExceptions = InvalidInstance.class)
	public void invalidInstantiateAbstractType() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidInstantiateAbstractType",
			"abstract type T; T test;"
		);
	}

	@Test
	public void typeWithAnnotationDefined() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("typeWithAnnotationDefined",
			"type T @dynamic;"
		);
	}

	@Test(expectedExceptions = UndefinedAnnotation.class)
	public void invalidTypeAnnotationNotDefined() throws DLException
	{
		BaseDLCore core = new BaseDLCore();
		core.addResolver(DefaultCore.STRING_RESOLVER);
		core.setAllowDefineTypes(true);
		core.parse("invalidTypeAnnotationNotDefined",
			"type T @dynamic;"
		);
	}

	@Test
	public void typeAttributeTypeDefined() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("typeAttributeTypeDefined",
			"type T { UUID id; }"
		);
	}

	@Test(expectedExceptions = UndefinedType.class)
	public void invalidTypeAttributeTypeNotDefined() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidTypeAttributeTypeNotDefined",
			"type T { A id; }"
		);
	}

	// @todo https://github.com/studio42gmbh/dl/issues/12 raise exception when trying to assign into a complex type
	@Test(expectedExceptions = InvalidType.class)
	public void invalidComplexTypeAssignedInModule() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidComplexTypeAssignedInModule",
			"type A; type B { A value; } B test : Hallo;"
		);
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidAbstractTypeAssignedInModule() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidAbstractTypeAssignedInModule",
			"abstract type A; A test : Hallo;"
		);
	}

	@Test(expectedExceptions = InvalidValue.class)
	public void invalidSimpleTypeAssigned() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidSimpleTypeAssigned",
			"type A; A test2 : 1.34;"
		);
	}

	@Test
	public void aliasType() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("aliasType",
			"type T alias U, V;"
		);
		DLType typeU = core.getType("U").orElseThrow();
		assertEquals(typeU.getName(), "T");
		DLType typeV = core.getType("V").orElseThrow();
		assertEquals(typeV.getName(), "T");
	}

	@Test
	public void externAliasType() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("externAliasType",
			"extern type de.s42.dl.types.DLTypesTest$TestClass alias U, V;"
		);
		DLType typeU = core.getType("U").orElseThrow();
		assertEquals(typeU.getName(), "de.s42.dl.types.DLTypesTest$TestClass");
		DLType typeV = core.getType("V").orElseThrow();
		assertEquals(typeV.getName(), "de.s42.dl.types.DLTypesTest$TestClass");
	}

	@Test
	public void extendParentsWithCombinedAttributes() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("extendParentsWithCombinedAttributes",
			"type A { int x; } "
			+ "type B { float y; } "
			+ "type C extends A, B { String z; float y @required; }"
		);
		//DLType C = core.getType("C").orElseThrow();
		//log.warn(DLHelper.describe(C));
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidTypeWith2SameNameAttributes() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidTypeWith2SameNameAttributes",
			"type A { int x; float x; }"
		);
	}

	@Test
	public void extendParentsAttributeMoreSpecificType() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("extendParentsAttributeMoreSpecificType",
			"type V; type U extends V; "
			+ "type A { V x; } "
			+ "type B extends A { U x; }"
		);
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidExtendParentsAttributeLessSpecificType() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidExtendParentsAttributeLessSpecificType",
			"type V; type U extends V;"
			+ "type A { U x; } "
			+ "type B extends A { V x; }"
		);
	}

	/**
	 * This extension mainly supports interface implementation approaches where the interface just defines a getter and
	 * the impl then has a full property
	 *
	 * @throws DLException
	 */
	@Test
	public void extendParentsAttributeFromReadOnly() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("extendParentsAttributeFromReadOnly",
			"abstract type A { int x @readonly; } "
			+ "type B extends A { int x; }"
		);
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidExtendParentsAttributeToReadOnly() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidExtendParentsAttributeToReadOnly",
			"type A { int x; } "
			+ "type B extends A { int x @readonly; }"
		);
	}

	@Test
	public void compatibleParentTypeAttributesWithSameName() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("compatibleParentTypeAttributesWithSameName",
			"type A { int x @required @greater(y); int y;} "
			+ "type B { int x @greater(y) @required; int y; int z; } "
			+ "type C extends A, B;"
		);
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidIncompatibleParentTypeAttributesWithSameNameDifferentAnnotations() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidIncompatibleParentTypeAttributesWithSameNameDifferentAnnotations",
			"type A { int x @required @greater(y); } "
			+ "type B { int x @greater(z) @required; } "
			+ "type C extends A, B;"
		);
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidIncompatibleParentTypeAttributesWithSameNameDifferentTypes() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidIncompatibleParentTypeAttributesWithSameNameDifferentTypes",
			"type A { int x; } "
			+ "type B { String x; } "
			+ "type C extends A, B;"
		);
	}

	public static class TestDefine
	{

		public Object[] objArrayVal;
		public String strVal;
		public int intVal;
		public long longVal;
		public float floatVal;
		public double doubleVal;
		public boolean booleanVal;
		public TestDefine refVal;
		@AttributeDL(ignore = true)
		public boolean ignoredVal;
	}

	@Test
	public void javaDefineAssignArray() throws DLException
	{
		DefaultCore core = new DefaultCore();
		DLType type = core.defineType(TestDefine.class, "TestDefine");
		//log.warn(DLHelper.describe(type));
		DLModule module = core.parse("testJavaDefineAssignArray",
			"TestDefine tRef;"
			+ "TestDefine t { "
			+ "objArrayVal : 42, 1.23, true, \"Test\"; "
			+ "strVal : \"Test\";"
			+ "intVal : 42;"
			+ "longVal : 420;"
			+ "floatVal : 4.31;"
			+ "doubleVal : 1.34;"
			+ "booleanVal : true;"
			+ "refVal : $tRef;"
			+ "}"
		);

		DLInstance tRef = module.getChild("tRef").orElseThrow();
		DLInstance t = module.getChild("t").orElseThrow();

		assertEquals((Object[]) t.get("objArrayVal"), new Object[]{42L, 1.23, true, "Test"});
		assertEquals(t.get("strVal"), "Test");
		assertEquals(t.get("intVal"), 42);
		assertEquals(t.get("longVal"), 420L);
		assertEquals(t.get("floatVal"), 4.31f);
		assertEquals(t.get("doubleVal"), 1.34);
		assertEquals(t.get("booleanVal"), true);
		assertEquals(t.get("refVal"), tRef);
	}

	@Test(expectedExceptions = InvalidAttribute.class)
	public void invalidUseOfIgnoredValue() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.defineType(TestDefine.class, "TestDefine");
		core.parse("testJavaDefineAssignArray",
			"TestDefine t { ignoredVal : true; bla : true; }"
		);
	}

	@Test
	public void aliasRedefineExternTypeAndAliases() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("aliasRedefineTypeAndAliases",
			"extern type de.s42.dl.types.primitive.StringDLType alias java.lang.String, String, string, str;"
		);
	}

	// @todo
	@Test
	public void invalidAliasRedefineExternTypeAndAliasesLocalDuplicate() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidAliasRedefineExternTypeAndAliasesLocalDuplicate",
			"extern type de.s42.dl.types.primitive.StringDLType alias java.lang.String, String, String, string, str;"
		);
	}
	
	@Test(expectedExceptions = InvalidType.class)
	public void invalidTypeExtendsItself() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidTypeExtendsItself",
			"type T extends T;"
		);
	}
	
}
