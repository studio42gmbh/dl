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

import de.s42.dl.parser.expression.operators.Xor;
import de.s42.dl.parser.expression.operators.Or;
import de.s42.dl.parser.expression.operators.Not;
import de.s42.dl.parser.expression.operators.Divide;
import de.s42.dl.parser.expression.operators.Subtract;
import de.s42.dl.parser.expression.operators.Multiply;
import de.s42.dl.parser.expression.operators.Equals;
import de.s42.dl.parser.expression.operators.Atom;
import de.s42.dl.parser.expression.operators.And;
import de.s42.dl.parser.expression.operators.Add;
import de.s42.dl.DLCore;
import de.s42.dl.DLModule;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidValue;
import de.s42.dl.instances.SimpleTypeDLInstance;
import de.s42.dl.parser.DLHrfParsing;
import de.s42.dl.parser.DLParser.AtomContext;
import de.s42.dl.parser.DLParser.ExpressionContext;
import de.s42.dl.parser.expression.operators.Pow;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.util.Optional;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Handles expressions in DL. This is just a reference implementation. In case expressions will be part of the DL spec
 * this has to be optimized.
 * See https://github.com/studio42gmbh/dl/issues/20
 *
 * @author Benjamin Schiller
 */
public final class DLHrfExpressionParser
{

	private final static Logger log = LogManager.getLogger(DLHrfExpressionParser.class.getName());

	private DLHrfExpressionParser()
	{
		// never instantiated
	}

	public static Object resolveExpression(DLCore core, DLModule module, ExpressionContext ctx) throws DLException
	{
		Expression expr = buildExpression(core, module, ctx);

		Object value = expr.evaluate();

		//log.debug("EXPRESSION", ctx.getText(), "=", value);
		return value;
	}

	public static Expression buildExpression(DLCore core, DLModule module, ExpressionContext ctx) throws InvalidValue
	{
		if (ctx.PLUS() != null) {
			return new Add(
				buildExpression(core, module, ctx.expression(0)),
				buildExpression(core, module, ctx.expression(1)),
				ctx, module
			);
		} else if (ctx.MINUS() != null) {

			// unary minus -> invert value
			if (ctx.atom() != null) {
				return buildAtom(core, module, ctx.atom(), true);
			} else {
				return new Subtract(
					buildExpression(core, module, ctx.expression(0)),
					buildExpression(core, module, ctx.expression(1)),
					ctx, module
				);
			}
		} else if (ctx.MUL() != null) {
			return new Multiply(
				buildExpression(core, module, ctx.expression(0)),
				buildExpression(core, module, ctx.expression(1)),
				ctx, module
			);
		} else if (ctx.DIV() != null) {
			return new Divide(
				buildExpression(core, module, ctx.expression(0)),
				buildExpression(core, module, ctx.expression(1)),
				ctx, module
			);
		} else if (ctx.AND() != null) {
			return new And(
				buildExpression(core, module, ctx.expression(0)),
				buildExpression(core, module, ctx.expression(1)),
				ctx, module
			);
		} else if (ctx.OR() != null) {
			return new Or(
				buildExpression(core, module, ctx.expression(0)),
				buildExpression(core, module, ctx.expression(1)),
				ctx, module
			);
		} else if (ctx.POW() != null) {
			return new Pow(
				buildExpression(core, module, ctx.expression(0)),
				buildExpression(core, module, ctx.expression(1)),
				ctx, module
			);
		} else if (ctx.XOR() != null) {
			return new Xor(
				buildExpression(core, module, ctx.expression(0)),
				buildExpression(core, module, ctx.expression(1)),
				ctx, module
			);
		} else if (ctx.EQUALS() != null) {
			return new Equals(
				buildExpression(core, module, ctx.expression(0)),
				buildExpression(core, module, ctx.expression(1)),
				ctx, module
			);
		} else if (ctx.NOT() != null) {
			return new Not(buildExpression(core, module, ctx.expression(0)), ctx, module);
		} else if (ctx.PARENTHESES_OPEN() != null) {
			return buildExpression(core, module, ctx.expression(0));
		} else if (ctx.atom() != null) {
			return buildAtom(core, module, ctx.atom(), false);
		}

		throw new InvalidValue(DLHrfParsing.createErrorMessage(module, "Unknown expression part " + ctx.getText(), ctx));
	}

	private static Atom buildAtom(DLCore core, DLModule module, AtomContext ctx, boolean negate) throws InvalidValue
	{
		Object value;

		if (ctx.STRING_LITERAL() != null) {
			value = ctx.getText();
		} else if (ctx.SYMBOL() != null) {
			value = ctx.getText();
		} else if (ctx.INTEGER_LITERAL() != null) {
			// https://github.com/studio42gmbh/dl/issues/26
			String t = ctx.INTEGER_LITERAL().getText();
			if (t.startsWith("0")) {
				if (t.startsWith("0x")) {
					value = Long.parseLong(t.substring(2), 16);
				} else if (t.startsWith("0b")) {
					value = Long.parseLong(t.substring(2), 2);
				} else {
					value = Long.parseLong(t.substring(1), 8);
				}
			} else {
				value = Long.parseLong(t);
			}
		} else if (ctx.FLOAT_LITERAL() != null) {
			value = Double.parseDouble(ctx.FLOAT_LITERAL().getText());
		} else if (ctx.BOOLEAN_LITERAL() != null) {
			if ("true".equals(ctx.BOOLEAN_LITERAL().getText())) {
				value = true;
			} else {
				value = false;
			}
		} else if (ctx.REF() != null) {
			value = resolveReference(core, module, ctx.REF().getText(), ctx);
		} else {
			throw new InvalidValue(DLHrfParsing.createErrorMessage(module, "Unknown atom part " + ctx.getText(), ctx));
		}

		if (negate) {

			if (value instanceof Number) {
				return new Atom(-((Number) value).doubleValue());
			} else {
				throw new InvalidValue(
					DLHrfParsing.createErrorMessage(
						module,
						"Can just negate number types, but "
						+ ctx.getText()
						+ " is "
						+ value
						+ " of type "
						+ ((value != null) ? value.getClass().getCanonicalName() : ""), ctx));
			}
		} else {
			return new Atom(value);
		}
	}

	protected static Object resolveReference(DLCore core, DLModule module, String refId, ParserRuleContext ctx) throws InvalidValue
	{
		Optional ref = module.resolveReference(core, refId);

		if (ref.isEmpty()) {
			throw new InvalidValue(DLHrfParsing.createErrorMessage(module, "Reference $" + refId + " is not defined in module", ctx));
		}

		Object resolved = ref.orElseThrow();

		// Unwrap simple instances
		// @improvement this unwrapping should be done more generic if possible
		if (resolved instanceof SimpleTypeDLInstance) {
			return ((SimpleTypeDLInstance) resolved).getData();
		}

		return resolved;
	}
}
