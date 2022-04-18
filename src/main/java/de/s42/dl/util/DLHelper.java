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
package de.s42.dl.util;

import de.s42.base.conversion.ConversionHelper;
import de.s42.base.zip.ZipHelper;
import de.s42.dl.*;
import de.s42.dl.DLAnnotated.DLMappedAnnotation;
import de.s42.dl.io.DLWriter;
import de.s42.dl.io.binary.BinaryDLWriter;
import de.s42.dl.io.hrf.HrfDLWriter;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.*;
import java.util.function.Predicate;

/**
 *
 * @author Benjamin Schiller
 */
public final class DLHelper
{

	private final static Logger log = LogManager.getLogger(DLHelper.class.getName());

	public static enum DLFileType
	{
		HRF,
		HRFMIN,
		BIN,
		BINCOMPRESSED
	}

	public static int BIN_SIGNATURE = 0x444C3432;

	private final static Map<Class, Boolean> unescapedTypes = Map.ofEntries(
		new AbstractMap.SimpleEntry<>(boolean.class, true),
		new AbstractMap.SimpleEntry<>(float.class, true),
		new AbstractMap.SimpleEntry<>(double.class, true),
		new AbstractMap.SimpleEntry<>(long.class, true),
		new AbstractMap.SimpleEntry<>(int.class, true),
		new AbstractMap.SimpleEntry<>(Boolean.class, true),
		new AbstractMap.SimpleEntry<>(Float.class, true),
		new AbstractMap.SimpleEntry<>(Double.class, true),
		new AbstractMap.SimpleEntry<>(Long.class, true),
		new AbstractMap.SimpleEntry<>(Integer.class, true)
	);

	private DLHelper()
	{
		// helper is never instanced
	}

	public static String describe(DLType type)
	{
		assert type != null;

		StringBuilder builder = new StringBuilder();

		builder
			.append("Type ")
			.append(type.getName());

		if (type.isAbstract()) {
			builder.append(" abstract");
		}

		if (type.isFinal()) {
			builder.append(" final");
		}

		if (type.isAllowDynamicAttributes()) {
			builder.append(" dynamic");
		}

		builder
			.append("\n");

		for (DLMappedAnnotation mappedAnnotation : type.getAnnotations()) {
			builder
				.append("\tAnnotation ")
				.append(mappedAnnotation.getAnnotation().getName())
				.append(" ")
				.append(Arrays.toString(mappedAnnotation.getParameters()))
				.append("\n");
		}

		for (DLType parent : type.getParents()) {
			builder
				.append("\tParent ")
				.append(parent.getName())
				.append("\n");
		}

		for (DLAttribute attribute : type.getAttributes()) {
			builder
				.append("\tAttribute ")
				.append(attribute.getType().getName())
				.append(" ")
				.append(attribute.getName())
				.append("\n");

			for (DLMappedAnnotation mappedAnnotation : attribute.getAnnotations()) {
				builder
					.append("\t\tAnnotation ")
					.append(mappedAnnotation.getAnnotation().getName())
					.append(" ")
					.append(Arrays.toString(mappedAnnotation.getParameters()))
					.append("\n");
			}
		}

		return builder.toString();
	}

	public static String describe(DLInstance instance)
	{
		assert instance != null;

		StringBuilder builder = new StringBuilder();

		builder
			.append("Instance ")
			.append(instance.getName())
			.append(" of type ")
			.append(instance.getType().getName())
			.append("\n");

		for (DLAttribute attribute : instance.getType().getAttributes()) {
			builder
				.append("\tAttribute ")
				.append(attribute.getType().getName())
				.append(" ")
				.append(attribute.getName())
				.append(" ")
				.append(String.valueOf(instance.get(attribute.getName())))
				.append("\n");
		}

		return builder.toString();
	}

	public static String toString(DLModule module)
	{
		return toString(module, false);
	}

	public static String toString(DLModule module, boolean prettyPrint)
	{
		assert module != null;

		StringBuilder result = new StringBuilder();

		if (prettyPrint) {
			result.append("/**\n * Created at ").append(new Date()).append("\n **/\n");
		}

		for (DLInstance instance : module.getChildren()) {

			if (prettyPrint) {
				result.append("\n");
			}

			result.append(toString(instance, prettyPrint));

			if (prettyPrint) {
				result.append("\n");
			}
		}

		return result.toString();
	}

	public static String toString(DLType type)
	{
		return toString(type, false);
	}

