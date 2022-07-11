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

import de.s42.base.files.FilesHelper;
import de.s42.dl.DLAnnotated.DLMappedAnnotation;
import de.s42.dl.DLCore;
import de.s42.dl.DLInstance;
import de.s42.dl.DLPragma;
import de.s42.dl.DLType;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.io.DLWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

	protected final Path file;
	protected final DLCore core;
	protected final List<JSONObject> json = new ArrayList<>();

	public JsonWriter(Path file, DLCore core)
	{
		assert file != null;
		assert core != null;

		this.file = file;
		this.core = core;
	}

	@Override
	public void write(DLPragma pragma) throws IOException
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void write(DLType type) throws IOException
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void write(DLInstance instance) throws IOException
	{
		assert instance != null;

		json.add(toJSON(instance));
	}

	@Override
	public void write(Object instance) throws IOException
	{
		assert instance != null;

		try {
			write(core.convertFromJavaObject(instance));
		} catch (DLException ex) {
			throw new IOException("Error writing - " + ex.getMessage(), ex);
		}
	}

	protected static Object convert(Object value)
	{
		if (value instanceof DLInstance) {
			return toJSON((DLInstance) value);
		} else if (value instanceof Collection) {

			List list = new ArrayList();

			for (Object el : (Collection) value) {

				list.add(convert(el));
			}

			return list;
		}

		return value;
	}
	
	public static JSONObject toJSON(DLCore core, Object object) throws DLException
	{
		return toJSON(core.convertFromJavaObject(object));
	}
	
	public static JSONObject toJSON(DLInstance instance)
	{
		JSONObject result = new JSONObject();

		if (instance.hasName()) {
			result.put("name", instance.getName());
		}

		result.put("type", instance.getType().getCanonicalName());

		// write attributes
		for (String attributeName : instance.getAttributeNames()) {

			result.put(
				attributeName,
				convert(instance.get(attributeName))
			);
		}

		// @todo write dynamic attributes with types
		// write annotations
		if (instance.hasAnnotations()) {
			JSONArray annotations = new JSONArray();
			result.put("annotations", annotations);

			for (DLMappedAnnotation annotation : instance.getAnnotations()) {

				JSONObject ann = new JSONObject();
				annotations.put(ann);

				ann.put("name", annotation.getAnnotation().getName());

				Object[] params = annotation.getParameters();

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

		// write children
		if (instance.hasChildren()) {

			JSONArray children = new JSONArray();
			result.put("children", children);

			for (DLInstance child : instance.getChildren()) {
				children.put(toJSON(child));
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
		// do nothing
	}
}
