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

import de.s42.dl.*;
import de.s42.base.strings.StringHelper;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Benjamin Schiller
 */
public class DefaultDLAttribute implements DLAttribute
{

	protected String name;
	protected Object defaultValue;
	protected DLType type;
	protected final List<DLMappedAnnotation> annotations = new ArrayList<>();
	protected final List<DLAttributeValidator> validators = new ArrayList<>();
	protected boolean readable = true;
	protected boolean writable = true;

	public DefaultDLAttribute()
	{

	}

	public DefaultDLAttribute(String name, DLType type)
	{
		assert name != null;
		assert type != null;

		this.name = name;
		this.type = type;
	}

	public DefaultDLAttribute(String name, DLType type, String defaultValue)
	{
		assert name != null;
		assert type != null;

		this.name = name;
		this.type = type;
		try {
			this.defaultValue = type.read(defaultValue);
		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void validate() throws InvalidAttribute
	{
		for (DLAttributeValidator validator : validators) {
			validator.validate(this);
		}
	}

	public void addValidator(DLAttributeValidator validator)
	{
		assert validator != null;

		validators.add(validator);
	}

	public List<DLAttributeValidator> getValidators()
	{
		return Collections.unmodifiableList(validators);
	}

	public void addAnnotation(DLAnnotation annotation, Object... parameters)
	{
		assert annotation != null;

		DLMappedAnnotation mapped = new DLMappedAnnotation(annotation, parameters);

		annotations.add(mapped);
	}

	@Override
	public Optional<DLMappedAnnotation> getAnnotation(Class<? extends DLAnnotation> annotationType)
	{
		assert annotationType != null;

		for (DLMappedAnnotation annotation : annotations) {
			if (annotationType.isAssignableFrom(annotation.getAnnotation().getClass())) {
				return Optional.of(annotation);
			}
		}

		return Optional.empty();
	}

	@Override
	public boolean hasAnnotation(Class<? extends DLAnnotation> annotationType)
	{
		assert annotationType != null;

		for (DLMappedAnnotation annotation : annotations) {
			if (annotationType.isAssignableFrom(annotation.getAnnotation().getClass())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public List<DLMappedAnnotation> getAnnotations()
	{
		return Collections.unmodifiableList(annotations);
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
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return StringHelper.toString(this);
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
}
