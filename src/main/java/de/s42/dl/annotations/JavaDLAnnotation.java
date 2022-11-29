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
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Benjamin Schiller
 * @todo Provide a way to change the class loader for plugins etc.
 */
public class JavaDLAnnotation extends AbstractDLAnnotation
{

	private final static Logger log = LogManager.getLogger(JavaDLAnnotation.class.getName());

	/**
	 * ATTENTION: This annotation will be mapped under java -> for IDE and code standard guidelines we do not call it java here
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.FIELD, ElementType.TYPE})
	@DLAnnotationType(JavaDLAnnotation.class)
	public static @interface javaAnnotation
	{
		String javaType();
	}
	
	public final static String DEFAULT_SYMBOL = "java";

	@DLAnnotationParameter(ordinal = 0)
	protected Object javaType;

	@Override
	public void bindToType(DLCore core, DLType type) throws InvalidAnnotation, InvalidType
	{
		assert type != null;
		
		ClassLoader classLoader = core.getClassLoader();

		if (javaType instanceof String) {
			try {
				((DefaultDLType) type).setJavaType(Class.forName((String) javaType, true, classLoader));
			} catch (ClassNotFoundException ex) {
				throw new InvalidType("Custom java class invalid - " + ex.getMessage(), ex);
			}
		} else if (javaType instanceof Boolean) {
			if (((Boolean) javaType) == false) {
				((DefaultDLType) type).setJavaType(null);
			}
		} // No javaType given -> Use the types canonical name as fqn
		else {
			try {
				javaType = type.getCanonicalName();
				((DefaultDLType) type).setJavaType(Class.forName((String) javaType, true, classLoader));
			} catch (ClassNotFoundException ex) {
				throw new InvalidType("Custom java class invalid - " + ex.getMessage(), ex);
			}
		}
	}

	public Object getJavaType()
	{
		return javaType;
	}

	public void setJavaType(Object javaType)
	{
		this.javaType = javaType;
	}
}
