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
import de.s42.dl.DLEntity;
import de.s42.dl.DLCore;
import de.s42.dl.DLInstance;
import de.s42.dl.DLModule;
import de.s42.dl.DLType;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.exceptions.InvalidType;
import de.s42.dl.instances.DefaultDLModule;
import de.s42.dl.io.DLReader;
import de.s42.dl.validation.ValidationResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Benjamin Schiller
 */
public class JsonReader implements DLReader
{

	protected final DLCore core;
	protected DLModule module;
	protected int offset;

	public JsonReader(DLCore core, Path file) throws IOException
	{
		assert core != null;
		assert file != null;
		assert Files.isRegularFile(file);

		this.core = core;

		String json = FilesHelper.getFileAsString(file).trim();

		init(core, json);
	}

	public JsonReader(DLCore core, String json) throws IOException
	{
		assert core != null;
		assert json != null;

		this.core = core;

		init(core, json);
	}

	private void init(DLCore core, String json) throws IOException
	{
		try {
			// Handle arrays
			if (json.startsWith("[")) {

				module = new DefaultDLModule();

				JSONArray array = new JSONArray(json);

				for (int i = 0; i < array.length(); ++i) {

					module.addChild(fromJSON(core, array.getJSONObject(i)));
				}
			} // Handle object
			else {

				DLInstance instance = fromJSON(core, new JSONObject(json));

				if (instance instanceof DLModule) {
					module = (DLModule) instance;
				} else {
					module = new DefaultDLModule();
					module.addChild(instance);
				}
			}
		} catch (DLException ex) {
			throw new IOException("Eror loading json - " + ex.getMessage(), ex);
		}
	}

	public static DLInstance fromJSON(DLCore core, JSONObject json) throws DLException
	{
		DLInstance result;

		String typeName = json.getString("type");

		Optional<DLType> optType = core.getType(typeName);

		if (optType.isEmpty()) {
			throw new InvalidType("Type " + typeName + " is not mapped");
		}

		DLType type = optType.orElseThrow();

		String instanceName = json.optString("name");

		result = core.createInstance(type, instanceName);

		// Read attributes
		for (String key : json.keySet()) {

			if (key.equals("name") || key.equals("type")) {
				continue;
			}

			Object value = json.get(key);

			// Handle sub objects
			if (value instanceof JSONObject) {
				value = fromJSON(core, (JSONObject) value);
			} // Handle arrays
			else if (value instanceof JSONArray) {
				value = ((JSONArray) value).toList().toArray();
			}

			// Set the attribute and make sure it is converted accordingly
			type.setAttributeFromValue(result, key, value);
		}

		// Make sure the generated instance is valid
		if (!result.validate(new ValidationResult())) {
			throw new InvalidInstance("Error validating newly form JSON created instance " + result.getName());
		}

		return result;
	}

	@Override
	public <DLEntityType extends DLEntity> DLEntityType read() throws IOException
	{
		if (!ready()) {
			throw new IOException("Reader is not ready");
		}

		DLInstance result = module.getChild(offset);

		offset++;

		return (DLEntityType) result;
	}

	@Override
	public DLModule readModule() throws IOException
	{
		return module;
	}

	@Override
	public Object readObject() throws IOException
	{
		return ((DLInstance) read()).toJavaObject();
	}

	@Override
	public boolean ready() throws IOException
	{
		return module != null && offset < module.getChildCount();
	}

	@Override
	public void close() throws IOException
	{
		// do nothing
	}
}
