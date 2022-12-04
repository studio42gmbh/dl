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

import de.s42.dl.DLCore;
import de.s42.dl.DLInstance;
import de.s42.dl.DLType;
import de.s42.dl.core.DefaultCore;
import java.io.IOException;
import java.util.Map;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 *
 * @author Benjamin Schiller
 */
public class JsonReaderTest
{

	@Test(enabled = false)
	public void validReadFromJson() throws Exception
	{
		DLCore core = new DefaultCore();

		// Defines a DL type T
		core.parse("DefineType", "type T { String login @length(5, 15); Double score @range(0, 10); Array<String> tags  @required; }");
		DLType type = core.getType("T").orElseThrow();
		JsonReader reader = new JsonReader(core,
			"{"
			+ "\"name\" : \"t\","
			+ "\"type\" : \"T\","
			+ "\"login\" : \"Login\","
			+ "\"score\" : 1.234,"
			+ "\"tags\" : [ \"A\", \"B\", \"C\" ]"
			+ "}");

		// Reads the instance t of DL only type T
		DLInstance t = reader.read();

		// Makes sure all values are in the expected java data format
		Assert.assertEquals(t.getType(), type);
		Assert.assertEquals(t.getName(), "t");
		Assert.assertEquals(t.get("login"), "Login");
		Assert.assertEquals((double) t.get("score"), 1.234);
		Assert.assertEquals(t.get("tags"), new String[]{"A", "B", "C"});
	}

	@Test
	public void validReadFromJsonMap() throws Exception
	{
		DLCore core = new DefaultCore();
		JsonReader reader = new JsonReader(core,
			"{"
			+ "\"type\" : \"Map<String, Double>\","
			+ "\"a\" : 1.234,"
			+ "\"b\" : 2.345,"
			+ "\"c\" : 3.456"
			+ "}");

		// Reads the json into a DL Map<String, Double> and then converts it into its Java data type Map<String, Double>
		Map<String, Double> map = (Map<String, Double>)reader.readObject();

		Assert.assertEquals((double) map.get("a"), 1.234);
		Assert.assertEquals((double) map.get("b"), 2.345);
		Assert.assertEquals((double) map.get("c"), 3, 456);
	}

	@Test(expectedExceptions = IOException.class)
	public void invalidReadFromJsonUndefinedType() throws Exception
	{
		DLCore core = new DefaultCore();
		JsonReader reader = new JsonReader(core,
			"{"
			+ "\"type\" : \"Undefined\""
			+ "}");
		Assert.assertNotNull(reader.read());
	}

	@Test(expectedExceptions = IOException.class)
	public void invalidReadFromJsonInvalidMapValueType() throws Exception
	{
		DLCore core = new DefaultCore();
		JsonReader reader = new JsonReader(core,
			"{"
			+ "\"type\" : \"Map<String, Double>\","
			+ "\"a\" : \"InvalidValue\""
			+ "}");
		Assert.assertNotNull(reader.read());
	}

	@Test(enabled = false, expectedExceptions = IOException.class)
	public void invalidReadFromJsonAnnotationValidation() throws Exception
	{
		DLCore core = new DefaultCore();

		// Defines a DL type T
		core.parse("DefineType", "type T { String login @length(5, 15); }");

		// Instance login is too short for length annotation
		JsonReader reader = new JsonReader(core,
			"{"
			+ "\"name\" : \"t\","
			+ "\"type\" : \"T\","
			+ "\"login\" : \"Log\"" // <- too short
			+ "}");
		Assert.assertNotNull(reader.read());
	}

}
