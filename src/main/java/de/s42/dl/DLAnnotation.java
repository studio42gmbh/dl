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

import de.s42.dl.annotations.DLAnnotated;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidAnnotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 *
 * @author Benjamin Schiller
 */
public interface DLAnnotation extends DLEntity
{

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.FIELD, ElementType.TYPE})
	@Repeatable(AnnotationDLContainer.class)
	public static @interface AnnotationDL
	{

		public String value() default "";

		public String[] parameters() default {};
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.FIELD, ElementType.TYPE})
	public static @interface AnnotationDLContainer
	{

		public AnnotationDL[] value();
	}

	public DLAnnotated getContainer();
	
	public boolean hasParameters();

	public Object[] getFlatParameters();

	public Map<String, Object> getNamedParameters();

	default public void bindToType(DLType type) throws DLException
	{
		throw new InvalidAnnotation(getClass().getName() + " can not be bound to types");
	}

	default public void bindToAttribute(DLAttribute attribute) throws DLException
	{
		throw new InvalidAnnotation(getClass().getName() + " can not be bound to attributes");
	}

	default public void bindToInstance(DLInstance instance) throws DLException
	{
		throw new InvalidAnnotation(getClass().getName() + " can not be bound to instances");
	}
}
