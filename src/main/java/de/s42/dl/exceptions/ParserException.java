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
package de.s42.dl.exceptions;

/**
 *
 * @author Benjamin Schiller
 */
public class ParserException extends RuntimeException
{
	protected final int line;
	protected final int position;
	
	public ParserException()
	{
		line = 0;
		position = 0;
	}

	public ParserException(String msg)
	{
		super(msg);
		line = 0;
		position = 0;
	}

	public ParserException(Throwable cause)
	{
		super(cause);
		line = 0;
		position = 0;
	}

	public ParserException(String msg, Throwable cause)
	{
		super(msg, cause);
		line = 0;
		position = 0;
	}
	
	public ParserException(String msg, int line, int position)
	{
		super(msg);
		this.line = line;
		this.position = position;
	}

	public ParserException(String msg, int line, int position, Exception cause)
	{
		super(msg, cause);
		this.line = line;
		this.position = position;
	}
	
	public int getLine()
	{
		return line;
	}

	public int getPosition()
	{
		return position;
	}
}
