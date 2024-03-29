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

import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidAnnotation;
import static de.s42.dl.validation.DefaultValidationCode.InvalidParameters;
import de.s42.dl.validation.ValidationResult;
import java.util.Map;

/**
 *
 * @author Benjamin Schiller
 * @param <DLAnnotationType>
 */
public interface DLAnnotationFactory<DLAnnotationType extends DLAnnotation>
{

	public Class<DLAnnotationType> getAnnotationType();

	public DLAnnotationType createAnnotation(String name, Object[] flatParameters) throws DLException;

	default public DLAnnotationType createAnnotation(String name) throws DLException
	{
		assert name != null;

		return createAnnotation(name, new Object[]{});
	}

	default public DLAnnotationType createAnnotation(String name, Map<String, Object> namedParameters) throws DLException
	{
		assert name != null;

		return createAnnotation(name, toFlatParameters(namedParameters));
	}

	default public boolean isValidNamedParameters(Map<String, Object> namedParameters)
	{
		return false;
	}

	/**
	 * Shall provide a qualified validation reporting to help the user (used in parsing).
	 *
	 * @param namedParameters
	 * @param result
	 *
	 * @return
	 */
	default public boolean validateNamedParameters(Map<String, Object> namedParameters, ValidationResult result)
	{
		assert result != null;

		if (!isValidNamedParameters(namedParameters)) {
			result.addError(InvalidParameters.toString(), "Named parameters are invalid", this);
		}

		return result.isValid();
	}

	default public boolean isValidNamedParameter(String name, Object value)
	{
		return false;
	}

	default public boolean isValidFlatParameters(Object[] flatParameters)
	{
		return false;
	}

	/**
	 * Shall provide a qualified validation reporting to help the user (used in parsing).
	 *
	 * @param flatParameters
	 * @param result
	 *
	 * @return
	 */
	default public boolean validateFlatParameters(Object[] flatParameters, ValidationResult result)
	{
		assert result != null;

		if (!isValidFlatParameters(flatParameters)) {
			result.addError(InvalidParameters.toString(), "Flat parameters are invalid", this);
		}

		return result.isValid();
	}

	default public Object[] toFlatParameters(Map<String, Object> namedParameters) throws DLException
	{
		throw new InvalidAnnotation("Can not flatten parameters");
	}
}
