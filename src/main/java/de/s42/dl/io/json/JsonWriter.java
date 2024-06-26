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
package de.s42.dl.io.json;

import de.s42.base.conversion.ConversionHelper;
import de.s42.base.files.FilesHelper;
import de.s42.dl.DLAnnotation;
import de.s42.dl.DLAttribute;
import de.s42.dl.DLCore;
import de.s42.dl.DLInstance;
import de.s42.dl.DLPragma;
import de.s42.dl.DLType;
import de.s42.dl.annotations.persistence.DontPersistDLAnnotation;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.io.DLWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class is a preliminary implementation showcasing writing dl to a normalized json format for the instances.
 *
 * @author Benjamin Schiller
 */
// @todo Add the complete json bridge also supporting types, annotations, etc.
public class JsonWriter implements DLWriter
{

	//private final static Logger log = LogManager.getLogger(JsonWriter.class.getName());
	protected final Path file;
	protected final DLCore core;
	protected final List<JSONObject> json = new ArrayList<>();

	public JsonWriter(Path file, DLCore core)
	{
		assert file != null : "file != null";
		assert core != null : "core != null";

		this.file = file;
		this.core = core;
	}

	@Override
	public void write(DLPragma pragma) throws IOException
	{
		assert pragma != null : "pragma != null";

		//@todo
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void write(DLType type) throws IOException
	{
		assert type != null : "type != null";

		//@todo
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void write(DLInstance instance) throws IOException
	{
		assert instance != null : "instance != null";

		try {
			json.add(toJSON(core, instance));
		} catch (DLException ex) {
			throw new IOException(ex);
		}
	}

	@Override
	public void write(Object instance) throws IOException
	{
		assert instance != null : "instance != null";

		try {
			write(core.convertFromJavaObject(instance));
		} catch (DLException ex) {
			throw new IOException("Error writing - " + ex.getMessage(), ex);
		}
	}

	protected static Object convert(DLCore core, Object value, boolean useCanonicalNames) throws DLException
	{
		assert core != null : "core != null";

		if (value == null) {
			return null;
		} else if (value instanceof DLInstance dLInstance) {
			return toJSON(core, dLInstance, useCanonicalNames);
		} else if (value.getClass().isArray()) {
			List list = new ArrayList();

			for (Object el : (Object[]) value) {

				list.add(convert(core, el, useCanonicalNames));
			}

			return list;
		} else if (value instanceof Collection collection) {

			List list = new ArrayList();

			for (Object el : collection) {

				list.add(convert(core, el, useCanonicalNames));
			}

			return list;
		} else if (value instanceof Date date) {
			return date.getTime();
		} else if (value instanceof Boolean) {
			return value;
			// This also catches BigDecimal and BigInteger (which may be present when retrieved from JSON Objects etc.)
		} else if (value instanceof Number) {
			return value;
		} else if (value instanceof String) {
			return value;
		}

		// If the value has a mapped type and the type is complex -> convert it through DL
		Optional<DLType> optType = core.getType(value.getClass());
		if (optType.isPresent() && optType.orElseThrow().isComplexType()) {
			return toJSON(core, core.convertFromJavaObject(value), useCanonicalNames);
		}

		return ConversionHelper.convert(value, String.class);
	}

	public static JSONObject toJSON(DLCore core, Object object) throws DLException
	{
		assert core != null : "core != null";
		assert object != null : "object != null";

		return toJSON(core, object, true);
	}

	public static JSONObject toJSON(DLCore core, Object object, boolean useCanonicalNames) throws DLException
	{
		assert core != null : "core != null";
		assert object != null : "object != null";

		return toJSON(core, core.convertFromJavaObject(object), useCanonicalNames);
	}

	public static JSONObject toJSON(DLCore core, DLInstance instance) throws DLException
	{
		assert core != null : "core != null";
		assert instance != null : "instance != null";

		return toJSON(core, instance, true);
	}

	public static JSONObject toJSON(DLCore core, DLInstance instance, boolean useCanonicalNames) throws DLException
	{
		assert core != null : "core != null";
		assert instance != null : "instance != null";

		JSONObject result = new JSONObject();

		if (instance.isNamed()) {
			result.put("name", instance.getName());
		}

		if (useCanonicalNames) {
			result.put("type", instance.getType().getCanonicalName());
		} else {
			result.put("type", instance.getType().getShortestName());
		}

		// Write attributes
		// HINT: if the object contains an attribute "type" it can overwrite the internal type doing that (with its pros and cons)
		for (String attributeName : instance.getAttributeNames()) {

			Object val = instance.get(attributeName);

			DLAttribute attribute = instance.getType().getAttribute(attributeName).orElse(null);

			// Ignore attribute that shall not be persisted (may be null on special types like maps)
			if ((attribute != null) && attribute.hasAnnotation(DontPersistDLAnnotation.class)) {
				continue;
			}

			result.put(
				attributeName,
				convert(core, val, useCanonicalNames)
			);
		}

		// @todo write dynamic attributes with types
		// Write annotations
		if (instance.hasAnnotations()) {
			JSONArray annotations = new JSONArray();
			result.put("annotations", annotations);

			for (DLAnnotation annotation : instance.getAnnotations()) {

				JSONObject ann = new JSONObject();
				annotations.put(ann);

				ann.put("name", annotation.getName());

				Object[] params = annotation.getFlatParameters();

				if (params != null
					&& params.length > 0) {

					JSONArray parameters = new JSONArray();
					result.put("parameters", parameters);

					for (Object parameter : params) {

						parameters.put(parameter);
					}
				}
			}
		}

		// Write children
		if (instance.hasChildren()) {

			JSONArray children = new JSONArray();
			result.put("children", children);

			for (DLInstance child : instance.getChildren()) {
				children.put(toJSON(core, child, useCanonicalNames));
			}
		}

		return result;
	}

	@Override
	public void close() throws IOException
	{
		// Write empty json
		if (json.isEmpty()) {
			FilesHelper.writeStringToFile(file, "{}");
			return;
		}

		// Write 1 object directly to file
		if (json.size() == 1) {
			FilesHelper.writeStringToFile(file, json.get(0).toString(2));
			return;
		}

		// Write multiple objects as array
		JSONArray data = new JSONArray();
		data.putAll(json);
		FilesHelper.writeStringToFile(file, data.toString(2));
	}

	@Override
	public void flush() throws IOException
	{
		// Do nothing
	}
}
