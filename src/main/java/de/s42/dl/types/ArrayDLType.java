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
package de.s42.dl.types;

import de.s42.base.conversion.ConversionHelper;
import de.s42.dl.DLCore;
import de.s42.dl.DLInstance;
import de.s42.dl.DLType;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidType;
import java.util.List;

/**
 *
 * @author Benjamin Schiller
 */
public class ArrayDLType extends DefaultDLType
{

	public final static String DEFAULT_SYMBOL = "Array";

	public ArrayDLType()
	{
		this(DEFAULT_SYMBOL);
	}

	public ArrayDLType(String name)
	{
		super(name);

		allowGenericTypes = true;
	}

	public ArrayDLType(String name, String genericTypeName, DLCore core) throws DLException
	{
		super(name);

		init(genericTypeName, core);
	}

	private void init(String genericTypeName, DLCore core) throws DLException
	{
		setAllowGenericTypes(true);
		addGenericType(core.getType(genericTypeName).orElseThrow());
	}

	@Override
	public Object read(Object... sources) throws InvalidType
	{
		if (sources == null) {
			return new Object[0];
		}

		// Handle if sources[0] is an array
		if (sources.length == 1) {

			if (sources[0].getClass().isArray()) {
			
				if (!isGenericType()) {
					return sources[0];
				} else {

					return ConversionHelper.convert(sources[0], getArrayValueArrayType());
				}
			}
			else if (List.class.isAssignableFrom(sources[0].getClass())) {
				
				sources = ((List)sources[0]).toArray();				
			}
		}

		// If it is not a generic array -> just return the given data
		if (!isGenericType()) {
			return sources;
		}

		Object[] result = new Object[sources.length];

		// validating types
		for (int i = 0; i < sources.length; ++i) {

			Object source = sources[i];

			if (source instanceof DLInstance) {

				DLInstance instance = (DLInstance) source;
				boolean foundType = false;

				//check all generic types
				for (DLType genericType : genericTypes) {

					// if one fits - fine
					if (genericType.isAssignableFrom(instance.getType())) {
						foundType = true;
						break;
					}
				}

				if (!foundType) {
					throw new InvalidType("Type " + instance.getType().getName() + " is not contained in genericTypes");
				}

				//leave instances unchanged for later conversion
				result[i] = source;

			} else {

				// https://github.com/studio42gmbh/dl/issues/9 validate/convert types of contents if it has 1 generic type					
				if (genericTypes.size() == 1) {

					result[i] = ConversionHelper.convert(source, getArrayValueType());
				} // @improvement with multiple generic types can not be dealt with atm
				else if (source != null && getJavaDataType().isAssignableFrom(source.getClass())) {
					throw new InvalidType("Source '" + source + "' is not contained in genericTypes");
				}
			}
		}

		return result;
	}

	public Class getArrayValueType() throws InvalidType
	{
		if (!isGenericType()) {
			return Object.class;
		}

		return getGenericTypes().get(0).getJavaDataType();
	}

	public Class getArrayValueArrayType() throws InvalidType
	{
		return getArrayValueType().arrayType();
	}

	@Override
	public void addGenericType(DLType genericType) throws InvalidType
	{
		assert genericType != null;

		if (getGenericTypes().size() >= 1) {
			throw new InvalidType("may only contain 1 generic types");
		}

		// Set the java type for the type
		setJavaType(genericType.getJavaDataType());

		super.addGenericType(genericType);
	}

	@Override
	public void validate() throws InvalidType
	{
		super.validate();

		int count = getGenericTypes().size();
		if (count != 0 && count != 1) {
			throw new InvalidType("may only contain 0 or 1 generic types");
		}
	}
}
