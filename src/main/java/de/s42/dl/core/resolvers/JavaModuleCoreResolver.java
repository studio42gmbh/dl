// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 * 
 * Copyright 2023 Studio 42 GmbH ( https://www.s42m.de ).
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
package de.s42.dl.core.resolvers;

import de.s42.dl.DLCore;
import de.s42.dl.DLModule;
import de.s42.dl.core.DLCoreResolver;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidModule;
import de.s42.dl.exceptions.InvalidType;
import de.s42.dl.instances.AbstractJavaDLModule;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Benjamin Schiller
 */
public class JavaModuleCoreResolver implements DLCoreResolver
{

	public final static String LIB_PREFIX = "java:";

	@Override
	public boolean canParse(DLCore core, String moduleId, String data)
	{
		if (moduleId == null) {
			return false;
		}

		// Just parse if prefix is given
		return moduleId.startsWith(LIB_PREFIX);
	}

	@Override
	public String resolveModuleId(DLCore core, String moduleId)
	{
		assert moduleId != null;

		return moduleId.replace(LIB_PREFIX, "");
	}

	@Override
	public String getContent(DLCore core, String resolvedModuleId, String data) throws InvalidModule, IOException
	{
		return "// The module is a binary module of class " + resolvedModuleId;
	}

	@Override
	public DLModule parse(DLCore core, String resolvedModuleId, String data) throws DLException
	{
		assert core != null;
		assert resolvedModuleId != null;

		try {
			AbstractJavaDLModule javaModule = (AbstractJavaDLModule) Class.forName(resolvedModuleId).getConstructor().newInstance();
			javaModule.setName(resolvedModuleId);
			javaModule.setType(core.getType(DLModule.class).orElseThrow(() -> {
				// This should not happen if the core is consistent
				return new InvalidType("Could not resolve type for module");
			}));
			javaModule.load(core);

			return javaModule;
		} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
			throw new InvalidModule("Error loading java module " + resolvedModuleId + " - " + ex.getMessage(), ex);
		}
	}
}
