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

import de.s42.dl.annotations.attributes.RequiredDLAnnotation.required;
import de.s42.log.LogManager;
import de.s42.log.Logger;

/**
 *
 * @author Benjamin Schiller
 */
public class ValidationElement
{

	private final static Logger log = LogManager.getLogger(ValidationElement.class.getName());

	@required
	protected String code;
	protected String description;
	protected Object source;
	@required
	protected ValidationElementType type;

	public ValidationElement()
	{

	}

	public ValidationElement(String code, String description, Object source, ValidationElementType type)
	{
		assert code != null;
		assert type != null;

		this.code = code;
		this.description = description;
		this.source = source;
		this.type = type;
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Object getSource()
	{
		return source;
	}

	public void setSource(Object source)
	{
		this.source = source;
	}

	public ValidationElementType getType()
	{
		return type;
	}

	public void setType(ValidationElementType type)
	{
		this.type = type;
	}
	//</editor-fold>
}
