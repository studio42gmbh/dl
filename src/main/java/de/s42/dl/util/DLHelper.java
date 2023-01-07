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
import de.s42.base.files.FilesHelper;
import de.s42.base.strings.StringHelper;
import de.s42.base.zip.ZipHelper;
import de.s42.dl.*;
import de.s42.dl.annotations.DLContract;
import de.s42.dl.annotations.persistence.DontPersistDLAnnotation;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.io.DLWriter;
import de.s42.dl.io.binary.BinaryDLWriter;
import de.s42.dl.io.hrf.HrfDLWriter;
import de.s42.dl.language.DLFileType;
import de.s42.dl.parser.contracts.operators.AbstractBinaryContractFactory;
import de.s42.dl.parser.contracts.operators.ContractAnnotationFactory;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
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
		new AbstractMap.SimpleEntry<>(Integer.class, true),
		new AbstractMap.SimpleEntry<>(Rectangle.class, true),
		new AbstractMap.SimpleEntry<>(Insets.class, true),
		new AbstractMap.SimpleEntry<>(Color.class, true),
		new AbstractMap.SimpleEntry<>(Point.class, true),
		new AbstractMap.SimpleEntry<>(Dimension.class, true)
	);

	private DLHelper()
	{
		// helper is never instanced
	}

	public static StringBuilder describe(ContractAnnotationFactory factory, StringBuilder builder)
	{
		assert factory != null;
		assert builder != null;

		builder.append(factory.getOperatorAsString());
		
		DLContract contract = factory.getContract();
		if (contract instanceof ContractAnnotationFactory) {
			describe((ContractAnnotationFactory)contract, builder);
		} else if (contract != null) {
			builder
				.append("@")
				.append(contract.getName())
				.append(Arrays.toString(contract.getFlatParameters()));
		}		
		
		return builder;
	}
			
	public static StringBuilder describe(AbstractBinaryContractFactory factory, StringBuilder builder)
	{
		assert factory != null;
		assert builder != null;
		
		builder.append("(");
		
		// First 
		DLContract first = factory.getContractFirst();
		if (first instanceof AbstractBinaryContractFactory) {
			describe((AbstractBinaryContractFactory)first, builder);
		}
		else if (first instanceof ContractAnnotationFactory) {
			describe((ContractAnnotationFactory)first, builder);
		}
		
		builder.append(factory.getOperatorAsString());
		
		// Second
		DLContract second = factory.getContractSecond();
		if (second instanceof AbstractBinaryContractFactory) {
			describe((AbstractBinaryContractFactory)second, builder);
		}
		else if (second instanceof ContractAnnotationFactory) {
			describe((ContractAnnotationFactory)second, builder);
		}
		
		builder.append(")");
		
		return builder;
	}

	public static String describe(DLAnnotation annotation)
	{
		return describe(annotation, new StringBuilder()).toString();
	}
	
	public static StringBuilder describe(DLAnnotation annotation, StringBuilder builder)
	{
		assert annotation != null;
		assert builder != null;
		
		builder
			.append("Annotation ")
			.append(annotation.getName())
			.append(" ")
			.append(Arrays.toString(annotation.getFlatParameters()));
		
		
		// Special handling for contracts -> reconstruct expression
		if (annotation instanceof AbstractBinaryContractFactory) {
			builder
				.append(" ");
			describe((AbstractBinaryContractFactory)annotation, builder);
		}
		else if (annotation instanceof ContractAnnotationFactory) {
			builder
				.append(" ");
			describe((ContractAnnotationFactory)annotation, builder);
		}

		return builder;		
	}
	
	public static String describe(DLType type)
	{
		assert type != null;

		StringBuilder builder = new StringBuilder();

		builder
			.append("Type ")
			.append(type.getCanonicalName());

		if (type.isAbstract()) {
			builder.append(" abstract");
		}

		if (type.isFinal()) {
			builder.append(" final");
		}

		if (type.isDynamic()) {
			builder.append(" dynamic");
		}

		if (type.isSimpleType()) {
			builder.append(" simple");
		}
		else {
			builder.append(" complex");
		}
				
		builder
			.append("\n");

		for (DLAnnotation annotation : type.getAnnotations()) {
			
			builder
				.append("\t");
			
			describe(annotation, builder);
			
			builder
				.append("\n");
		}

		for (DLType parent : type.getParents()) {
			builder
				.append("\tParent ")
				.append(parent.getCanonicalName())
				.append("\n");
		}

		for (DLType contained : type.getContainedTypes()) {
			builder
				.append("\tContained ")
				.append(contained.getCanonicalName())
				.append("\n");
		}

		for (DLAttribute attribute : type.getAttributes()) {
			builder
				.append("\tAttribute ")
				.append(attribute.getType().getCanonicalName())
				.append(" ")
				.append(attribute.getName());

			if (attribute.getDefaultValue() != null) {

				builder
					.append(" = ")
					.append(attribute.getDefaultValue());
			}

			builder
				.append("\n");

			for (DLAnnotation annotation : attribute.getAnnotations()) {
				builder
					.append("\t\tAnnotation ")
					.append(annotation.getName())
					.append(" ")
					.append(Arrays.toString(annotation.getFlatParameters()))
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
		for (DLAnnotation annotation : type.getAnnotations()) {
			result
				.append(" @")
				.append(annotation.getName());

			if (annotation.hasParameters()) {

				result.append("(");

				boolean first = true;
				for (Object parameter : annotation.getFlatParameters()) {
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
		if (!type.getOwnParents().isEmpty()) {
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
		if (!type.getOwnContainedTypes().isEmpty()) {
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

				for (DLAnnotation annotation : attribute.getAnnotations()) {
					result
						.append(" @")
						.append(annotation.getName());

					if (annotation.hasParameters()) {

						result.append("(");

						boolean first = true;
						for (Object parameter : annotation.getFlatParameters()) {
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

	protected static String singleValueToString(Object value, boolean prettyPrint, int indent)
	{
		if (value == null) {
			return "";
		} else if (value instanceof DLInstance) {
			return toString((DLInstance) value, prettyPrint, indent + 1);
		} else if (value instanceof Date) {
			return "\"" + ConversionHelper.DATE_FORMAT.format(value) + "\"";
		} else if (value.getClass().isArray()) {

			StringBuilder builder = new StringBuilder();

			boolean first = true;
			for (Object val : (Object[]) value) {

				if (!first) {
					if (prettyPrint) {
						builder.append(", ");
					} else {
						builder.append(",");
					}
				}

				builder.append(singleValueToString(val, prettyPrint, indent + 1));

				first = false;
			}

			return builder.toString();
		} else if (!unescapedTypes.containsKey(value.getClass())) {
			return "\"" + StringHelper.escapeJavaString(ConversionHelper.convert(value, String.class)) + "\"";
		} else if (!value.getClass().isPrimitive()) {
			return StringHelper.escapeJavaString(ConversionHelper.convert(value, String.class));
		}

		throw new RuntimeException("Unknown single value");
	}

	public static String toString(DLInstance instance, boolean prettyPrint, int indent)
	{
		assert instance != null;

		StringBuilder result = new StringBuilder();

		result.append(instance.getType().getCanonicalName());

		if (instance.getName() != null) {
			result.append(" ");
			result.append(instance.getName());
		}

		if (prettyPrint) {
			result.append(" {\n");
		} else {
			result.append("{");
		}

		// Make sure the attributes are sorted alphabetically		
		List<String> attributeNames = new ArrayList<>(instance.getAttributeNames());
		Collections.sort(attributeNames);

		// Write out the attributes
		DLType type = instance.getType();
		for (String attributeName : attributeNames) {

			DLAttribute attribute = type.getAttribute(attributeName).orElseThrow();

			// Ignore attribute that shall not be persisted
			if (attribute.hasAnnotation(DontPersistDLAnnotation.class)) {
				continue;
			}

			Object value = (Object) instance.get(attributeName);

			// @todo https://github.com/studio42gmbh/dl/issues/19 Deal in a more generic manner with string conversion of values
			// -> Use DLType.write, Use Stringhelper
			value = singleValueToString(value, prettyPrint, indent);

			if (value != null && !((String) value).isBlank()) {

				if (prettyPrint) {
					for (int i = 0; i < indent; ++i) {
						result.append("\t");
					}
				}

				result.append(attributeName);

				if (prettyPrint) {
					result.append(" : ");
				} else {
					result.append(":");
				}

				result.append(value);

				if (prettyPrint) {
					result.append(";\n");
				} else {
					result.append(";");
				}
			}
		}

		// Children
		if (instance.hasChildren()) {

			for (DLInstance child : instance.getChildren()) {
				if (prettyPrint) {
					result.append("\n");
					for (int i = 0; i < indent; ++i) {
						result.append("\t");
					}
				}

				result.append(toString(child, prettyPrint, indent + 1));

				if (prettyPrint) {
					result.append("\n");
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
		return isDLB(getIntFileSignature(file));
	}

	public static boolean isDLB(int fileSignature)
	{
		return fileSignature == BIN_SIGNATURE;
	}

	// see https://en.wikipedia.org/wiki/List_of_file_signatures
	public static int getIntFileSignature(Path file)
	{
		assert file != null;
		assert Files.isRegularFile(file);

		int fileSignature = 0;
		try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "r")) {
			fileSignature = raf.readInt();
		} catch (IOException e) {
			// handle if you like
		}

		return fileSignature;
	}

	/**
	 * Tries to recognize the filre type first by extension then by signature dword in files. Defaults to HRF otherwise
	 *
	 * @param file
	 *
	 * @return
	 */
	public static DLFileType recognizeFileType(Path file)
	{
		String ext = FilesHelper.getExtension(file).toLowerCase();

		// Default ending .dl to be HRF
		if (ext.equals(DLFileType.HRF.defaultExtension)) {
			return DLFileType.HRF;
		} // Default ending .dlb to be BIN 
		else if (ext.equals(DLFileType.BIN.defaultExtension)) {
			return DLFileType.BIN;
		} // Default ending .dla to be BINCOMPRESSED
		else if (ext.equals(DLFileType.BINCOMPRESSED.defaultExtension)) {
			return DLFileType.BINCOMPRESSED;
		}

		// Try to identify the file using the first 4 bytes as signature from file
		int fileSignature = getIntFileSignature(file);

		if (isDLB(fileSignature)) {
			return DLFileType.BIN;
		} // Attention: All zips will be recognized as BINCOMPRESSED
		else if (ZipHelper.isArchive(fileSignature)) {
			return DLFileType.BINCOMPRESSED;
		} // The default is HRF
		else {
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

	public static <EntityType> EntityType readInstanceFromFile(DLCore core, Path file) throws DLException
	{
		assert core != null;
		assert file != null;

		return (EntityType) core.parse(file.toString()).getChildAsJavaObject(0);
	}
}
