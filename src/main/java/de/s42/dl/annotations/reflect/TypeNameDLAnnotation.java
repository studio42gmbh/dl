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
import de.s42.dl.annotations.*;
import de.s42.dl.DLType;
import static de.s42.dl.validation.DefaultValidationCode.NotMatching;
import de.s42.dl.validation.ValidationResult;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

/**
 *
 * @author Benjamin Schiller
 */
public class TypeNameDLAnnotation extends AbstractDLContract<TypeNameDLAnnotation>
{

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.TYPE})
	@DLAnnotationType(TypeNameDLAnnotation.class)
	public static @interface typeName
	{

		public String pattern();

		public boolean ignoreAbstract() default false;
	}

	@DLAnnotationParameter(ordinal = 0, required = true, validation = IsValidRegex.class)
	protected String pattern;

	@DLAnnotationParameter(ordinal = 1, required = false, defaultValue = "false")
	protected boolean ignoreAbstract = false;

	private Pattern patternPattern;

	@Override
	public boolean validate(DLType type, ValidationResult result)
	{
		assert type != null;
		assert result != null;

		if (isIgnoreAbstract() && type.isAbstract()) {
			return true;
		}

		preparePatterns();

		if (!patternPattern.matcher(type.getName()).matches()) {
			result.addError(NotMatching.toString(), "Type name '" + type.getName() + "' does not match pattern '" + pattern + "'");
			return false;
		}

		return true;
	}

	@Override
	public boolean canValidateType()
	{
		return true;
	}

	protected synchronized void preparePatterns()
	{
		if (patternPattern != null) {
			return;
		}

		// Precompile pattern - after binding the pattern and typepattern may not get changed anymore for consistency
		patternPattern = Pattern.compile(pattern);
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

	public boolean isIgnoreAbstract()
	{
		return ignoreAbstract;
	}

	public void setIgnoreAbstract(boolean ignoreAbstract)
	{
		this.ignoreAbstract = ignoreAbstract;
	}
	//</editor-fold>
}
