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
package de.s42.dl.types.collections;

import de.s42.base.conversion.ConversionHelper;
import de.s42.dl.DLInstance;
import de.s42.dl.DLType;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidType;
import de.s42.dl.exceptions.InvalidValue;
import de.s42.dl.types.SimpleDLType;
import static de.s42.dl.validation.DefaultValidationCode.InvalidGenericParameters;
import de.s42.dl.validation.ValidationResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 *
 *
 * @author Benjamin Schiller
 */
// https://github.com/studio42gmbh/dl/issues/10 List support
public class SetDLType extends SimpleDLType
{

	public final static String DEFAULT_SYMBOL = "Set";

	public SetDLType()
	{
		this(DEFAULT_SYMBOL, null);
	}

	public SetDLType(DLType parent)
	{
		this(DEFAULT_SYMBOL, null);

		addParent(parent);
	}

	public SetDLType(DLType parent, DLType genericType)
	{
		this(DEFAULT_SYMBOL, genericType);

		addParent(parent);
	}

	public SetDLType(String name, DLType genericType)
	{
		super(name);

		init(genericType);
	}

	private void init(DLType genericType)
	{
		setAllowGenericTypes(true);
		//setComplexType(true);

		if (genericType != null) {
			try {
				addGenericType(genericType);
			} catch (InvalidType ex) {
				throw new RuntimeException("This should not happen as setAllowGenericTypes was just called - " + ex, ex);
			}
		}
	}

	@Override
	public DLInstance fromJavaObject(Object value) throws DLException
	{
		if (!(value instanceof Set)) {
			throw new InvalidValue("value has to be instanceof Set");
		}

		DLInstance instance = core.createInstance(this);

		// @todo properly handle generic and complex elements
		Set converted = new HashSet<>();

		for (Object el : (Set) value) {

			Optional<DLType> optElType = core.getType(el.getClass());

			if (optElType.isPresent()) {
				DLType elType = optElType.orElseThrow();

				if (elType.isComplexType()) {
					converted.add(core.convertFromJavaObject(el));
				} else {
					converted.add(el);
				}
			} else {
				converted.add(el);
			}
		}

		instance.set(SimpleDLType.ATTRIBUTE_VALUE, converted);

		return instance;
	}

	@Override
	public Object read(Object... sources) throws InvalidType
	{
		assert sources != null;

		// Validate read
		validateRead(sources);

		Set result;

		int size = getGenericTypes().size();
		if (size > 0) {

			if (size != 1) {
				throw new InvalidType("may contain either 0 or 1 generic types");
			}

			Class type = getSetValueType();

			if (sources.length == 1 && Set.class.isAssignableFrom(sources[0].getClass())) {
				// Ensure the list contains only allowed data
				Set data = Collections.checkedSet(new HashSet(), type);
				data.addAll((Set) sources[0]);
				return data;
			}

			result = ConversionHelper.convertSet(sources, type);
		} else {

			if (sources.length == 1 && Set.class.isAssignableFrom(sources[0].getClass())) {
				return sources[0];
			}

			result = new HashSet(Arrays.asList(sources));
		}

		return result;
	}

	@Override
	public Object createJavaInstance() throws InvalidType
	{
		if (isGenericType()) {

			Class valueType = getSetValueType();

			return Collections.checkedSet(
				new HashSet<>(),
				valueType
			);
		} else {
			return new HashSet<>();
		}
	}

	@Override
	public Class getJavaDataType()
	{
		return Set.class;
	}

	public Class getSetValueType() throws InvalidType
	{
		if (!isGenericType()) {
			return Object.class;
		}

		return getGenericTypes().get(0).getJavaDataType();
	}

	@Override
	public void addGenericType(DLType genericType) throws InvalidType
	{
		assert genericType != null;

		if (getGenericTypes().size() >= 1) {
			throw new InvalidType("may only contain 1 generic types");
		}

		super.addGenericType(genericType);
	}

	@Override
	public boolean validate(ValidationResult result)
	{
		boolean valid = super.validate(result);

		int count = getGenericTypes().size();
		if (count != 0 && count != 1) {
			result.addError(InvalidGenericParameters.toString(), "May only contain 0 or 1 generic types", this);
			valid = false;
		}

		return valid;
	}
}
