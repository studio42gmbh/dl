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

import de.s42.base.strings.StringHelper;
import de.s42.dl.DLCore;
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
import de.s42.dl.DLModule;
import de.s42.dl.DLReferenceResolver;
import de.s42.dl.exceptions.ParserException;
import de.s42.dl.parser.DLHrfParsingException;
import de.s42.dl.parser.DLParser.AtomContext;
import de.s42.dl.parser.DLParser.ExpressionContext;
import de.s42.dl.parser.expression.operators.Greater;
import de.s42.dl.parser.expression.operators.GreaterEquals;
import de.s42.dl.parser.expression.operators.Lesser;
import de.s42.dl.parser.expression.operators.LesserEquals;
import de.s42.dl.parser.expression.operators.Like;
import de.s42.dl.parser.expression.operators.Pow;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.util.Optional;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Handles expressions in DL. This is a reference implementation.
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

	public static Object resolveExpression(DLModule module, ExpressionContext ctx) throws ParserException
	{
		assert module != null;
		assert ctx != null;

		Expression expr = buildExpression(module, ctx);

		Object value = expr.evaluate();

		//log.debug("EXPRESSION", ctx.getText(), "=", value);
		return value;
	}

	public static Expression buildExpression(DLModule module, ExpressionContext ctx) throws ParserException
	{
		assert module != null;
		assert ctx != null;

		if (ctx.PLUS() != null) {
			return new Add(
				buildExpression(module, ctx.expression(0)),
				buildExpression(module, ctx.expression(1)),
				ctx, module
			);
		} else if (ctx.MINUS() != null) {

			// unary minus -> invert value
			if (ctx.atom() != null) {
				return buildAtom(module, ctx.atom(), true);
			} else {
				return new Subtract(
					buildExpression(module, ctx.expression(0)),
					buildExpression(module, ctx.expression(1)),
					ctx, module
				);
			}
		} else if (ctx.MUL() != null) {
			return new Multiply(
				buildExpression(module, ctx.expression(0)),
				buildExpression(module, ctx.expression(1)),
				ctx, module
			);
		} else if (ctx.DIV() != null) {
			return new Divide(
				buildExpression(module, ctx.expression(0)),
				buildExpression(module, ctx.expression(1)),
				ctx, module
			);
		} else if (ctx.AND() != null) {
			return new And(
				buildExpression(module, ctx.expression(0)),
				buildExpression(module, ctx.expression(1)),
				ctx, module
			);
		} else if (ctx.OR() != null) {
			return new Or(
				buildExpression(module, ctx.expression(0)),
				buildExpression(module, ctx.expression(1)),
				ctx, module
			);
		} else if (ctx.POW() != null) {
			return new Pow(
				buildExpression(module, ctx.expression(0)),
				buildExpression(module, ctx.expression(1)),
				ctx, module
			);
		} else if (ctx.XOR() != null) {
			return new Xor(
				buildExpression(module, ctx.expression(0)),
				buildExpression(module, ctx.expression(1)),
				ctx, module
			);
		} else if (ctx.EQUALS() != null) {
			return new Equals(
				buildExpression(module, ctx.expression(0)),
				buildExpression(module, ctx.expression(1)),
				ctx, module
			);
		} else if (ctx.LIKE() != null) {
			return new Like(
				buildExpression(module, ctx.expression(0)),
				buildExpression(module, ctx.expression(1)),
				ctx, module
			);
		} else if (ctx.NOT() != null) {
			return new Not(
				buildExpression(module, ctx.expression(0)),
				ctx, module
			);
		} else if (ctx.LESSER() != null) {
			return new Lesser(
				buildExpression(module, ctx.expression(0)),
				buildExpression(module, ctx.expression(1)),
				ctx, module
			);
		} else if (ctx.LESSER_EQUALS() != null) {
			return new LesserEquals(
				buildExpression(module, ctx.expression(0)),
				buildExpression(module, ctx.expression(1)),
				ctx, module
			);
		} else if (ctx.GREATER() != null) {
			return new Greater(
				buildExpression(module, ctx.expression(0)),
				buildExpression(module, ctx.expression(1)),
				ctx, module
			);
		} else if (ctx.GREATER_EQUALS() != null) {
			return new GreaterEquals(
				buildExpression(module, ctx.expression(0)),
				buildExpression(module, ctx.expression(1)),
				ctx, module
			);
		} else if (ctx.PARENTHESES_OPEN() != null) {
			return buildExpression(module, ctx.expression(0));
		} else if (ctx.atom() != null) {
			return buildAtom(module, ctx.atom(), false);
		}

		throw new DLHrfParsingException(
			"Unknown expression part " + ctx.getText(),
			module,
			ctx
		);
	}

	private static Atom buildAtom(DLModule module, AtomContext ctx, boolean negate) throws ParserException
	{
		assert module != null;
		assert ctx != null;

		Object value;

		if (ctx.STRING_LITERAL() != null) {
			value = unescapeString(ctx.getText());
		} else if (ctx.SYMBOL() != null) {
			value = ctx.getText();
		} else if (ctx.INTEGER_LITERAL() != null) {
			// DLHrfParsing Allow hexadecimal numbers ad basic format in HRF DL 0x00... (#26)
			String t = ctx.INTEGER_LITERAL().getText();
			if (t.startsWith("0") && t.length() > 1) {
				if (t.startsWith("0x")) {
					value = Long.valueOf(t.substring(2), 16);
				} else if (t.startsWith("0b")) {
					value = Long.valueOf(t.substring(2), 2);
				} else {
					value = Long.valueOf(t.substring(1), 8);
				}
			} else {
				value = Long.valueOf(t);
			}
		} else if (ctx.FLOAT_LITERAL() != null) {
			value = Double.valueOf(ctx.FLOAT_LITERAL().getText());
		} else if (ctx.BOOLEAN_LITERAL() != null) {
			if ("true".equals(ctx.BOOLEAN_LITERAL().getText())) {
				value = true;
			} else {
				value = false;
			}
		} else if (ctx.REF() != null) {
			value = resolveReference(module, ctx.REF().getText(), ctx);
		} else {
			throw new DLHrfParsingException(
				"Unknown atom part " + ctx.getText(),
				module,
				ctx
			);
		}

		if (negate) {

			if (value instanceof Number) {
				return new Atom(-((Number) value).doubleValue());
			} else {
				throw new DLHrfParsingException(
					"Can just negate number types, but "
					+ ctx.getText()
					+ " is "
					+ value
					+ " of type "
					+ ((value != null) ? value.getClass().getCanonicalName() : ""),
					module,
					ctx
				);
			}
		} else {
			return new Atom(value);
		}
	}

	public static String unescapeString(String stringValue)
	{
		assert stringValue != null;

		return StringHelper.unescapeJavaString(stringValue.substring(1, stringValue.length() - 1));
	}

	public static Object resolveReference(DLModule module, String refId, ParserRuleContext ctx) throws ParserException
	{
		assert module != null;
		assert refId != null;
		assert ctx != null;

		DLCore core = module.getCore();

		assert core != null;

		DLReferenceResolver resolver = core.getReferenceResolver();

		assert resolver != null;

		// First try to find the ref in core
		// Call the parser in non strict mode to avoid getting exceptions -> will return null then
		Optional ref = resolver.resolve(core, refId, false);
		if (ref.isPresent()) {
			return ref.orElseThrow();
		}

		// Resolve the reference ignoring the first char which is the $ sign
		try {
			ref = resolver.resolve(module, refId);
			if (ref.isPresent()) {
				return ref.orElseThrow();
			}
		} // Translate the parser exception into ovreall parsing context
		catch (ParserException ex) {
			throw new DLHrfParsingException(
				"Reference '" + refId + "' is invalid - " + ex.getMessage(),
				module,
				ctx,
				ex
			);
		}

		return null;
	}
}
