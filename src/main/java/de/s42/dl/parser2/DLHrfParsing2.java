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
package de.s42.dl.parser2;

import de.s42.dl.*;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.instances.DefaultDLModule;
import de.s42.dl.parser.DLLexer;
import de.s42.dl.parser.DLParser;
import de.s42.dl.parser.DLParserBaseListener;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * This is a test implementation - PLEASE DO NOT USE THIS!
 * @author Benjamin Schiller
 */
public class DLHrfParsing2 extends DLParserBaseListener
{

	private final static Logger log = LogManager.getLogger(DLHrfParsing2.class.getName());

	private final DefaultDLModule module;
	private final DLCore core;

	public DLHrfParsing2(DLCore core, DefaultDLModule module)
	{
		this.core = core;
		this.module = module;
	}

	@Override
	public void enterTypeDefinition(DLParser.TypeDefinitionContext ctx)
	{
		log.debug("enterTypeDefinition", ctx.typeHeader().typeDefinitionName().getText());
	}
	
	

	@Override
	public void exitTypeDefinition(DLParser.TypeDefinitionContext ctx)
	{
		log.debug("exitTypeDefinition", ctx.typeHeader().typeDefinitionName().getText());
	}
	
	public static DLModule parse(DLCore core, String moduleId, String data) throws DLException
	{
		DefaultDLModule module = (DefaultDLModule) core.createModule(moduleId);

		DLHrfParsing2 parsing = new DLHrfParsing2(core, module);

		// Setup lexer
		DLLexer lexer = new DLLexer(CharStreams.fromString(data));
		lexer.removeErrorListeners();
		lexer.addErrorListener(new DLHrfParsing2ErrorHandler(parsing, module));
		TokenStream tokens = new CommonTokenStream(lexer);

		// @test Iterate tokens from lexer
		while (true) {
			Token token = tokens.LT(1);
			log.debug("TOKEN: " + token);
			if (token.getType() == DLLexer.EOF) {
				break;
			}
			tokens.consume();
		}
		tokens.seek(0);
		
		// Setup parser
		DLParser parser = new DLParser(tokens);
		parser.removeErrorListeners();
		parser.addErrorListener(new DLHrfParsing2ErrorHandler(parsing, module));

		// Parse with treewalker
		DLParser.DataContext root = parser.data();
		ParseTreeWalker walker = new ParseTreeWalker();

		try {
			walker.walk(parsing, root);
		} catch (RuntimeException ex) {
			if (ex.getCause() instanceof DLException) {
				throw (DLException) ex.getCause();
			}
			throw ex;
		}

		return module;
	}
}
