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
package de.s42.dl.annotations;

import de.s42.dl.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Benjamin Schiller
 */
public abstract class AbstractDLAnnotated implements DLAnnotated
{

	protected String name;
	protected final List<DLAnnotation> annotations = new ArrayList<>();

	@Override
	public void addAnnotation(DLAnnotation annotation)
	{
		assert annotation != null;

		annotations.add(annotation);
	}

	@Override
	public  <AnnotationType extends DLAnnotation> Optional<AnnotationType> getAnnotation(Class<AnnotationType> type)
	{
		assert type != null;

		for (DLAnnotation annotation : annotations) {
			if (type.isAssignableFrom(annotation.getClass())) {
				return Optional.of((AnnotationType)annotation);
			}
		}

		return Optional.empty();
	}

	@Override
	public Optional<DLAnnotation> getAnnotation(String name)
	{
		assert name != null;

		for (DLAnnotation annotation : annotations) {
			if (name.equals(annotation.getName())) {
				return Optional.of(annotation);
			}
		}

		return Optional.empty();
	}

	@Override
	public <AnnotationType extends DLAnnotation> List<AnnotationType> getAnnotations(Class<AnnotationType> type)
	{
		assert type != null;

		List<AnnotationType> result = new ArrayList<>();

		for (DLAnnotation annotation : annotations) {
			if (type.isAssignableFrom(annotation.getClass())) {
				result.add((AnnotationType)annotation);
			}
		}

		return result;
	}

	@Override
	public List<DLAnnotation> getAnnotations(String name)
	{
		assert name != null;

		List<DLAnnotation> result = new ArrayList<>();

		for (DLAnnotation annotation : annotations) {
			if (name.equals(annotation.getName())) {
				result.add(annotation);
			}
		}

		return result;
	}

	@Override
	public <AnnotationType extends DLAnnotation> boolean hasAnnotation(Class<AnnotationType> type)
	{
		assert type != null;

		for (DLAnnotation annotation : annotations) {
			if (type.isAssignableFrom(annotation.getClass())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean hasAnnotation(String name)
	{
		assert name != null;

		for (DLAnnotation annotation : annotations) {
			if (name.equals(annotation.getName())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean hasAnnotations()
	{
		return !annotations.isEmpty();
	}

	@Override
	public List<DLAnnotation> getAnnotations()
	{
		return Collections.unmodifiableList(annotations);
	}

	@Override
	public void removeAnnotation(DLAnnotation annotation)
	{
		
	}

	@Override
	public void removeAllAnnotations()
	{
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
}
