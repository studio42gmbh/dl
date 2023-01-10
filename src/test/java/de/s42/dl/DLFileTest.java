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
package de.s42.dl;

import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.io.hrf.HrfDLReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.UUID;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class DLFileTest
{

	@Test
	public void validTestSimple() throws IOException, URISyntaxException, DLException
	{
		HrfDLReader reader = new HrfDLReader(Path.of(getClass().getResource("simple.dl").toURI()), new DefaultCore());

		DLModule module = reader.readModule();

		Assert.assertEquals(module.get("firstName"), "Benjamin");
		Assert.assertEquals(module.getString("firstName"), "Benjamin");

		Assert.assertEquals(module.get("lastName"), "Schiller");
		Assert.assertEquals(module.getString("lastName"), "Schiller");

		Assert.assertEquals(module.get("middleName"), "the \"Don\"");
		Assert.assertEquals(module.getString("middleName"), "the \"Don\"");

		Assert.assertEquals((long) module.get("height"), 190L);
		Assert.assertEquals(module.getLong("height"), 190L);

		Assert.assertEquals((double) module.get("score"), -1.45);
		Assert.assertEquals(module.getDouble("score"), -1.45);

		Assert.assertEquals((boolean) module.get("awake"), true);
		Assert.assertEquals(module.getBoolean("awake"), true);
	}

	@Test
	public void validTestTypes() throws IOException, URISyntaxException, DLException
	{
		DLCore core = new DefaultCore();

		DLInstance module = core.parse("de/s42/dl/type.dl");

		Assert.assertEquals(module.get("id"), UUID.fromString("ad232384-4a04-4119-9e95-4753f31e3b09"));

		DLInstance user = module.getChild("test").orElseThrow();
		Assert.assertEquals(user.get("id"), UUID.fromString("ad232384-4a04-4119-9e95-4753f31e3b09"));
		Assert.assertEquals(user.get("name"), "John \"Rambo\" Doe");

		DLInstance user2 = module.getChild(2);
		Assert.assertEquals(user2.get("id"), UUID.fromString("bd232384-4a04-4119-9e95-4753f31e3b09"));
		Assert.assertEquals(user2.get("name"), "John \"Rambo\" Doe 2");

		DLInstance user3 = module.getChild(3);
		Assert.assertEquals(user3.get("id"), UUID.fromString("bd332384-4a04-4119-9e95-4753f31e3b08"));
		Assert.assertEquals(user3.get("name"), "Default Name");
	}

	@Test
	public void validTestAnnotations() throws IOException, URISyntaxException, DLException
	{
		DefaultCore core = new DefaultCore();
		core.getPathResolver().addResolveDirectory(Path.of("./"));

		core.parse("de/s42/dl/annotation.dl");

		DLInstance test = core.getExported("test1").get();

		Assert.assertEquals(test.get("globalName"), "TEST");
	}
}
