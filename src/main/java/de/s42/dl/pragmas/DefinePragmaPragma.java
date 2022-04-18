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
package de.s42.dl.pragmas;

import de.s42.dl.DLCore;
import de.s42.dl.DLPragma;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidPragma;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Benjamin Schiller
 */
public class DefinePragmaPragma extends AbstractDLPragma
{

	private final static Logger log = LogManager.getLogger(DefinePragmaPragma.class.getName());

	public final static String DEFAULT_IDENTIFIER = "definePragma";

	public DefinePragmaPragma()
	{
		super(DEFAULT_IDENTIFIER);
	}

	public DefinePragmaPragma(String identifier)
	{
		super(identifier);
	}

	@Override
	public void doPragma(DLCore core, Object... parameters) throws InvalidPragma
	{
		assert core != null;
		assert parameters != null;

		parameters = validateParameters(parameters, new Class[]{Class.class});

		Class pragmaClass = (Class) parameters[0];

		try {
			DLPragma pragma = (DLPragma) pragmaClass.getConstructor().newInstance();

			core.definePragma(pragma);

			if (!pragma.getName().equals(pragma.getClass().getName())) {
				core.defineAliasForPragma(pragma.getClass().getName(), pragma);
			}
		} catch (DLException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
			throw new InvalidPragma("Can not load new pragma - " + ex.getMessage(), ex);
		}
	}
}
