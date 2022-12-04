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
package de.s42.dl.attributes;

import de.s42.base.beans.BeanHelper;
import de.s42.base.beans.BeanInfo;
import de.s42.base.beans.BeanProperty;
import de.s42.base.beans.InvalidBean;
import de.s42.dl.*;
import de.s42.base.strings.StringHelper;
import de.s42.dl.annotations.AbstractDLAnnotated;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidAttribute;
import de.s42.dl.validation.DLAttributeValidator;
import de.s42.dl.validation.ValidationResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Benjamin Schiller
 */
public class DefaultDLAttribute extends AbstractDLAnnotated implements DLAttribute
{

	protected Object defaultValue;
	protected DLType type;
	protected DLType container;
	protected final List<DLAttributeValidator> validators = new ArrayList<>();
	protected boolean readable = true;
	protected boolean writable = true;

	public DefaultDLAttribute()
	{

	}

	public DefaultDLAttribute(String name, DLType type, DLType container)
	{
		assert name != null;
		assert type != null;
		assert container != null;

		this.name = name;
		this.type = type;
		this.container = container;
	}

	public DefaultDLAttribute(String name, DLType type, DLType container, String defaultValue)
	{
		assert name != null;
		assert type != null;
		assert container != null;

		this.name = name;
		this.type = type;
		this.container = container;
		try {
			this.defaultValue = type.read(defaultValue);
		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	public <ReturnType> ReturnType getValueFromJavaObject(Object object) throws InvalidAttribute
	{
		try {
			assert object != null;

			BeanInfo<?> info = BeanHelper.getBeanInfo(object.getClass());

			BeanProperty property = info.getProperty(getName()).orElseThrow(() -> {
				return new InvalidAttribute("Attribute '" + getName() + "' is not a valid bean property");
			});

			return (ReturnType) property.read(object);
		} catch (InvalidBean ex) {
			throw new InvalidAttribute("Object is not a valid bean - " + ex.getMessage(), ex);
		}
	}

	public void setValueToJavaObject(Object object, Object value) throws InvalidAttribute
	{
		try {
			assert object != null;

			BeanInfo<?> info = BeanHelper.getBeanInfo(object.getClass());

			BeanProperty property = info.getProperty(getName()).orElseThrow(() -> {
				return new InvalidAttribute("Attribute '" + getName() + "' is not a valid bean property");
			});

			property.write(object, value);
		} catch (InvalidBean ex) {
			throw new InvalidAttribute("Object is not a valid bean - " + ex.getMessage(), ex);
		}
	}

	@Override
	public boolean validate(ValidationResult result)
	{
		boolean valid = true;

		for (DLAttributeValidator validator : validators) {
			valid &= validator.validate(this, result);
		}

		return valid;
	}

	@Override
	public boolean addValidator(DLAttributeValidator validator)
	{
		assert validator != null;

		return validators.add(validator);
	}

	@Override
	public List<DLAttributeValidator> getValidators()
	{
		return Collections.unmodifiableList(validators);
	}

	@Override
	public Object getDefaultValue()
	{
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue)
	{
		this.defaultValue = defaultValue;
	}

	@Override
	public DLType getType()
	{
		return type;
	}

	public void setType(DLType type)
	{
		this.type = type;
	}

	@Override
	public boolean isReadable()
	{
		return readable;
	}

	public void setReadable(boolean readable)
	{
		this.readable = readable;
	}

	@Override
	public boolean isWritable()
	{
		return writable;
	}

	public void setWritable(boolean writable)
	{
		this.writable = writable;
	}

	@Override
	public DLType getContainer()
	{
		return container;
	}

	public void setContainer(DLType container)
	{
		this.container = container;
	}

	@Override
	public String toString()
	{
		return StringHelper.toString(getClass(), getName(),
			new String[]{
				"type",
				"readable",
				"writable",
				"defaultValue",
				"container"
			},
			new Object[]{
				getType(),
				isReadable(),
				isWritable(),
				getDefaultValue(),
				getContainer().getCanonicalName()
			}
		);
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 29 * hash + Objects.hashCode(this.name);
		hash = 29 * hash + Objects.hashCode(this.container);
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DefaultDLAttribute other = (DefaultDLAttribute) obj;
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		return Objects.equals(this.container, other.container);
	}
}
