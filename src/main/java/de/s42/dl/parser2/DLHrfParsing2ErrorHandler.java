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

import de.s42.base.files.FilesHelper;
import de.s42.dl.DLModule;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 *
 * @author Benjamin Schiller
 */
public class DLHrfParsing2ErrorHandler extends BaseErrorListener
{

	protected final DLModule module;
	protected final DLHrfParsing2 dl;

	public DLHrfParsing2ErrorHandler(DLHrfParsing2 dl, DLModule module)
	{
		this.module = module;
		this.dl = dl;
	}

	@Override
	public void syntaxError(Recognizer<?, ?> rcgnzr, Object o, int line, int position, String message, RecognitionException re)
	{
		StringBuilder msg = new StringBuilder();
		msg
			.append(message)
			.append("\n")
			.append(FilesHelper.createMavenNetbeansFileConsoleLink("\t ",
				module.getShortName(), module.getName(), line, position + 1, false));

		throw new RuntimeException(msg.toString());
	}
	
	public String createErrorMessage(String reason, ParserRuleContext context) throws RuntimeException
	{
		return createErrorMessage(module, reason, context);
	}

	public static String createErrorMessage(DLModule module, String reason, ParserRuleContext context) throws RuntimeException
	{
		// https://github.com/apache/netbeans/blob/c084119009d2e0f736f225d706bc1827af283501/java/maven/src/org/netbeans/modules/maven/output/GlobalOutputProcessor.java
		//"Das sollte gehen @ GPClient, C:\\home\\f12\\development\\gods_playground\\gp-client\\data\\renderdoc.config.dl, line 5, column 6");

		StringBuilder message = new StringBuilder();
		message
			.append(reason)
			.append("\n")
			.append(FilesHelper.createMavenNetbeansFileConsoleLink("\t ",
				module.getShortName(), module.getName(),
				context.start.getLine(), context.start.getCharPositionInLine() + 1, false));

		return message.toString();
	}

	public String createErrorMessage(String reason, Throwable cause, ParserRuleContext context) throws RuntimeException
	{
		return createErrorMessage(module, reason, cause, context);
	}

	public static String createErrorMessage(DLModule module, String reason, Throwable cause, ParserRuleContext context) throws RuntimeException
	{
		StringBuilder message = new StringBuilder();

		message
			.append(reason);
		if (cause.getMessage() != null) {
			message
				.append(" - ")
				// @improvement DL why is the exception throwing if i leave " in there?
				.append(cause.getMessage().replace("\"", ""));
		}

		message
			.append("\n")
			.append(FilesHelper.createMavenNetbeansFileConsoleLink("\t ",
				module.getShortName(), module.getName(),
				context.start.getLine(), context.start.getCharPositionInLine() + 1, false));

		return message.toString();
	}	
}
