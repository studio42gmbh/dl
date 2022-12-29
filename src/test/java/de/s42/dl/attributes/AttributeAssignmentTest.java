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
package de.s42.dl.attributes;

import de.s42.dl.DLInstance;
import de.s42.dl.DLModule;
import de.s42.dl.DLType;
import de.s42.dl.core.BaseDLCore;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.core.resolvers.StringCoreResolver;
import de.s42.dl.exceptions.InvalidType;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.util.Arrays;
import java.util.Objects;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import static de.s42.base.testing.AssertHelper.*;
import de.s42.dl.exceptions.InvalidValue;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Benjamin Schiller
 */
public class AttributeAssignmentTest
{

	private final static Logger log = LogManager.getLogger(AttributeAssignmentTest.class.getName());

	@Test
	public void simpleModuleAttributeAssignment() throws Exception
	{
		DefaultCore core = new DefaultCore();
		DLModule module = core.parse("simpleModuleAttributeAssignment",
			"int x : 42; boolean y : true; float z : 3.14; String w : \"Test\";"
		);
		assertEquals(module.get("x"), 42);
		assertEquals(module.get("y"), true);
		assertEpsilonEquals(module.getFloat("z"), 3.14f);
		assertEquals(module.get("w"), "Test");
	}

	@Test
	public void simpleUntypedModuleAttributeAssignment() throws Exception
	{
		DefaultCore core = new DefaultCore();
		DLModule module = core.parse("simpleUntypedModuleAttributeAssignment",
			"x : 42; y : true; z : 3.14; w : \"Test\";"
		);
		assertEquals(module.get("x"), 42L);
		assertEquals(module.get("y"), true);
		assertEpsilonEquals(module.getDouble("z"), 3.14);
		assertEquals(module.get("w"), "Test");
	}

	@Test
	public void simpleUntypedModuleAttributeAssignmentInBaseCore() throws Exception
	{
		BaseDLCore core = new BaseDLCore();
		core.addResolver(new StringCoreResolver(core));
		core.setAllowDefineTypes(true);
		DLModule module = core.parse("simpleUntypedModuleAttributeAssignmentInBaseCore",
			"x : 42; y : true; z : 3.14; w : \"Test\";"
		);
		assertEquals(module.get("x"), 42L);
		assertEquals(module.get("y"), true);
		assertEpsilonEquals(module.getDouble("z"), 3.14);
		assertEquals(module.get("w"), "Test");
	}

	@Test
	public void simpleModuleAttributeArrayAssignment() throws Exception
	{
		DefaultCore core = new DefaultCore();
		DLModule module = core.parse("simpleModuleAttributeAssignment",
			"Array x : 42, true, \"Test\";"
		);
		assertEquals((Object[]) module.get("x"), new Object[]{42L, true, "Test"});
	}

	@Test
	public void simpleUntypedModuleAttributeArrayAssignment() throws Exception
	{
		DefaultCore core = new DefaultCore();
		DLModule module = core.parse("simpleModuleAttributeAssignment",
			"x : 42, true, \"Test\";"
		);
		assertEquals((Object[]) module.get("x"), new Object[]{42L, true, "Test"});
	}

	@Test
	public void simpleModuleAttributeGenericArrayAssignment() throws Exception
	{
		DefaultCore core = new DefaultCore();
		DLModule module = core.parse("simpleModuleAttributeGenericArrayAssignment",
			"Array<Integer> x : 42, 43;"
		);
		assertEquals((Object[]) module.get("x"), new Object[]{42, 43});
	}

	@Test
	public void simpleModuleAttributeGenericBooleanArrayAssignment() throws Exception
	{
		DefaultCore core = new DefaultCore();
		DLModule module = core.parse("simpleModuleAttributeGenericBooleanArrayAssignment",
			"Array<Boolean> x : true, false;"
		);
		assertEquals((Object[]) module.get("x"), new Object[]{true, false});
	}

	@Test(expectedExceptions = InvalidValue.class)
	public void invalidSimpleModuleAttributeGenericArrayAssignment() throws Exception
	{
		DefaultCore core = new DefaultCore();
		core.parse("invalidSimpleModuleAttributeGenericArrayAssignment",
			"Array<Boolean> x : 42, 43;"
		);
	}

