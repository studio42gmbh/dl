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
package de.s42.dl.annotations.strings;

import de.s42.base.validation.IsValidRegex;
import de.s42.dl.annotations.AbstractValueDLContract;
import de.s42.dl.annotations.DLAnnotated;
import de.s42.dl.annotations.DLAnnotationParameter;
import de.s42.dl.annotations.DLAnnotationType;
import de.s42.dl.exceptions.InvalidAnnotation;
import static de.s42.dl.validation.DefaultValidationCode.InvalidValueType;
import static de.s42.dl.validation.DefaultValidationCode.NotMatching;
import de.s42.dl.validation.ValidationResult;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 *
 * @author Benjamin Schiller
 */
public class RegexDLAnnotation extends AbstractValueDLContract<RegexDLAnnotation>
{

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.TYPE})
	@DLAnnotationType(RegexDLAnnotation.class)
	public static @interface regex
	{

		public String pattern();
	}

	@DLAnnotationParameter(ordinal = 0, required = true, validation = IsValidRegex.class)
	protected String pattern;

	private Pattern patternPattern;

	@Override
	protected void validateThis() throws InvalidAnnotation
	{
		if (pattern == null) {
			throw new InvalidAnnotation("Pattern is null");
		}
		
		// Precompile pattern - after binding the pattern may not get changed anymore for consistency
		try {
			patternPattern = Pattern.compile(pattern);
		} catch (PatternSyntaxException ex) {
			throw new InvalidAnnotation("Pattern syntax  '" + pattern + "' is invalid - " + ex.getMessage(), ex);
		}
	}

	@Override
	protected boolean validateValue(Object value, ValidationResult result, DLAnnotated source)
	{
		assert source != null;
		assert result != null;

		// Allow to have null values
		if (value == null) {
			return result.isValid();
		}
		
		// Make sure its a String
		if (!(value instanceof String)) {
			result.addError(InvalidValueType.toString(), "Attribute has to be of type String in @" + getName(), source);
			return result.isValid();
		}

		if (!patternPattern.matcher((String) value).matches()) {
			result.addError(NotMatching.toString(),
				"Value '" + value + "' does not match pattern '" + pattern + "' in @" + getName()
			);
			return result.isValid();
		}

		return result.isValid();
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
	//</editor-fold>
}
