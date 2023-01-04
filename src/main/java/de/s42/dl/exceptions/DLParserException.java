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
public class DLParserException extends DLException
{

	protected int startLine;
	protected int startPosition;
	protected int startOffset;
	protected int endLine;
	protected int endPosition;
	protected int endOffset;

	public DLParserException()
	{
		startLine = 0;
		startPosition = 0;
		startOffset = 0;
		endLine = 0;
		endPosition = 0;
		endOffset = 0;
	}

	public DLParserException(String msg)
	{
		super(msg);

		startLine = 0;
		startPosition = 0;
		startOffset = 0;
		endLine = 0;
		endPosition = 0;
		endOffset = 0;
	}

	public DLParserException(Throwable cause)
	{
		super(cause);

		startLine = 0;
		startPosition = 0;
		startOffset = 0;
		endLine = 0;
		endPosition = 0;
		endOffset = 0;
	}

	public DLParserException(String msg, Throwable cause)
	{
		super(msg, cause);

		startLine = 0;
		startPosition = 0;
		startOffset = 0;
		endLine = 0;
		endPosition = 0;
		endOffset = 0;
	}

	public DLParserException(String msg, int line, int position, int offset)
	{
		super(msg);

		assert line >= 0;
		assert position >= 0;
		assert offset >= 0;

		startLine = line;
		startPosition = position;
		startOffset = offset;
		endLine = line;
		endPosition = position;
		endOffset = offset;
	}

	public DLParserException(String msg, int line, int position, int offset, Exception cause)
	{
		super(msg, cause);

		assert line >= 0;
		assert position >= 0;
		assert offset >= 0;

		startLine = line;
		startPosition = position;
		startOffset = offset;
		endLine = line;
		endPosition = position;
		endOffset = offset;
	}

	public DLParserException(String msg, int startLine, int startPosition, int startOffset, int endLine, int endPosition, int endOffset)
	{
		super(msg);

		assert startLine >= 0;
		assert startPosition >= 0;
		assert startOffset >= 0;
		assert endLine >= 0;
		assert endPosition >= 0;
		assert endOffset >= 0;
		assert endLine >= startLine;
		assert endPosition >= startPosition;
		assert endOffset >= startOffset;

		this.startLine = startLine;
		this.startPosition = startPosition;
		this.startOffset = startOffset;
		this.endLine = endLine;
		this.endPosition = endPosition;
		this.endOffset = endOffset;
	}

	public DLParserException(String msg, int startLine, int startPosition, int startOffset, int endLine, int endPosition, int endOffset, Exception cause)
	{
		super(msg, cause);

		assert startLine >= 0;
		assert startPosition >= 0;
		assert startOffset >= 0;
		assert endLine >= 0;
		assert endPosition >= 0;
		assert endOffset >= 0;
		assert endLine >= startLine;
		assert endPosition >= startPosition;
		assert endOffset >= startOffset;

		this.startLine = startLine;
		this.startPosition = startPosition;
		this.startOffset = startOffset;
		this.endLine = endLine;
		this.endPosition = endPosition;
		this.endOffset = endOffset;
	}

	public int getStartLine()
	{
		return startLine;
	}

	public int getStartPosition()
	{
		return startPosition;
	}

	public int getStartOffset()
	{
		return startOffset;
	}

	public int getEndLine()
	{
		return endLine;
	}

	public int getEndPosition()
	{
		return endPosition;
	}

	public int getEndOffset()
	{
		return endOffset;
	}

	@Deprecated
	public void setStartLine(int startLine)
	{
		this.startLine = startLine;
	}

	@Deprecated
	public void setStartPosition(int startPosition)
	{
		this.startPosition = startPosition;
	}

	@Deprecated
	public void setStartOffset(int startOffset)
	{
		this.startOffset = startOffset;
	}

	@Deprecated
	public void setEndLine(int endLine)
	{
		this.endLine = endLine;
	}

	@Deprecated
	public void setEndPosition(int endPosition)
	{
		this.endPosition = endPosition;
	}

	@Deprecated
	public void setEndOffset(int endOffset)
	{
		this.endOffset = endOffset;
	}
}