	public static String toString(DLType type, boolean prettyPrint)
	{
		assert type != null;

		StringBuilder result = new StringBuilder();

		// currently simple types can just be defined externally
		if (type.isSimpleType()) {
			result.append("extern ");
		}

		// either final or abstract
		if (type.isFinal()) {
			result.append("final ");
		} else if (type.isAbstract()) {
			result.append("abstract ");
		}

		// name
		result
			.append("type ")
			.append(type.getCanonicalName());

		// type annotations
		for (DLMappedAnnotation annotation : type.getAnnotations()) {
			result
				.append(" @")
				.append(annotation.getAnnotation().getName());

			if (annotation.getParameters().length > 0) {

				result.append("(");

				boolean first = true;
				for (Object parameter : annotation.getParameters()) {
					if (!first) {
						result.append(", ");
					}
					first = false;
					result.append(parameter);
				}

				result.append(")");
			}
		}

		// extends
		if (type.getOwnParents().size() > 0) {
			result
				.append(" extends ");

			boolean first = true;
			for (DLType parent : type.getOwnParents()) {
				if (!first) {
					result.append(", ");
				}
				first = false;
				result.append(parent.getCanonicalName());
			}
		}

		// contains
		if (type.getOwnContainedTypes().size() > 0) {
			result
				.append(" contains ");

			boolean first = true;
			for (DLType contained : type.getOwnContainedTypes()) {
				if (!first) {
					result.append(", ");
				}
				first = false;
				result.append(contained.getCanonicalName());
			}
		}

		// simple type or no attributes - no body
		if (type.isSimpleType() || type.getOwnAttributes().isEmpty()) {
			if (prettyPrint) {
				result.append(";\n");
			} else {
				result.append(";");
			}
		} // complex type
		else {
			if (prettyPrint) {
				result.append("\n{\n");
			} else {
				result.append("{");
			}

			// own attributes
			for (DLAttribute attribute : type.getOwnAttributes()) {

				if (prettyPrint) {
					result.append("\t");
				}
				
				result
					.append(attribute.getType().getCanonicalName())
					.append(" ")
					.append(attribute.getName());

				for (DLMappedAnnotation annotation : attribute.getAnnotations()) {
					result
						.append(" @")
						.append(annotation.getAnnotation().getName());

					if (annotation.getParameters().length > 0) {

						result.append("(");

						boolean first = true;
						for (Object parameter : annotation.getParameters()) {
							if (!first) {
								result.append(", ");
							}
							first = false;
							result.append(parameter);
						}

						result.append(")");
					}
				}

				if (attribute.getDefaultValue() != null) {
					result.append(" : ").append(attribute.getDefaultValue());
				}

				if (prettyPrint) {
					result.append(";\n");
				} else {
					result.append(";");
				}
			}

			if (prettyPrint) {
				result.append("}\n");
			} else {
				result.append("}");
			}
		}

		return result.toString();
	}

	public static String toString(DLInstance instance)
	{
		return toString(instance, false, 1);
	}

	public static String toString(DLInstance instance, boolean prettyPrint)
	{
		return toString(instance, prettyPrint, 1);
	}

	public static String toString(DLInstance instance, boolean prettyPrint, int indent)
	{
		assert instance != null;

		StringBuilder result = new StringBuilder();

		result.append(instance.getType().getName());

		if (instance.getName() != null) {
			result.append(" ");
			result.append(instance.getName());
		}

		if (prettyPrint) {
			result.append(" {\n");
		} else {
			result.append("{");
		}

		for (DLAttribute attribute : instance.getType().getAttributes()) {

			Object value = (Object) instance.get(attribute.getName());

			if (value != null) {

				if (prettyPrint) {
					for (int i = 0; i < indent; ++i) {
						result.append("\t");
					}
				}

				result.append(attribute.getName());

				if (prettyPrint) {
					result.append(" : ");
				} else {
					result.append(":");
				}

				// @todo DL deal in a more generic manner with string conversion of values -> Use DLType.write
				if (value instanceof DLInstance) {
					value = toString((DLInstance) value, prettyPrint, indent + 1);
				} else if (value instanceof Date) {
					value = "\"" + ConversionHelper.DATE_FORMAT.format(value) + "\"";
				} else if (!unescapedTypes.containsKey(value.getClass())) {
					value = "\"" + value + "\"";
				}

				result.append(value);

				if (prettyPrint) {
					result.append(";\n");
				} else {
					result.append(";");
				}
			}
		}

		if (prettyPrint) {
			for (int i = 0; i < indent - 1; ++i) {
				result.append("\t");
			}
		}
		result.append("}");

		return result.toString();
	}

	public static boolean isDLB(Path file)
	{
		assert file != null;
		assert Files.isRegularFile(file);

		int fileSignature = 0;
		try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "r")) {
			fileSignature = raf.readInt();
		} catch (IOException e) {
			// handle if you like
		}

		// see https://en.wikipedia.org/wiki/List_of_file_signatures
		// file signature
		return fileSignature == BIN_SIGNATURE;
	}

	// @todo DL optimize performance of recognition preventing opening the file multiple times
	public static DLFileType recognizeFileType(Path file)
	{
		if (isDLB(file)) {
			return DLFileType.BINCOMPRESSED;
		} else if (ZipHelper.isArchive(file)) {
			return DLFileType.BINCOMPRESSED;
		} else {
			return DLFileType.HRF;
		}
	}

	public static void writeEntityToFile(DLCore core, Path file, Object entity, DLFileType fileType) throws IOException
	{
		assert core != null;
		assert file != null;
		assert entity != null;
		assert fileType != null;

		// Write human readable format
		if (fileType == DLFileType.HRF || fileType == DLFileType.HRFMIN) {
			try (DLWriter writer = new HrfDLWriter(file, core, fileType == DLFileType.HRF)) {
				writer.write(entity);
			}
		} // Write binary format 
		else if (fileType == DLFileType.BIN || fileType == DLFileType.BINCOMPRESSED) {
			try (DLWriter writer = new BinaryDLWriter(file, core, fileType == DLFileType.BINCOMPRESSED)) {
				writer.write(entity);
			}
		}
	}

	public static void writeTypesToFile(DLCore core, Path file, DLFileType fileType) throws IOException
	{
		writeTypesToFile(core, file, fileType, (type) -> {
			return true;
		});
	}

	public static void writeTypesToFile(DLCore core, Path file, DLFileType fileType, Predicate<DLType> filter) throws IOException
	{
		assert core != null;
		assert file != null;
		assert fileType != null;

		if (fileType == DLFileType.HRF || fileType == DLFileType.HRFMIN) {
			try (DLWriter writer = new HrfDLWriter(file, core, fileType == DLFileType.HRF)) {

				for (DLType type : core.getTypes()) {
					if (filter.test(type)) {
						writer.write(type);
					}
				}
			}
		} else {
			throw new IOException("The given file type " + fileType + " is not supported");
		}
	}
}
