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
package de.s42.dl.parser;

import de.s42.dl.DLModule;
import de.s42.dl.exceptions.ParserException;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 *
 * @author Benjamin Schiller
 */
public class DLHrfParsingException extends ParserException
{

	public DLHrfParsingException()
	{
	}

	public DLHrfParsingException(String msg)
	{
		super(msg);
	}

	public DLHrfParsingException(Throwable cause)
	{
		super(cause);
	}

	public DLHrfParsingException(String msg, Throwable cause)
	{
		super(msg, cause);
	}

	public DLHrfParsingException(String msg, int line, int position)
	{
		super(msg, line, position);
	}

	public DLHrfParsingException(String msg, int line, int position, Exception cause)
	{
		super(msg, line, position, cause);
	}

	public DLHrfParsingException(String msg, ParserRuleContext context)
	{
		super(msg, context.getStart().getLine(), context.getStart().getCharPositionInLine() + 1);
	}

	public DLHrfParsingException(String msg, Exception ex, ParserRuleContext context)
	{
		super(msg, context.getStart().getLine(), context.getStart().getCharPositionInLine() + 1, ex);
	}

	public DLHrfParsingException(String msg, DLModule module, ParserRuleContext context)
	{
		this(DLHrfParsingErrorHandler.createErrorMessage(module, msg, context), context);
	}
	
	public DLHrfParsingException(String msg, DLModule module, ParserRuleContext context, Exception ex)		
	{
		this(DLHrfParsingErrorHandler.createErrorMessage(module, msg, ex, context), ex, context);
	}
}
