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
package de.s42.dl.annotations;

import de.s42.dl.DLCore;
import de.s42.dl.DLType;
import de.s42.dl.annotations.ContainDLAnnotation.contain;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.java.DLContainer;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 *
 * @author Benjamin Schiller
 */
public class ContainDLAnnotationTest
{

	private final static Logger log = LogManager.getLogger(ContainDLAnnotationTest.class.getName());

	@contain(contain = "Object", min = 1, max = 3)
	public static class ContainerClass implements DLContainer
	{

		@Override
		public void addChild(String name, Object child)
		{
			// do nothing
		}
	}

	protected void assertAnnotation(ContainDLAnnotation annotation, String contained, int min, int max)
	{
		Assert.assertEquals(annotation.getContain(), contained, "contain");
		Assert.assertEquals(annotation.getMin(), min, "min");
		Assert.assertEquals(annotation.getMax(), max, "max");
	}

	@Test
	public void validContainJavaType() throws DLException
	{
		DefaultCore core = new DefaultCore();
		DLType type = core.defineType(ContainerClass.class);
		//log.debug("ContainerClass DLType:\n", DLHelper.describe(type));

		Assert.assertTrue(type.hasAnnotation(ContainDLAnnotation.class), "@contain should be mapped for type ContainerClass");

		ContainDLAnnotation annotation = (ContainDLAnnotation) type.getAnnotation(ContainDLAnnotation.class).orElseThrow();

		assertAnnotation(annotation, "Object", 1, 3);
	}

	@Test
	public void validContainNamedParameters() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("validContainNamedParameters",
			"type T extends Object;"
			+ "type Container @contain(contain : T, min : 1, max : 7) contains Object;"
			+ "Container c { T t1; }");

		DLType Container = core.getType("Container").orElseThrow();
		Assert.assertTrue(Container.hasAnnotation(ContainDLAnnotation.class), "@contain should be mapped for type Container");

		ContainDLAnnotation annotation = (ContainDLAnnotation) Container.getAnnotation(ContainDLAnnotation.class).orElseThrow();

		assertAnnotation(annotation, "T", 1, 7);
	}

	@Test
	public void validContainNamedPartialParameters() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("validContainNamedPartialParameters",
			"type T extends Object;"
			+ "type Container @contain(contain : T) contains Object;"
			+ "Container c { T t1; }");

		DLType Container = core.getType("Container").orElseThrow();
		Assert.assertTrue(Container.hasAnnotation(ContainDLAnnotation.class), "@contain should be mapped for type Container");

		ContainDLAnnotation annotation = (ContainDLAnnotation) Container.getAnnotation(ContainDLAnnotation.class).orElseThrow();

		assertAnnotation(annotation, "T", 0, Integer.MAX_VALUE);
	}

	@Test
	public void validContainUnnamedParameters() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("validContainUnnamedParameters",
			"type T extends Object;"
			+ "type Container @contain(T, 1, 5) contains Object;"
			+ "Container c { T t1; }");

		DLType Container = core.getType("Container").orElseThrow();
		Assert.assertTrue(Container.hasAnnotation(ContainDLAnnotation.class), "@contain should be mapped for type Container");

		ContainDLAnnotation annotation = (ContainDLAnnotation) Container.getAnnotation(ContainDLAnnotation.class).orElseThrow();

		assertAnnotation(annotation, "T", 1, 5);
	}

	@Test(expectedExceptions = InvalidInstance.class)
	public void invalidContainTooFew() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidContainTooFew",
			"type T extends Object;"
			+ "type Container @contain(contain : T, min : 1, max : 7) contains Object;"
			+ "Container c { }");
	}

	@Test(expectedExceptions = InvalidInstance.class)
	public void invalidContainTooMany() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidContainTooFew",
			"type T extends Object;"
			+ "type Container @contain(contain : T, min : 0, max : 2) contains Object;"
			+ "Container c { T t1; T t2; T t3; }");
	}

	@Test(expectedExceptions = InvalidAnnotation.class)
	public void invalidEmptyContainBrackets() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidEmptyContainBrackets", "type T @contain();");
	}

	@Test(expectedExceptions = InvalidAnnotation.class)
	public void invalidEmptyContain() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidEmptyContain", "type T @contain;");
	}
	
	@Test(expectedExceptions = InvalidAnnotation.class)
	public void invalidContaToAttribute() throws DLException
	{
		DLCore core = new DefaultCore();
		core.parse("invalidContaToAttribute", "type T { int x @contain(Other); }");
	}
}
