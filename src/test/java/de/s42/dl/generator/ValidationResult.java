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
package de.s42.dl.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Benjamin Schiller
 */
public class ValidationResult
{
	public final static class InvalidElement
	{
		protected String rule;
		protected String attribute;
		protected String message; 
		
		public InvalidElement()
		{
			
		}
		
		public InvalidElement(String rule, String attribute, String message)
		{
			this.rule = rule;
			this.attribute = attribute;
			this.message = message;
		}

		public String getRule()
		{
			return rule;
		}

		public void setRule(String rule)
		{
			this.rule = rule;
		}

		public String getAttribute()
		{
			return attribute;
		}

		public void setAttribute(String attribute)
		{
			this.attribute = attribute;
		}

		public String getMessage()
		{
			return message;
		}

		public void setMessage(String message)
		{
			this.message = message;
		}
	}
	
	protected final List<InvalidElement> invalidElements = new ArrayList<>();

	public List<InvalidElement> getInvalidElements()
	{
		return Collections.unmodifiableList(invalidElements);
	}
	
	public InvalidElement addInvalid(String rule, String attribute, String message)
	{
		InvalidElement element = new InvalidElement(rule, attribute, message);
		
		invalidElements.add(element);
		
		return element;
	}
}
