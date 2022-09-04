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
import de.s42.dl.core.DefaultCore;
import de.s42.dl.types.ClassDLType;
import de.s42.dl.types.DateDLType;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.util.Date;
import org.json.JSONObject;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 *
 * @author Benjamin Schiller
 */
public class JsonWriterTest
{
	private final static Logger log = LogManager.getLogger(JsonWriterTest.class.getName());
	
	public JsonWriterTest()
	{
	}

	@Test
	public void testWriteSimpleClassValue() throws Exception
	{
		DLCore core = new DefaultCore();
		
		Class data = String.class;
		
		JSONObject json = JsonWriter.toJSON(core, data);
		
		Assert.assertEquals(json.getString("type"), ClassDLType.DEFAULT_SYMBOL);
		Assert.assertEquals(json.getString("value"), data.getName());
		
		log.debug(json.toString(2));
	}
	
	@Test
	public void testWriteSimpleDateValue() throws Exception
	{
		DLCore core = new DefaultCore();
		
		Date data = new Date();
		
		JSONObject json = JsonWriter.toJSON(core, data);
		
		Assert.assertEquals(json.getString("type"), DateDLType.DEFAULT_SYMBOL);
		Assert.assertEquals(json.getLong("value"), data.getTime());
		
		log.debug(json.toString(2));
	}
}
