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

import de.s42.dl.annotations.attributes.WriteOnlyDLAnnotation;
import de.s42.dl.annotations.attributes.RequiredDLAnnotation;
import de.s42.dl.annotations.types.NoGenericsDLAnnotation;
import de.s42.dl.annotations.attributes.ReadOnlyDLAnnotation;
import de.s42.dl.annotations.types.JavaDLAnnotation;
import de.s42.dl.DLCore;
import de.s42.dl.DLEntity;
import de.s42.dl.DLPragma;
import de.s42.dl.DLType;
import de.s42.dl.annotations.*;
import de.s42.dl.annotations.attributes.GenerateUUIDDLAnnotation;
import de.s42.dl.annotations.attributes.GreaterDLAnnotation;
import de.s42.dl.annotations.attributes.GreaterEqualDLAnnotation;
import de.s42.dl.annotations.attributes.NoDefaultValueDLAnnotation;
import de.s42.dl.annotations.files.IsDirectoryDLAnnotation;
import de.s42.dl.annotations.files.IsFileDLAnnotation;
import de.s42.dl.annotations.instances.ExportDLAnnotation;
import de.s42.dl.annotations.numbers.EvenDLAnnotation;
import de.s42.dl.annotations.numbers.RangeDLAnnotation;
import de.s42.dl.annotations.persistence.DontPersistDLAnnotation;
import de.s42.dl.annotations.reflect.AttributeNamesDLAnnotation;
import de.s42.dl.annotations.reflect.TypeNameDLAnnotation;
import de.s42.dl.annotations.strings.LengthDLAnnotation;
import de.s42.dl.annotations.strings.RegexDLAnnotation;
import de.s42.dl.annotations.types.GenericDLAnnotation;
import de.s42.dl.core.resolvers.FileCoreResolver;
import de.s42.dl.core.resolvers.LibraryCoreResolver;
import de.s42.dl.core.resolvers.ResourceCoreResolver;
import de.s42.dl.core.resolvers.StringCoreResolver;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.instances.base.Environment;
import de.s42.dl.pragmas.*;
import de.s42.dl.pragmas.debug.LogPragma;
import de.s42.dl.types.*;
import de.s42.dl.types.base.ArrayDLType;
import de.s42.dl.types.base.ClassDLType;
import de.s42.dl.types.base.DateDLType;
import de.s42.dl.types.base.PathDLType;
import de.s42.dl.types.base.SymbolDLType;
import de.s42.dl.types.base.UUIDDLType;
import de.s42.dl.types.collections.ListDLType;
import de.s42.dl.types.collections.MapDLType;
import de.s42.dl.types.collections.SetDLType;
import de.s42.dl.types.dl.CoreDLType;
import de.s42.dl.types.primitive.BooleanDLType;
import de.s42.dl.types.primitive.ByteDLType;
import de.s42.dl.types.primitive.CharDLType;
import de.s42.dl.types.primitive.DoubleDLType;
import de.s42.dl.types.primitive.FloatDLType;
import de.s42.dl.types.primitive.IntegerDLType;
import de.s42.dl.types.primitive.LongDLType;
import de.s42.dl.types.primitive.NumberDLType;
import de.s42.dl.types.primitive.ObjectDLType;
import de.s42.dl.types.primitive.ShortDLType;
import de.s42.dl.types.primitive.StringDLType;
import de.s42.log.LogLevel;
import de.s42.log.LogManager;
import de.s42.log.Logger;

/**
 *
 * @author Benjamin Schiller
 */
public class DefaultCore extends BaseDLCore
{

	public final static DLCoreResolver RESOURCE_RESOLVER = new ResourceCoreResolver();
	public final static DLCoreResolver STRING_RESOLVER = new StringCoreResolver();
	public final static DLCoreResolver FILE_RESOLVER = new FileCoreResolver();
	public final static DLCoreResolver LIBRARY_RESOLVER = new LibraryCoreResolver();

	public final static DLPragma BASE_PATH_PRAGMA = new BasePathPragma();
	public final static DLPragma DEFINE_PRAGMA_PRAGMA = new DefinePragmaPragma();
	public final static DLPragma DISABLE_DEFINE_PRAGMAS_PRAGMA = new DisableDefinePragmasPragma();
	public final static DLPragma DISABLE_USE_PRAGMAS_PRAGMA = new DisableUsePragmasPragma();
	public final static DLPragma DISABLE_DEFINE_TYPES_PRAGMA = new DisableDefineTypesPragma();
	public final static DLPragma DISABLE_DEFINE_ANNOTATIONS_PRAGMA = new DisableDefineAnnotationsPragma();
	public final static DLPragma DISABLE_REQUIRE_PRAGMA = new DisableRequirePragma();
	public final static DLPragma DISABLE_USE_ASSERTS_PRAGMA = new DisableUseAssertsPragma();
	public final static DLPragma LOG_PRAGMA = new LogPragma();

	public DefaultCore()
	{
		// Allow definitions and require by default
		super(true);
		init();
	}

