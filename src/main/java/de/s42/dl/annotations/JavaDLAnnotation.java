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
package de.s42.dl.annotations;

import de.s42.dl.DLCore;
import de.s42.dl.DLType;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.exceptions.InvalidType;
import de.s42.dl.types.DefaultDLType;
import de.s42.log.LogManager;
import de.s42.log.Logger;

/**
 *
 * @author Benjamin Schiller
 */
public class JavaDLAnnotation extends AbstractDLAnnotation
{

	private final static Logger log = LogManager.getLogger(JavaDLAnnotation.class.getName());

	public final static String DEFAULT_SYMBOL = "java";

	public JavaDLAnnotation()
	{
		this(DEFAULT_SYMBOL);
	}

	public JavaDLAnnotation(String name)
	{
		super(name);
	}

	@Override
	public void bindToType(DLCore core, DLType type, Object... parameters) throws InvalidAnnotation, InvalidType
	{
		assert type != null;

		if (parameters != null && parameters.length > 0) {
			if (parameters.length > 1) {
				throw new InvalidAnnotation("has to have 0 or 1 parameter but has " + parameters.length);
			}

			if (parameters.length == 1) {

				// allow to turn off java annotation with @java(false)
				if (parameters[0] instanceof Boolean) {
					if (!((Boolean) parameters[0])) {
						((DefaultDLType) type).setJavaType(null);
						return;
					}
				}

				if (!(parameters[0] instanceof String)) {
					throw new InvalidAnnotation("has to have first String parameter but is of type " + parameters[0].getClass().getName());
				}

				try {
					((DefaultDLType) type).setJavaType(Class.forName((String) parameters[0]));
				} catch (ClassNotFoundException ex) {
					throw new InvalidType("Custom java class invalid - " + ex.getMessage(), ex);
				}
			}
		} // if no parameter given use the type canonical name as java class identifier
		else {
			try {
				((DefaultDLType) type).setJavaType(Class.forName(type.getCanonicalName()));
			} catch (ClassNotFoundException ex) {
				throw new InvalidType("Custom java class invalid - " + ex.getMessage(), ex);
			}
		}
	}
}
