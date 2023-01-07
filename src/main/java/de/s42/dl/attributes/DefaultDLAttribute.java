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
import de.s42.base.collections.CollectionsHelper;
import de.s42.dl.*;
import de.s42.dl.annotations.AbstractDLAnnotated;
import de.s42.dl.annotations.attributes.ReadOnlyDLAnnotation;
import de.s42.dl.annotations.attributes.WriteOnlyDLAnnotation;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidAttribute;
import de.s42.dl.validation.DLAttributeValidator;
import de.s42.dl.validation.ValidationResult;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Benjamin Schiller
 */
public class DefaultDLAttribute extends AbstractDLAnnotated implements DLAttribute
{

	private final static Logger log = LogManager.getLogger(DefaultDLAttribute.class.getName());

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
		assert object != null;
		
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
		assert result != null;
		
		for (DLAttributeValidator validator : validators) {
			validator.validate(this, result);
		}

		return result.isValid();
	}

	@Override
	public boolean validateValue(Object value, ValidationResult result)
	{
		assert result != null;
		
		for (DLAttributeValidator validator : validators) {

			validator.validate(this, value, result);
		}

		return result.isValid();
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
		StringBuilder builder = new StringBuilder();

		builder.append(type.getCanonicalName());

		if (name != null) {
			builder.append(" ");
			builder.append(name);
		}

		/*for (DLAnnotation annotation : annotations) {
			builder.append(" ");
			builder.append(annotation.toString());
		}*/
		return builder.toString();
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 41 * hash + Objects.hashCode(this.name);
		hash = 41 * hash + Objects.hashCode(this.defaultValue);
		hash = 41 * hash + Objects.hashCode(this.type);
		hash = 41 * hash + Objects.hashCode(this.container);
		hash = 41 * hash + Objects.hashCode(this.validators);
		hash = 41 * hash + (this.readable ? 1 : 0);
		hash = 41 * hash + (this.writable ? 1 : 0);
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
		if (this.readable != other.readable) {
			return false;
		}
		if (this.writable != other.writable) {
			return false;
		}
		if (!Objects.equals(this.defaultValue, other.defaultValue)) {
			return false;
		}
		if (!Objects.equals(this.type, other.type)) {
			return false;
		}
		if (!Objects.equals(this.container, other.container)) {
			return false;
		}
		return Objects.equals(this.validators, other.validators);
	}

	@Override
	public boolean equalDataType(DLAttribute other)
	{
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (this.readable != other.isReadable()) {
			return false;
		}
		if (this.writable != other.isWritable()) {
			return false;
		}
		if (!Objects.equals(this.defaultValue, other.getDefaultValue())) {
			return false;
		}
		if (!Objects.equals(this.type, other.getType())) {
			return false;
		}
		return CollectionsHelper.listEqualsIgnoreOrder(this.annotations, other.getAnnotations());
	}

	@Override
	public boolean equalOrMoreSpecificDataType(DLAttribute other)
	{
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}

		// May not be not readable if it was before
		if (!this.readable && other.isReadable()) {
			return false;
		}

		// May not be not writable if it was before
		if (!this.writable && other.isWritable()) {
			return false;
		}

		if (!this.type.isDerivedTypeOf(other.getType())) {
			return false;
		}
		return annotationsEqualOrMoreSpecific(this.annotations, other.getAnnotations());
	}

	/**
	 * This method allows to compare annotations of own are more specific than other
	 * This is a very specific method but that should to the trick for now
	 *
	 * @param own
	 * @param other
	 *
	 * @return
	 */
	protected boolean annotationsEqualOrMoreSpecific(List<DLAnnotation> own, List<DLAnnotation> other)
	{

		HashSet<DLAnnotation> setOther = new HashSet<>(other);

		// Remove all annotations of this 
		setOther.removeAll(own);

		Iterator<DLAnnotation> setOtherIt = setOther.iterator();
		while (setOtherIt.hasNext()) {
			DLAnnotation ann = setOtherIt.next();
			if (ann instanceof ReadOnlyDLAnnotation || ann instanceof WriteOnlyDLAnnotation) {
				setOtherIt.remove();
			}
		}

		// Now the other set has to be empty
		if (!setOther.isEmpty()) {
			return false;
		}

		return true;
	}
}
