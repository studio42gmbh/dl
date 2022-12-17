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

import de.s42.dl.DLCore;
import de.s42.dl.DLEntity;
import de.s42.dl.annotations.*;
import de.s42.dl.annotations.files.IsDirectoryDLAnnotation;
import de.s42.dl.annotations.files.IsFileDLAnnotation;
import de.s42.dl.annotations.reflect.AttributeNamesDLAnnotation;
import de.s42.dl.annotations.reflect.TypeNameDLAnnotation;
import de.s42.dl.core.resolvers.FileCoreResolver;
import de.s42.dl.core.resolvers.ResourceCoreResolver;
import de.s42.dl.core.resolvers.StringCoreResolver;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.pragmas.*;
import de.s42.dl.types.*;
import de.s42.log.LogLevel;
import de.s42.log.LogManager;
import de.s42.log.Logger;

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

			// Allow definitions and require by default
			allowDefineTypes = true;
			allowDefineAnnotationFactories = true;
			allowDefinePragmas = true;
			allowUsePragmas = true;
			allowRequire = true;

			// Add file and resource resolver
			addResolver(new FileCoreResolver(this));
			addResolver(new ResourceCoreResolver(this));
			addResolver(new StringCoreResolver(this));

			// Define basic annotations
			defineAnnotationFactory(new ContainDLAnnotation(), ContainDLAnnotation.contain.class.getSimpleName());
			defineAnnotationFactory(new DontPersistDLAnnotation(), DontPersistDLAnnotation.dontPersist.class.getSimpleName());
			defineAnnotationFactory(new DynamicDLAnnotation(), DynamicDLAnnotation.dynamic.class.getSimpleName());
			defineAnnotationFactory(new JavaDLAnnotation(), JavaDLAnnotation.DEFAULT_SYMBOL);
			defineAnnotationFactory(new RequiredDLAnnotation(), RequiredDLAnnotation.required.class.getSimpleName());
			defineAnnotationFactory(new NoGenericsDLAnnotation(), NoGenericsDLAnnotation.noGenerics.class.getSimpleName());
			defineAnnotationFactory(new ReadOnlyDLAnnotation(), ReadOnlyDLAnnotation.readonly.class.getSimpleName());
			defineAnnotationFactory(new WriteOnlyDLAnnotation(), WriteOnlyDLAnnotation.writeonly.class.getSimpleName());

			// File annotations
			defineAnnotationFactory(new IsFileDLAnnotation(), IsFileDLAnnotation.isFile.class.getSimpleName());
			defineAnnotationFactory(new IsDirectoryDLAnnotation(), IsDirectoryDLAnnotation.isDirectory.class.getSimpleName());

			// Reflect annotations
			defineAnnotationFactory(new TypeNameDLAnnotation(), TypeNameDLAnnotation.typeName.class.getSimpleName());
			defineAnnotationFactory(new AttributeNamesDLAnnotation(), AttributeNamesDLAnnotation.attributeNames.class.getSimpleName());

			defineAnnotationFactory(new ContainOnlyDLAnnotation(), ContainOnlyDLAnnotation.DEFAULT_SYMBOL);
			defineAnnotationFactory(new ContainOnceDLAnnotation(), ContainOnceDLAnnotation.DEFAULT_SYMBOL);
			defineAnnotationFactory(new ExportDLAnnotation(), ExportDLAnnotation.DEFAULT_SYMBOL);
			defineAnnotationFactory(new GenerateUUIDDLAnnotation(), GenerateUUIDDLAnnotation.DEFAULT_SYMBOL);
			defineAnnotationFactory(new GenericDLAnnotation(), GenericDLAnnotation.DEFAULT_SYMBOL);
			defineAnnotationFactory(new LengthDLAnnotation(), LengthDLAnnotation.DEFAULT_SYMBOL);
			defineAnnotationFactory(new RangeDLAnnotation(), RangeDLAnnotation.DEFAULT_SYMBOL);
			defineAnnotationFactory(new GreaterDLAnnotation(), GreaterDLAnnotation.greater.class.getSimpleName());
			defineAnnotationFactory(new GreaterEqualDLAnnotation(), GreaterEqualDLAnnotation.DEFAULT_SYMBOL);
			defineAnnotationFactory(new EqualDLAnnotation(), EqualDLAnnotation.DEFAULT_SYMBOL);
			defineAnnotationFactory(new NotEqualDLAnnotation(), NotEqualDLAnnotation.DEFAULT_SYMBOL);
			defineAnnotationFactory(new LesserEqualDLAnnotation(), LesserEqualDLAnnotation.DEFAULT_SYMBOL);
			defineAnnotationFactory(new LesserDLAnnotation(), LesserDLAnnotation.DEFAULT_SYMBOL);
			defineAnnotationFactory(new PreliminaryDLAnnotation(), PreliminaryDLAnnotation.DEFAULT_SYMBOL);
			defineAnnotationFactory(new RequiredOrDLAnnotation(), RequiredOrDLAnnotation.DEFAULT_SYMBOL);
			defineAnnotationFactory(new UniqueDLAnnotation(), UniqueDLAnnotation.DEFAULT_SYMBOL);
			defineAnnotationFactory(new RegexDLAnnotation(), RegexDLAnnotation.DEFAULT_SYMBOL);

			// Define basic pragmas
			definePragma(new BasePathPragma());
			definePragma(new DefinePragmaPragma());
			definePragma(new DisableDefinePragmasPragma());
			definePragma(new DisableUsePragmasPragma());
			definePragma(new DisableDefineTypesPragma());
			definePragma(new DisableDefineAnnotationsPragma());
			definePragma(new DisableRequirePragma());

			// Define basic simple types
			defineType(new BooleanDLType(), "java.lang.Boolean", "boolean", "bool");
			defineType(new ByteDLType(), "java.lang.Byte", "byte");
			defineType(new CharDLType(), "java.lang.Character", "Char", "char");
			defineType(new ShortDLType(), "java.lang.Short", "short");
			defineType(new ClassDLType(), "java.lang.Class");
			defineType(new DoubleDLType(), "java.lang.Double", "double");
			defineType(new FloatDLType(), "java.lang.Float", "float");
			defineType(new IntegerDLType(), "java.lang.Integer", "int");
			defineType(new LongDLType(), "java.lang.Long", "long");
			defineType(new NumberDLType(), "java.lang.Number");
			defineType(new ObjectDLType(), "java.lang.Object");
			defineType(new PathDLType(), "java.nio.file.Path", "sun.nio.fs.WindowsPath");
			defineType(new SymbolDLType());
			defineType(new UUIDDLType(), "java.util.UUID", "uuid");
			defineType(new StringDLType(), "java.lang.String", "string", "str");
			defineType(new DateDLType(), "java.util.Date", "java.sql.Timestamp");

			defineType(DLEntity.class, "DLEntity");

			// Define log types
			defineType(LogLevel.class);
			defineType(Logger.class);
			defineType(LogManager.class);

			// Define List types https://github.com/studio42gmbh/dl/issues/10
			// The specific generic types will be generated automatically in BaseDLCore.getType(String name, List<DLType> genericTypes)
			defineType(new ListDLType(),
				"java.util.List",
				"java.util.ArrayList",
				"java.util.LinkedList",
				"java.util.Collections$UnmodifiableList"
			);

			// Define Array types
			// The specific generic types will be generated automatically in BaseDLCore.getType(String name, List<DLType> genericTypes)
			defineType(new ArrayDLType(), "java.lang.Array");

			// Define Map types https://github.com/studio42gmbh/dl/issues/11
			// The specific generic types will be generated automatically in BaseDLCore.getType(String name, List<DLType> genericTypes)
			defineType(new MapDLType(),
				"java.util.Map",
				"java.util.HashMap",
				"java.util.Collections$UnmodifiableMap",
				"de.s42.base.collections.MapHelper$MapN",
				"java.util.Collections$CheckedMap"
			);

			// Define Set types https://github.com/studio42gmbh/dl/issues/24
			// The specific generic types will be generated automatically in BaseDLCore.getType(String name, List<DLType> genericTypes)
			defineType(new SetDLType(),
				"java.util.Set",
				"java.util.HashSet",
				"java.util.Collections$UnmodifiableSet",
				"java.util.ImmutableCollections$SetN");

			// Define type Core and map $core with this
			CoreDLType coreType = (CoreDLType) defineType(new CoreDLType(),
				"Core",
				DLCore.class.getName(),
				DefaultCore.class.getName()
			);
			addExported(new CoreDLInstance(this, coreType));

		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}
}
