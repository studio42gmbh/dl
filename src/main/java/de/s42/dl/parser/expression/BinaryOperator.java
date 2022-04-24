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
package de.s42.dl.parser.expression;

import de.s42.dl.DLModule;
import de.s42.dl.parser.DLParser.ExpressionContext;

/**
 *
 * @author Benjamin Schiller
 */
public abstract class BinaryOperator implements Expression
{

	protected final DLModule module;
	protected final ExpressionContext context;
	protected final Expression first;
	protected final Expression second;

	public BinaryOperator(Expression first, Expression second, ExpressionContext context, DLModule module)
	{
		assert first != null;
		assert second != null;
		assert context != null;
		assert module != null;

		this.first = first;
		this.second = second;
		this.context = context;
		this.module = module;
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public Expression getFirst()
	{
		return first;
	}

	public Expression getSecond()
	{
		return second;
	}

	public ExpressionContext getContext()
	{
		return context;
	}

	public DLModule getModule()
	{
		return module;
	}
	//</editor-fold>
}