	@Test
	public void simpleInstanceAttributeAssignment() throws Exception
	{
		DefaultCore core = new DefaultCore();
		DLModule module = core.parse("simpleInstanceAttributeAssignment",
			"type T { Integer x; } T t { x : 42; }"
		);
		assertEquals(module.getChild("t").orElseThrow().get("x"), 42);
	}

	@Test
	public void simpleInstanceTypedAttributeAssignment() throws Exception
	{
		DefaultCore core = new DefaultCore();
		DLModule module = core.parse("simpleInstanceTypedAttributeAssignment",
			"type T { Integer x; } T t { Integer x : 42; }"
		);
		assertEquals(module.getChild("t").orElseThrow().get("x"), 42);
	}

	@Test(expectedExceptions = InvalidType.class)
	public void invalidSimpleInstanceTypedAttributeAssignment() throws Exception
	{
		DefaultCore core = new DefaultCore();
		core.parse("simpleInstanceTypedAttributeAssignment",
			"type T { Integer x; } T t { Long x : 42; }"
		);
	}

	@Test
	public void simpleInstanceTypedArrayAttributeAssignment() throws Exception
	{
		DefaultCore core = new DefaultCore();
		DLModule module = core.parse("simpleInstanceTypedArrayAttributeAssignment",
			"type T { Array<Integer> x; } T t { Array<Integer> x : 42, 43; }"
		);
		assertEquals(module.getChild("t").orElseThrow().get("x"), new Object[]{42, 43});
	}

	@Test
	public void simpleInstanceUntypedArrayAttributeAssignment() throws Exception
	{
		DefaultCore core = new DefaultCore();
		DLModule module = core.parse("simpleInstanceTypedArrayAttributeAssignment",
			"type T { Array<Integer> x; } T t { x : 42, 43; }"
		);
		assertEquals(module.getChild("t").orElseThrow().get("x"), new Object[]{42, 43});
	}

	@Test
	public void simpleModuleAttributeInstanceValueAssignment() throws Exception
	{
		DefaultCore core = new DefaultCore();
		DLModule module = core.parse("simpleModuleAttributeInstanceValueAssignment",
			"type T { Integer x; } T t : T t2 @export { x : 42; };"
		);
		assertEquals(module.getInstance("t").get("x"), 42);
		assertEquals(core.getExported("t2").orElseThrow().get("x"), 42);

		// Ensure the instances retrieved are equal
		assertEquals(core.getExported("t2").orElseThrow().get("x"), module.getInstance("t").get("x"));
	}

	@Test
	public void simpleModuleAttributeInstanceArrayAssignment() throws Exception
	{
		DefaultCore core = new DefaultCore();
		DLModule module = core.parse("simpleModuleAttributeInstanceArrayAssignment",
			"type T { Integer x; } Array x : T t1 {}, T t2 {}, T t3 {};"
		);
		
		DLType type = module.getDefinedType("T").orElseThrow();
		DLInstance t1 = (DLInstance)((Object[]) module.get("x"))[0];
		DLInstance t2 = (DLInstance)((Object[]) module.get("x"))[1];
		DLInstance t3 = (DLInstance)((Object[]) module.get("x"))[2];

		assertEquals(t1.getType(), type);
		assertEquals(t1.getName(), "t1");
		assertEquals(t2.getType(), type);
		assertEquals(t2.getName(), "t2");
		assertEquals(t3.getType(), type);
		assertEquals(t3.getName(), "t3");
	}

	public static class TestT
	{

		public String name;
		public TestT[] data;

		// <editor-fold desc="hashCode, equals, toString" defaultstate="collapsed">
		@Override
		public int hashCode()
		{
			int hash = 5;
			hash = 41 * hash + Objects.hashCode(this.name);
			hash = 41 * hash + Arrays.deepHashCode(this.data);
			return hash;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final TestT other = (TestT) obj;
			if (!Objects.equals(this.name, other.name)) {
				return false;
			}
			return Arrays.deepEquals(this.data, other.data);
		}

		@Override
		public String toString()
		{
			if (data != null) {
				return name + Arrays.toString(data);
			} else {
				return name + "[]";
			}
		}
		//</editor-fold>
	}

