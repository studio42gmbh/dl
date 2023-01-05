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
package de.s42.dl.parser.contracts;

import de.s42.dl.DLModule;
import de.s42.dl.parser.DLParser.AnnotationDefinitionExpressionContext;

/**
 *
 * @author Benjamin Schiller
 */
public abstract class BinaryContractOperator implements ContractExpression
{

	protected final DLModule module;
	protected final AnnotationDefinitionExpressionContext context;
	protected final ContractExpression first;
	protected final ContractExpression second;

	public BinaryContractOperator(ContractExpression first, ContractExpression second, AnnotationDefinitionExpressionContext context, DLModule module)
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
	public ContractExpression getFirst()
	{
		return first;
	}

	public ContractExpression getSecond()
	{
		return second;
	}

	public AnnotationDefinitionExpressionContext getContext()
	{
		return context;
	}

	public DLModule getModule()
	{
		return module;
	}
	//</editor-fold>
}
