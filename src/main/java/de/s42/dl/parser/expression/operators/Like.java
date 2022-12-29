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
package de.s42.dl.parser.expression.operators;

import de.s42.dl.DLModule;
import de.s42.dl.parser.DLHrfParsingException;
import de.s42.dl.parser.DLParser.ExpressionContext;
import de.s42.dl.parser.expression.Expression;
import de.s42.dl.parser.expression.BinaryOperator;
import java.util.regex.PatternSyntaxException;

/**
 *
 * @author Benjamin Schiller
 */
public class Like extends BinaryOperator
{

	public Like(Expression first, Expression second, ExpressionContext context, DLModule module)
	{
		super(first, second, context, module);
	}

	@Override
	public Boolean evaluate()
	{
		Object firstEval = first.evaluate();
		Object secondEval = second.evaluate();

		// Make sure both operands are strings
		if (firstEval instanceof String && secondEval instanceof String) {
			try {
				return ((String) firstEval).matches((String) secondEval);
			}
			catch (PatternSyntaxException ex) {
				throw new DLHrfParsingException(
					"Second operand in '" + context.getText() + "' has to have a valid regex pattern",
					module,
					context,
					ex
				);
			}
		}

		throw new DLHrfParsingException(
			"Types invalid in '" + context.getText() + "' both have to be String",
			module,
			context
		);
	}
}
