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
package de.s42.dl.annotations.reflect;

import de.s42.base.validation.IsValidRegex;
import de.s42.dl.DLAttribute;
import de.s42.dl.annotations.*;
import de.s42.dl.DLType;
import de.s42.dl.exceptions.DLException;
import static de.s42.dl.validation.DefaultValidationCode.NotMatching;
import de.s42.dl.validation.ValidationResult;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

/**
 *
 * @author Benjamin Schiller
 */
public class AttributeNamesDLAnnotation extends AbstractDLConcept<AttributeNamesDLAnnotation>
{

	private final static Logger log = LogManager.getLogger(AttributeNamesDLAnnotation.class.getName());

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.TYPE})
	@DLAnnotationType(AttributeNamesDLAnnotation.class)
	@Repeatable(AttributeNamesDLContainer.class)
	public static @interface attributeNames
	{

		public String pattern();

		public String typePattern() default ".*";
	}

	// Allows to add multiple attributeNames to a java class
	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.TYPE})
	@DLAnnotationContainerType
	public static @interface AttributeNamesDLContainer
	{

		public attributeNames[] value();
	}

	@DLAnnotationParameter(ordinal = 0, required = true, validation = IsValidRegex.class)
	protected String pattern;

	@DLAnnotationParameter(ordinal = 1, required = false, defaultValue = ".*", validation = IsValidRegex.class)
	protected String typePattern;

	private Pattern patternPattern;

	private Pattern typePatternPattern;

	@Override
	public boolean validate(DLType type, ValidationResult result)
	{
		assert type != null;
		assert result != null;

		boolean valid = true;

		for (DLAttribute attribute : type.getAttributes()) {

			// Just validate attributes whose type matches the typePattern
			if (!typePatternPattern.matcher(attribute.getType().getCanonicalName()).matches()) {
				continue;
			}

			// If the attribute name does not match pattern -> validation Error
			if (!patternPattern.matcher(attribute.getName()).matches()) {
				result.addError(NotMatching.toString(), "Attribute name '" + attribute.getName() + "' in type '" + type + "' does not match pattern '" + pattern + "'");
				valid = false;
			}
		}

		return valid;
	}

	@Override
	public synchronized void bindToType(DLType type) throws DLException
	{
		assert type != null;

		type.addValidator(this);

		// Precompile pattern - after binding the pattern and typepattern may not get changed anymore for consistency
		patternPattern = Pattern.compile(pattern);
		typePatternPattern = Pattern.compile(typePattern);
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public String getPattern()
	{
		return pattern;
	}

	public void setPattern(String pattern)
	{
		assert patternPattern == null;

		this.pattern = pattern;
	}

	public String getTypePattern()
	{
		return typePattern;
	}

	public void setTypePattern(String typePattern)
	{
		assert typePatternPattern == null;

		this.typePattern = typePattern;
	}
	//</editor-fold>
}
