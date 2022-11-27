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

import de.s42.dl.DLAnnotated;
import de.s42.dl.DLAnnotated.DLMappedAnnotation;
import de.s42.dl.DLAnnotation;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidAnnotation;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author Benjamin Schiller
 */
public final class DLAnnotationHelper
{

	public static final Set<String> SUPPRESSED_ANNOTATION_ELEMENT_NAMES = new HashSet<>(Arrays.asList(new String[]{
		"equals",
		"hashCode",
		"toString",
		"annotationType"
	}));

	private DLAnnotationHelper()
	{
		// never instantiated
	}

	public static Optional<DLAnnotated.DLMappedAnnotation> getIfDLAnnotation(Annotation javaAnnotation) throws DLException
	{
		assert javaAnnotation != null;

		try {

			DLAnnotationType dlAnnotationType = javaAnnotation.annotationType().getAnnotation(DLAnnotationType.class);

			// Check if the given java annotation is a tagged dl annotation
			if (!javaAnnotation.annotationType().isAnnotationPresent(DLAnnotationType.class)) {
				return Optional.empty();
			}

			Map<String, Object> namedParameters = new HashMap<>();

			// Read values from fixed fields
			for (Field field : javaAnnotation.annotationType().getFields()) {

				if (!SUPPRESSED_ANNOTATION_ELEMENT_NAMES.contains(field.getName())) {
					namedParameters.put(field.getName(), field.get(javaAnnotation));
				}
			}

			// Read values from methods
			for (Method method : javaAnnotation.annotationType().getMethods()) {

				if (!SUPPRESSED_ANNOTATION_ELEMENT_NAMES.contains(method.getName())) {
					namedParameters.put(method.getName(), method.invoke(javaAnnotation));
				}
			}

			// Could add check if the class is derived from DLAnnotation
			DLAnnotation dlAnnotation = (DLAnnotation) dlAnnotationType.value().getConstructor().newInstance();

			for (Map.Entry<String, Object> entry : namedParameters.entrySet()) {
				if (!dlAnnotation.isValidNamedParameter(entry.getKey(), entry.getValue())) {
					throw new InvalidAnnotation("Named parameter " + entry.getKey() + " is not valid - value " + entry.getValue());
				}
			}

			DLMappedAnnotation mappedAnnotation = new DLMappedAnnotation(dlAnnotation, dlAnnotation.toFlatParameters(namedParameters));

			return Optional.of(mappedAnnotation);
		} catch (DLException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
			throw new DLException("Error getting dl annotation - " + ex.getMessage(), ex);
		}
	}

	public static List<DLAnnotated.DLMappedAnnotation> getDLAnnotations(Annotation[] javaAnnotations) throws DLException
	{
		List<DLAnnotated.DLMappedAnnotation> result = new ArrayList<>();

		for (Annotation javaAnnotation : javaAnnotations) {

			Optional<DLAnnotated.DLMappedAnnotation> optDlMappedAnnotation = getIfDLAnnotation(javaAnnotation);

			if (optDlMappedAnnotation.isPresent()) {
				result.add(optDlMappedAnnotation.orElseThrow());
			}
		}

		return result;
	}

	public static List<DLAnnotated.DLMappedAnnotation> getDLAnnotations(Class annotatedClass) throws DLException
	{
		return getDLAnnotations(annotatedClass.getAnnotations());
	}
}
