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
package de.s42.dl.parser.contracts.operators;

import de.s42.dl.DLModule;
import de.s42.dl.parser.DLHrfParsingException;
import de.s42.dl.parser.DLParser;
import de.s42.dl.parser.contracts.BinaryContractOperator;
import de.s42.dl.parser.contracts.ContractExpression;
import de.s42.dl.parser.contracts.DLContractFactory;

/**
 *
 * @author Benjamin Schiller
 */
public class And extends BinaryContractOperator
{

	public And(ContractExpression first, ContractExpression second, DLParser.AnnotationDefinitionExpressionContext context, DLModule module)
	{
		super(first, second, context, module);
	}

	@Override
	public DLContractFactory evaluate() throws DLHrfParsingException
	{
		DLContractFactory firstEval = first.evaluate();
		DLContractFactory secondEval = second.evaluate();
		
		return new AndAnnotationFactory("and" + firstEval.getName() + secondEval.getName(), firstEval, secondEval, new Object[]{});		
	}
}
