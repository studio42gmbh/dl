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
package de.s42.dl.parameters;

import de.s42.base.strings.StringHelper;
import static de.s42.dl.validation.DefaultValidationCode.InvalidParameters;
import de.s42.dl.validation.ValidationResult;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 *
 * @author Benjamin Schiller
 */
public final class NamedParameter
{

	private final static Set<String> IGNORED_TO_STRING_PROPERTIES = Set.of("parameters", "validate");

	public final String name;
	public final Class type;
	public final Object defaultValue;
	public final Function<Object, Boolean> validate;
	public final boolean required;

	/**
	 * ATTENTION: parameters will be set by NamedParameters when constructing NamedParameters with this parameter
	 */
	protected NamedParameters parameters;

	/**
	 * ATTENTION: ordinal will be set by NamedParameters when constructing NamedParameters with this parameter
	 */
	protected int ordinal;

	public NamedParameter(String name, Class type, Object defaultValue, boolean required, int ordinal, Function<Object, Boolean> validate)
	{
		assert name != null;
		assert type != null;

		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		this.required = required;
		this.validate = validate;
		this.ordinal = ordinal;
		
		// Validate default value
		if (this.defaultValue != null) {
			if (!isValid(this.defaultValue)) {
				throw new RuntimeException("Default value of " + name + " is invalid");
			}
		}
	}

	public boolean isValid(Object value)
	{
		if (required && value == null) {
			return false;
		}
		
		if (validate != null) {
			return validate.apply(value);
		}

		return true;
	}
	
	public boolean validate(Object value, ValidationResult result)
	{
		assert result != null;
		
		if (required && value == null) {
			result.addError(InvalidParameters.toString(), "Parameter '" + name + "' is required", this);
			return result.isValid();
		}
		
		if (validate != null) {
			if (!validate.apply(value)) {
				result.addError(InvalidParameters.toString(), "Parameter '" + name + "' validation " + validate.getClass().getSimpleName() + " failed", this);
			}
		}

		return result.isValid();
	}

	public <ObjectType> ObjectType get(Object[] flatParameters)
	{
		return (ObjectType) flatParameters[ordinal];
	}

	public int getOrdinal()
	{
		return ordinal;
	}

	public NamedParameters getParameters()
	{
		return parameters;
	}

	@Override
	public String toString()
	{
		return StringHelper.toString(this, IGNORED_TO_STRING_PROPERTIES);
	}

	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 59 * hash + Objects.hashCode(this.name);
		hash = 59 * hash + Objects.hashCode(this.type);
		hash = 59 * hash + Objects.hashCode(this.defaultValue);
		hash = 59 * hash + (this.required ? 1 : 0);
		hash = 59 * hash + this.ordinal;
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
		final NamedParameter other = (NamedParameter) obj;
		if (this.required != other.required) {
			return false;
		}
		if (this.ordinal != other.ordinal) {
			return false;
		}
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		if (!Objects.equals(this.type, other.type)) {
			return false;
		}
		return Objects.equals(this.defaultValue, other.defaultValue);
	}
}
