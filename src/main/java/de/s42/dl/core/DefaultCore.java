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
package de.s42.dl.core;

import de.s42.dl.annotations.*;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.pragmas.*;
import de.s42.dl.types.*;

/**
 *
 * @author Benjamin Schiller
 */
public class DefaultCore extends BaseDLCore
{

	public DefaultCore()
	{
		init();
	}

	private void init()
	{
		try {
			allowDefineTypes = true;
			allowDefineAnnotations = true;
			allowDefinePragmas = true;
			allowRequire = true;

			defineType(new ArrayDLType(), "java.lang.Array");
			defineType(new BooleanDLType(), "java.lang.Boolean", "boolean", "bool");
			defineType(new ClassDLType(), "java.lang.Class");
			defineType(new DoubleDLType(), "java.lang.Double", "double");
			defineType(new FloatDLType(), "java.lang.Float", "float");
			defineType(new IntegerDLType(), "java.lang.Integer", "int");
			defineType(new LongDLType(), "java.lang.Long", "long");
			defineType(new MapDLType(), "java.util.Map");
			defineType(new NumberDLType(), "java.lang.Number");
			defineType(new ObjectDLType(), "java.lang.Object");
			defineType(new PathDLType(), "java.nio.file.Path");
			defineType(new SymbolDLType());
			defineType(new UUIDDLType(), "java.util.UUID");
			defineType(new StringDLType(), "java.lang.String");
			defineType(new DateDLType(), "java.util.Date");

			// define type Core and map $core with this
			CoreDLType coreType = (CoreDLType) defineType(new CoreDLType(), "Core", "de.s42.dl.DLCore");
			addExported(new CoreDLInstance(this, coreType));

			defineAnnotation(new ContainDLAnnotation());
			defineAnnotation(new ContainOnceDLAnnotation());
			defineAnnotation(new DynamicDLAnnotation());
			defineAnnotation(new ExportDLAnnotation());
			defineAnnotation(new GenerateUUIDDLAnnotation());
			defineAnnotation(new GenericDLAnnotation());
			defineAnnotation(new I18nDLAnnotation());
			defineAnnotation(new IsDirectoryDLAnnotation());
			defineAnnotation(new IsFileDLAnnotation());
			defineAnnotation(new JavaDLAnnotation());
			defineAnnotation(new LengthDLAnnotation());
			defineAnnotation(new RangeDLAnnotation());
			defineAnnotation(new GreaterDLAnnotation());
			defineAnnotation(new PreliminaryDLAnnotation());
			defineAnnotation(new ReadOnlyDLAnnotation());
			defineAnnotation(new RequiredDLAnnotation());
			defineAnnotation(new RequiredOrDLAnnotation());
			defineAnnotation(new UniqueDLAnnotation());
			defineAnnotation(new WriteOnlyDLAnnotation());

			definePragma(new BasePathPragma());
			definePragma(new DefinePragmaPragma());
			definePragma(new DisableDefinePragmasPragma());
			definePragma(new DisableDefineTypesPragma());
			definePragma(new DisableDefineAnnotationsPragma());
			definePragma(new DisableRequirePragma());
		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}
}