	@Test
	public void simpleModuleAttributeInstanceArrayAssignmentToJava() throws Exception
	{
		DefaultCore core = new DefaultCore();
		DLType type = core.defineType(TestT.class, "T");
		//log.warn(DLHelper.describe(type));
		DLModule module = core.parse("simpleModuleAttributeInstanceArrayAssignmentToJava",
			"T t { data : T t1 { data : T t11 {} ; }, T t2 {}, T t3 {}; }"
		);

		TestT t = (TestT) module.getChild("t").orElseThrow().toJavaObject();

		TestT javaT11 = new TestT();
		javaT11.name = "t11";
		TestT javaT1 = new TestT();
		javaT1.name = "t1";
		javaT1.data = new TestT[]{javaT11};
		TestT javaT2 = new TestT();
		javaT2.name = "t2";
		TestT javaT3 = new TestT();
		javaT3.name = "t3";
		TestT javaT = new TestT();
		javaT.name = "t";
		javaT.data = new TestT[]{javaT1, javaT2, javaT3};

		assertEquals(t, javaT);
	}

	@Test
	public void simpleModuleAttributeInstanceListAssignment() throws Exception
	{
		DefaultCore core = new DefaultCore();
		DLModule module = core.parse("simpleModuleAttributeAssignment",
			"type T { Integer x; } List<T> x : T t1 {}, T t2 {}, T t3 {};"
		);
		
		DLType type = module.getDefinedType("T").orElseThrow();
		DLInstance t1 = (DLInstance)((List) module.get("x")).get(0);
		DLInstance t2 = (DLInstance)((List) module.get("x")).get(1);
		DLInstance t3 = (DLInstance)((List) module.get("x")).get(2);

		assertEquals(t1.getType(), type);
		assertEquals(t1.getName(), "t1");
		assertEquals(t2.getType(), type);
		assertEquals(t2.getName(), "t2");
		assertEquals(t3.getType(), type);
		assertEquals(t3.getName(), "t3");
	}

	@Test
	public void simpleModuleAttributeInstanceSetAssignment() throws Exception
	{
		DefaultCore core = new DefaultCore();
		DLModule module = core.parse("simpleModuleAttributeInstanceSetAssignment",
			"type T { Integer x; } Set<T> x : T t1 {}, T t2 {}, T t3 {};"
		);
		
		DLType type = module.getDefinedType("T").orElseThrow();
		DLInstance t1 = (DLInstance)((Set<DLInstance>) module.get("x")).stream().filter((t) -> {
			return t.getName().equals("t1");
		}).findFirst().orElseThrow();
		DLInstance t2 = (DLInstance)((Set<DLInstance>) module.get("x")).stream().filter((t) -> {
			return t.getName().equals("t2");
		}).findFirst().orElseThrow();
		DLInstance t3 = (DLInstance)((Set<DLInstance>) module.get("x")).stream().filter((t) -> {
			return t.getName().equals("t3");
		}).findFirst().orElseThrow();

		assertEquals(t1.getType(), type);
		assertEquals(t1.getName(), "t1");
		assertEquals(t2.getType(), type);
		assertEquals(t2.getName(), "t2");
		assertEquals(t3.getType(), type);
		assertEquals(t3.getName(), "t3");
	}

	@Test
	public void simpleModuleAttributeInstanceMapAssignment() throws Exception
	{
		DefaultCore core = new DefaultCore();
		DLModule module = core.parse("simpleModuleAttributeInstanceMapAssignment",
			"type T { Integer x; } "
			+ "Map<String, T> x : \"t1\", T t1 {}, \"t2\", T t2 {}, \"t3\", T t3 {};"
		);
		
		DLType type = module.getDefinedType("T").orElseThrow();
		DLInstance t1 = (DLInstance)((Map<String, DLInstance>) module.get("x")).get("t1");
		DLInstance t2 = (DLInstance)((Map<String, DLInstance>) module.get("x")).get("t2");
		DLInstance t3 = (DLInstance)((Map<String, DLInstance>) module.get("x")).get("t3");

		assertEquals(t1.getType(), type);
		assertEquals(t1.getName(), "t1");
		assertEquals(t2.getType(), type);
		assertEquals(t2.getName(), "t2");
		assertEquals(t3.getType(), type);
		assertEquals(t3.getName(), "t3");
	}
}
