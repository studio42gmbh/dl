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
import de.s42.dl.DLModule;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.nio.file.Path;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class is a preliminary implementation showcasing writing dl to a normalized json format for the instances.
 *
 * @author Benjamin Schiller
 */
// @todo Add the complete json bridge also supporting types, annotations, etc.
public class JsonWriter implements Closeable, Flushable, AutoCloseable
{

	protected final Path file;
	protected final DLCore core;
	protected JSONObject json;

	public JsonWriter(Path file, DLCore core)
	{
		assert file != null;
		assert core != null;

		this.file = file;
		this.core = core;
	}

	public void write(DLModule module) throws IOException
	{
		assert module != null;

		json = writeToJSON(module);
	}

	protected JSONObject writeToJSON(DLInstance instance)
	{
		JSONObject result = new JSONObject();

		if (instance.hasName()) {
			result.put("name", instance.getName());
		}

		result.put("type", instance.getType().getCanonicalName());

		// write attributes
		for (String attributeName : instance.getAttributeNames()) {

			Object value = (Object) instance.get(attributeName);
			
			if (value instanceof DLInstance) {
				result.put(attributeName, writeToJSON((DLInstance)value));
			}
			else {
				result.put(attributeName, value);
			}
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
				children.put(writeToJSON(child));
			}
		}

		return result;
	}

	@Override
	public void close() throws IOException
	{
		FilesHelper.writeStringToFile(file, json.toString(1));
	}

	@Override
	public void flush() throws IOException
	{
		// do nothing
	}

}