	private void init()
	{
		try {

			loadResolvers(this);

			loadAnnotations(this);

			loadPragmas(this);

			loadTypes(this);

			loadExports(this);

		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void loadExports(BaseDLCore core) throws DLException
	{
		assert core != null;

		// Define type Core and map $core with this
		CoreDLType coreType = (CoreDLType) core.defineType(new CoreDLType(),
			"Core",
			DLCore.class.getName(),
			DefaultCore.class.getName(),
			CoreDLType.class.getName()
		);
		core.addExported(new CoreDLInstance(core, coreType));

		// Define type for Environment and map an instance in $env
		core.defineType(Environment.class);
		core.addExported("env", new Environment());
	}

	public static void loadResolvers(BaseDLCore core)
	{
		assert core != null;

		// Add file and resource resolver
		core.addResolver(LIBRARY_RESOLVER);
		core.addResolver(FILE_RESOLVER);
		core.addResolver(RESOURCE_RESOLVER);
		core.addResolver(STRING_RESOLVER);
	}

	public static void loadAnnotations(DLCore core) throws DLException
	{
		assert core != null;

		// Define basic annotations
		core.defineAnnotationFactory(new JavaDLAnnotation(), JavaDLAnnotation.DEFAULT_SYMBOL, JavaDLAnnotation.class.getName());
		core.defineAnnotationFactory(new RequiredDLAnnotation(), RequiredDLAnnotation.required.class.getSimpleName(), RequiredDLAnnotation.class.getName());
		core.defineAnnotationFactory(new ReadOnlyDLAnnotation(), ReadOnlyDLAnnotation.readonly.class.getSimpleName(), ReadOnlyDLAnnotation.class.getName());
		core.defineAnnotationFactory(new WriteOnlyDLAnnotation(), WriteOnlyDLAnnotation.writeonly.class.getSimpleName(), WriteOnlyDLAnnotation.class.getName());
		core.defineAnnotationFactory(new NoDefaultValueDLAnnotation(), NoDefaultValueDLAnnotation.noDefaultValue.class.getSimpleName(), NoDefaultValueDLAnnotation.class.getName());
		core.defineAnnotationFactory(new ExportDLAnnotation(), ExportDLAnnotation.DEFAULT_SYMBOL, ExportDLAnnotation.class.getName());

		core.defineAnnotationFactory(new DontPersistDLAnnotation(), DontPersistDLAnnotation.dontPersist.class.getSimpleName(), DontPersistDLAnnotation.class.getName());
		core.defineAnnotationFactory(new GenericDLAnnotation(), GenericDLAnnotation.DEFAULT_SYMBOL, GenericDLAnnotation.class.getName());
		core.defineAnnotationFactory(new NoGenericsDLAnnotation(), NoGenericsDLAnnotation.noGenerics.class.getSimpleName(), NoGenericsDLAnnotation.class.getName());
		core.defineAnnotationFactory(new ContainDLAnnotation(), ContainDLAnnotation.contain.class.getSimpleName(), ContainDLAnnotation.class.getName());

		// File annotations
		core.defineAnnotationFactory(new IsFileDLAnnotation(), IsFileDLAnnotation.isFile.class.getSimpleName(), IsFileDLAnnotation.class.getName());
		core.defineAnnotationFactory(new IsDirectoryDLAnnotation(), IsDirectoryDLAnnotation.isDirectory.class.getSimpleName(), IsDirectoryDLAnnotation.class.getName());

		// Reflect annotations
		core.defineAnnotationFactory(new TypeNameDLAnnotation(), TypeNameDLAnnotation.typeName.class.getSimpleName(), TypeNameDLAnnotation.class.getName());
		core.defineAnnotationFactory(new AttributeNamesDLAnnotation(), AttributeNamesDLAnnotation.attributeNames.class.getSimpleName(), AttributeNamesDLAnnotation.class.getName());

		// Number annotations
		core.defineAnnotationFactory(new RangeDLAnnotation(), RangeDLAnnotation.range.class.getSimpleName(), RangeDLAnnotation.class.getName());
		core.defineAnnotationFactory(new EvenDLAnnotation(), EvenDLAnnotation.even.class.getSimpleName(), EvenDLAnnotation.class.getName());

		// String annotations
		core.defineAnnotationFactory(new LengthDLAnnotation(), LengthDLAnnotation.length.class.getSimpleName(), LengthDLAnnotation.class.getName());
		core.defineAnnotationFactory(new RegexDLAnnotation(), RegexDLAnnotation.regex.class.getSimpleName(), RegexDLAnnotation.class.getName());

		// Comparison annotations
		core.defineAnnotationFactory(new GreaterDLAnnotation(), GreaterDLAnnotation.greater.class.getSimpleName(), GreaterDLAnnotation.class.getName());
		core.defineAnnotationFactory(new GreaterEqualDLAnnotation(), GreaterEqualDLAnnotation.DEFAULT_SYMBOL, GreaterEqualDLAnnotation.class.getName());

		// Mutating annotations
		core.defineAnnotationFactory(new GenerateUUIDDLAnnotation(), GenerateUUIDDLAnnotation.generateUUID.class.getSimpleName(), GenerateUUIDDLAnnotation.class.getName());

	}

	public static void loadPragmas(DLCore core) throws DLException
	{
		assert core != null;

		// Define basic pragmas
		core.definePragma(BASE_PATH_PRAGMA);
		core.definePragma(DEFINE_PRAGMA_PRAGMA);
		core.definePragma(DISABLE_DEFINE_PRAGMAS_PRAGMA);
		core.definePragma(DISABLE_USE_PRAGMAS_PRAGMA);
		core.definePragma(DISABLE_DEFINE_TYPES_PRAGMA);
		core.definePragma(DISABLE_DEFINE_ANNOTATIONS_PRAGMA);
		core.definePragma(DISABLE_REQUIRE_PRAGMA);
		core.definePragma(DISABLE_USE_ASSERTS_PRAGMA);
		core.definePragma(LOG_PRAGMA);
	}

	public static void loadTypes(BaseDLCore core) throws DLException
	{
		assert core != null;

		// Define basic simple types
		DLType objectType = core.defineType(new ObjectDLType(),
			"java.lang.Object",
			ObjectDLType.class.getName()
		);

		// Number types
		DefaultDLType numberType = (DefaultDLType) core.defineType(new NumberDLType(objectType),
			"java.lang.Number",
			NumberDLType.class.getName());

		core.defineType(new DoubleDLType(numberType),
			"java.lang.Double",
			"double",
			DoubleDLType.class.getName());

		core.defineType(new FloatDLType(numberType),
			"java.lang.Float",
			"float",
			FloatDLType.class.getName());

		core.defineType(new IntegerDLType(numberType),
			"java.lang.Integer",
			"int",
			IntegerDLType.class.getName());

		core.defineType(new BooleanDLType(numberType),
			"java.lang.Boolean",
			"boolean",
			"bool",
			BooleanDLType.class.getName());

		core.defineType(new LongDLType(numberType),
			"java.lang.Long",
			"long",
			LongDLType.class.getName());

		core.defineType(new ByteDLType(numberType),
			"java.lang.Byte",
			"byte",
			ByteDLType.class.getName());

		core.defineType(new ShortDLType(numberType),
			"java.lang.Short",
			"short",
			ShortDLType.class.getName());

		core.defineType(new CharDLType(objectType),
			"java.lang.Character",
			"Char",
			"char",
			CharDLType.class.getName());

		core.defineType(new StringDLType(objectType),
			"java.lang.String",
			"string",
			"str",
			StringDLType.class.getName());

		// Base Types
		// The specific generic types will be generated automatically in BaseDLCore.getType(String name, List<DLType> genericTypes)
		core.defineType(new ArrayDLType(objectType),
			"java.lang.Array",
			ArrayDLType.class.getName()
		);

		core.defineType(new PathDLType(objectType),
			"java.nio.file.Path",
			"sun.nio.fs.WindowsPath",
			PathDLType.class.getName()
		);

		core.defineType(new ClassDLType(objectType),
			"java.lang.Class",
			ClassDLType.class.getName()
		);

		core.defineType(new SymbolDLType(objectType),
			SymbolDLType.class.getName());

		core.defineType(new UUIDDLType(objectType),
			"java.util.UUID",
			"uuid",
			UUIDDLType.class.getName());

		core.defineType(new DateDLType(objectType),
			"java.util.Date",
			"java.sql.Timestamp",
			DateDLType.class.getName());

		// Base classes to types
		core.defineType(DLEntity.class, "DLEntity");

		// Define log types
		core.defineType(LogLevel.class);
		core.defineType(Logger.class);
		core.defineType(LogManager.class);

		// Define List types https://github.com/studio42gmbh/dl/issues/10
		// The specific generic types will be generated automatically in BaseDLCore.getType(String name, List<DLType> genericTypes)
		core.defineType(new ListDLType(objectType),
			"java.util.List",
			"java.util.ArrayList",
			"java.util.LinkedList",
			"java.util.Collections$UnmodifiableList",
			ListDLType.class.getName()
		);

		// Define Map types https://github.com/studio42gmbh/dl/issues/11
		// The specific generic types will be generated automatically in BaseDLCore.getType(String name, List<DLType> genericTypes)
		core.defineType(new MapDLType(objectType),
			"java.util.Map",
			"java.util.HashMap",
			"java.util.Collections$UnmodifiableMap",
			"de.s42.base.collections.MapHelper$MapN",
			"java.util.Collections$CheckedMap",
			MapDLType.class.getName()
		);

		// Define Set types https://github.com/studio42gmbh/dl/issues/24
		// The specific generic types will be generated automatically in BaseDLCore.getType(String name, List<DLType> genericTypes)
		core.defineType(new SetDLType(objectType),
			"java.util.Set",
			"java.util.HashSet",
			"java.util.Collections$UnmodifiableSet",
			"java.util.ImmutableCollections$SetN",
			SetDLType.class.getName());
	}
}
