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
import de.s42.dl.validation.DLAttributeValidator;
import de.s42.dl.validation.DLValidatable;
import de.s42.dl.validation.ValidationResult;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 *
 * @author Benjamin Schiller
 */
public interface DLAttribute extends DLEntity, DLAnnotated, DLValidatable
{

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.FIELD, ElementType.METHOD})
	public static @interface AttributeDL
	{

		public String defaultValue() default "";

		public boolean required() default false;

		public boolean ignore() default false;
	}

	public DLType getContainer();

	default public DLCore getCore()
	{
		return getContainer().getCore();
	}

	public Object getDefaultValue();

	public DLType getType();

	// VALIDATOR
	public boolean addValidator(DLAttributeValidator validator);

	public List<DLAttributeValidator> getValidators();

	public boolean validateValue(Object value, ValidationResult result);

	// FLAGS
	public boolean isReadable();

	public boolean isWritable();

	/**
	 * This method shall return true if the data represented by this attribute is equal (same type, same annnotations,
	 * ... but not same container)
	 *
	 * @param other
	 *
	 * @return
	 */
	public boolean equalDataType(DLAttribute other);

	public boolean equalOrMoreSpecificDataType(DLAttribute other);

}
