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
package de.s42.dl.instances;

import de.s42.dl.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Benjamin Schiller
 */
public class DefaultDLModule extends DefaultDLInstance implements DLModule
{

	protected final List<DLType> definedTypes = new ArrayList<>();

	public static String createShortName(String moduleName)
	{
		assert moduleName != null;

		String shortName = moduleName;

		int index = shortName.lastIndexOf(File.separatorChar);

		if (index == -1) {
			index = shortName.lastIndexOf('/');
		}

		// No separators in path
		if (index == -1) {
			return shortName;
		}

		return shortName.substring(index + 1);
	}

	public DefaultDLModule(DLType type)
	{
		super(type);
	}

	public DefaultDLModule(DLType type, String name)
	{
		super(type, name);
	}

	@Override
	public String getShortName()
	{
		return createShortName(getName());
	}

	@Override
	public List<DLType> getDefinedTypes()
	{
		return Collections.unmodifiableList(definedTypes);
	}

	@Override
	public boolean addDefinedType(DLType type)
	{
		assert type != null;

		return definedTypes.add(type);
	}

	@Override
	public Optional<DLType> getDefinedType(String typeName)
	{
		assert typeName != null;

		return definedTypes
			.stream()
			.filter((filterType) -> {
				return filterType.getName().equals(typeName);
			})
			.findFirst();
	}
}
