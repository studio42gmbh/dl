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

import de.s42.dl.DLAnnotationFactory;
import de.s42.dl.DLCore;
import de.s42.dl.DLModule;
import de.s42.dl.annotations.DLContract;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.ParserException;
import de.s42.dl.parser.DLHrfParsing;
import de.s42.dl.parser.DLHrfParsingException;
import de.s42.dl.parser.DLParser.AnnotationContext;
import de.s42.dl.parser.DLParser.AnnotationDefinitionExpressionContext;
import de.s42.dl.parser.contracts.operators.And;
import de.s42.dl.parser.contracts.operators.Contract;
import de.s42.dl.parser.contracts.operators.ContractAnnotationFactory;
import de.s42.dl.parser.contracts.operators.Equals;
import de.s42.dl.parser.contracts.operators.Not;
import de.s42.dl.parser.contracts.operators.Or;
import de.s42.dl.parser.contracts.operators.Xor;
import de.s42.log.LogManager;
import de.s42.log.Logger;

/**
 * Handles expressions in DL. This is a reference implementation.
 *
 * @author Benjamin Schiller
 */
public final class DLHrfContractParser
{

	private final static Logger log = LogManager.getLogger(DLHrfContractParser.class.getName());

	private DLHrfContractParser()
	{
		// never instantiated
	}

	/**
	 * Creates a complex contract annotation factory and sets its name from ctx
	 *
	 * @param module
	 * @param name
	 * @param ctx
	 *
	 * @return
	 *
	 * @throws ParserException
	 */
	public static DLContractFactory resolveExpression(DLModule module, String name, AnnotationDefinitionExpressionContext ctx) throws ParserException
	{
		assert module != null;
		assert name != null;
		assert ctx != null;

		ContractExpression expr = buildExpression(module, ctx);

		DLContractFactory value = expr.evaluate();

		value.setName(name);

		return value;
	}

	public static ContractExpression buildExpression(DLModule module, AnnotationDefinitionExpressionContext ctx) throws ParserException
	{
		if (ctx.AND() != null) {
			return new And(
				buildExpression(module, ctx.annotationDefinitionExpression(0)),
				buildExpression(module, ctx.annotationDefinitionExpression(1)),
				ctx, module
			);
		} else if (ctx.OR() != null) {
			return new Or(
				buildExpression(module, ctx.annotationDefinitionExpression(0)),
				buildExpression(module, ctx.annotationDefinitionExpression(1)),
				ctx, module
			);
		} else if (ctx.XOR() != null) {
			return new Xor(
				buildExpression(module, ctx.annotationDefinitionExpression(0)),
				buildExpression(module, ctx.annotationDefinitionExpression(1)),
				ctx, module
			);
		} else if (ctx.EQUALS() != null) {
			return new Equals(
				buildExpression(module, ctx.annotationDefinitionExpression(0)),
				buildExpression(module, ctx.annotationDefinitionExpression(1)),
				ctx, module
			);
		} else if (ctx.NOT() != null) {
			return new Not(
				buildExpression(module, ctx.annotationDefinitionExpression(0)),
				ctx, module
			);
		} else if (ctx.PARENTHESES_OPEN() != null) {
			return buildExpression(module, ctx.annotationDefinitionExpression(0));
		} else if (ctx.annotation() != null) {
			return buildContract(module, ctx.annotation());
		}

		throw new DLHrfParsingException(
			"Unknown contract expression part " + ctx.getText(),
			module,
			ctx
		);
	}

	private static ContractExpression buildContract(DLModule module, AnnotationContext ctx) throws ParserException
	{
		try {
			String annotationName = ctx.annotationName().getText();

			DLCore core = module.getType().getCore();

			if (!core.hasAnnotationFactory(annotationName)) {
				throw new DLHrfParsingException(
					"Annotation factory '" + annotationName + "' is not defined",
					module,
					ctx
				);
			}

			DLAnnotationFactory factory = core.getAnnotationFactory(annotationName).orElseThrow();

			if (!DLContract.class.isAssignableFrom(factory.getAnnotationType())) {
				throw new DLHrfParsingException(
					"Annotation '" + annotationName + "' is not a contract",
					module,
					ctx
				);
			}

			Object[] parameters = DLHrfParsing.fetchStaticParameters(module, annotationName, ctx.staticParameters());
			ContractAnnotationFactory contract = new ContractAnnotationFactory(annotationName, factory, parameters);

			return new Contract(contract);
		} catch (DLException ex) {
			throw new DLHrfParsingException(
				"Unknown contract expression part " + ctx.getText(),
				module,
				ctx,
				ex
			);
		}
	}
}
