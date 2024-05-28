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
package de.s42.dl.validation;

import de.s42.dl.types.DLContainer;
import static de.s42.dl.validation.ValidationElementType.Error;
import static de.s42.dl.validation.ValidationElementType.Info;
import static de.s42.dl.validation.ValidationElementType.Warning;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Benjamin Schiller
 */
public class ValidationResult implements DLContainer<ValidationElement>
{

	private final static Logger log = LogManager.getLogger(ValidationResult.class.getName());

	protected final List<ValidationElement> elements;

	protected boolean errors = false;
	protected boolean warnings = false;
	protected boolean infos = false;

	public ValidationResult()
	{
		elements = new ArrayList<>();
	}

	protected ValidationResult(List<ValidationElement> elements)
	{
		this.elements = elements;
	}

	public String toMessage()
	{
		if (isValid()) {
			return "Valid";
		}

		StringBuilder builder = new StringBuilder();

		builder
			.append(getErrorCount())
			.append(" error(s) - ")
			.append(getWarningCount())
			.append(" warning(s) - ")
			.append(getInfoCount())
			.append(" info(s) - ");

		for (ValidationElement error : getErrors()) {
			builder
				.append("[Error:")
				.append(error.getCode())
				.append("] : ")
				.append(error.getDescription())
				.append(" ");
		}

		for (ValidationElement warning : getWarnings()) {
			builder
				.append("[Warning:")
				.append(warning.getCode())
				.append("] : ")
				.append(warning.getDescription())
				.append(" ");
		}

		for (ValidationElement info : getInfos()) {
			builder
				.append("[Info:")
				.append(info.getCode())
				.append("] : ")
				.append(info.getDescription())
				.append(" ");
		}

		return builder.toString();
	}

	@Override
	public void addChild(String name, ValidationElement child)
	{
		assert child != null;

		addElement(child);
	}
	
	public ValidationElement addElement(ValidationElement element)
	{
		assert element != null;

		elements.add(element);

		if (element.type.equals(Error)) {
			errors = true;
		} else if (element.type.equals(Warning)) {
			warnings = true;
		} else if (element.type.equals(Info)) {
			infos = true;
		}

		return element;
	}

	public ValidationElement addWarning(String code, String description)
	{
		return addWarning(code, description, null);
	}

	public ValidationElement addWarning(String code, String description, Object source)
	{
		assert code != null;
		assert description != null;

		return addElement(new ValidationElement(code, description, source, ValidationElementType.Warning));
	}

	public int getWarningCount()
	{
		return (int) elements.stream().filter((element) -> {
			return element.getType().equals(Warning);
		}).count();
	}

	public List<ValidationElement> getWarnings()
	{
		return elements.stream().filter((element) -> {
			return element.getType().equals(Warning);
		}).toList();
	}

	public ValidationElement addError(String code, String description)
	{
		return addError(code, description, null);
	}

	public ValidationElement addError(String code, String description, Object source)
	{
		assert code != null;
		assert description != null;

		return addElement(new ValidationElement(code, description, source, ValidationElementType.Error));
	}

	public int getErrorCount()
	{
		return (int) elements.stream().filter((element) -> {
			return element.getType().equals(Error);
		}).count();
	}

	public List<ValidationElement> getErrors()
	{
		return elements.stream().filter((element) -> {
			return element.getType().equals(Error);
		}).toList();
	}

	public ValidationElement addInfo(String code, String description)
	{
		return addError(code, description, null);
	}

	public ValidationElement addInfo(String code, String description, Object source)
	{
		assert code != null;
		assert description != null;

		return addElement(new ValidationElement(code, description, source, ValidationElementType.Info));
	}

	public int getInfoCount()
	{
		return (int) elements.stream().filter((element) -> {
			return element.getType().equals(Info);
		}).count();
	}

	public List<ValidationElement> getInfos()
	{
		return elements.stream().filter((element) -> {
			return element.getType().equals(Info);
		}).toList();
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	@Override
	public List<ValidationElement> getChildren()
	{
		return elements;
	}

	public boolean isValid()
	{
		return !hasErrors() && !hasWarnings();
	}

	public boolean hasErrors()
	{
		return errors;
	}

	public boolean hasWarnings()
	{
		return warnings;
	}

	public boolean hasInfos()
	{
		return infos;
	}
	//</editor-fold>
}
