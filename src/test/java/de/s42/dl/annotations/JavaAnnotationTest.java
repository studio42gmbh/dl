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

import de.s42.base.validation.IsBoolean;
import de.s42.dl.DLAnnotated.DLMappedAnnotation;
import de.s42.dl.DLCore;
import de.s42.dl.DLInstance;
import de.s42.dl.DLType;
import de.s42.dl.annotations.JavaAnnotationTest.EditorAnnotation.editor;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.util.DLHelper;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 *
 * @author Benjamin Schiller
 */
public class JavaAnnotationTest
{

	private final static Logger log = LogManager.getLogger(JavaAnnotationTest.class.getName());

	/**
	 * My own annotation class allowing to tag classes in java type safe and with explicit parameters
	 */
	public static class EditorAnnotation extends AbstractDLAnnotation
	{

		@Retention(RetentionPolicy.RUNTIME)
		@Target(value = {ElementType.TYPE})
		@DLAnnotationType(EditorAnnotation.class)
		public static @interface editor
		{

			@DLAnnotationParameter(ordinal = 0, validation = IsBoolean.class)
			public boolean visible() default true;

			@DLAnnotationParameter(ordinal = 1, validation = IsBoolean.class)
			public boolean editable() default true;
		}

		public EditorAnnotation()
		{
			super(editor.class);
		}

		public EditorAnnotation(String name)
		{
			super(name, editor.class);
		}

		@Override
		public void bindToType(DLCore core, DLType type, Object... parameters) throws DLException
		{
			if (!isValidFlatParameters(parameters)) {
				throw new InvalidAnnotation("flat parameters are not valid");
			}
		}

		public boolean isVisible(DLMappedAnnotation mappedAnnotation) throws InvalidAnnotation
		{
			return getNamedParameter("visible", mappedAnnotation.getParameters());
		}

		public boolean isEditable(DLMappedAnnotation mappedAnnotation) throws InvalidAnnotation
		{
			return getNamedParameter("editable", mappedAnnotation.getParameters());
		}
	}

	// Intuitive and typed dl annotations in java! :)
	@editor(editable = false)
	public static class MyAnnotatedClass
	{
	}

	@Test
	public void validCustomAnnotationInJava() throws Exception
	{
		// Find annotations on classes
		List<DLMappedAnnotation> mappedAnnotations = DLAnnotationHelper.getDLAnnotations(MyAnnotatedClass.class);

		Assert.assertEquals(mappedAnnotations.size(), 1);
		Assert.assertTrue(mappedAnnotations.get(0).getAnnotation() instanceof EditorAnnotation);

		EditorAnnotation annotation = mappedAnnotations.get(0).getAnnotation();

		Assert.assertEquals(annotation.getParameters().indexOf("visible"), 0);
		Assert.assertEquals(annotation.getParameters().indexOf("editable"), 1);

		Assert.assertEquals(mappedAnnotations.get(0).getParameters()[0], true);
		Assert.assertEquals(annotation.isVisible(mappedAnnotations.get(0)), true);
		Assert.assertEquals(annotation.isEditable(mappedAnnotations.get(0)), false);

		DefaultCore core = new DefaultCore();
		core.defineAnnotation(new EditorAnnotation());
		core.defineType(MyAnnotatedClass.class);

		MyAnnotatedClass javaInstance = new MyAnnotatedClass();

		DLInstance instance = core.convertFromJavaObject(javaInstance);

		log.warn("DESC\n", DLHelper.describe(instance.getType()));

		// Load special annotations in BaseDL when convertFromJavaObject
		Assert.assertTrue(instance.getType().getAnnotation(EditorAnnotation.class).isPresent());

		DLMappedAnnotation mappedAnnotation = instance.getType().getAnnotation(EditorAnnotation.class).orElseThrow();
		EditorAnnotation annotation2 = mappedAnnotation.getAnnotation();

		Assert.assertEquals(annotation2.isVisible(mappedAnnotation), true);
		Assert.assertEquals(annotation2.isEditable(mappedAnnotation), false);
	}

	@Test
	public void validCustomNamedAndUnnamedParameterizedAnnotation() throws Exception
	{
		DLCore core = new DefaultCore();
		core.defineAnnotation(new EditorAnnotation());
		core.parse("validCustomNamedAndUnnamedParameterizedAnnotation",
			"type T @editor(editable : false, visible : true) {} T t {}");
		core.parse("validCustomNamedAndUnnamedParameterizedAnnotation2",
			"type T2 @editor(visible : false) {} T2 t2 {}");
		core.parse("validCustomNamedAndUnnamedParameterizedAnnotation3",
			"type T3 @editor(editable : true) {} T3 t3 {}");
		core.parse("validCustomNamedAndUnnamedParameterizedAnnotation4",
			"type T4 @editor(true, false) {} T4 t4 {}");
	}

	@Test(expectedExceptions = InvalidAnnotation.class)
	public void invalidCustomAnnotationInvalidNamedParameter() throws Exception
	{
		DLCore core = new DefaultCore();
		core.defineAnnotation(new EditorAnnotation());
		core.parse("invalidCustomAnnotationInvalidNamedParameter",
			"type T @editor(wrong : true) {} T t {}");

	}

}
