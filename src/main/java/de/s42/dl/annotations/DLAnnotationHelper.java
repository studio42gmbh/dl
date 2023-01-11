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

import de.s42.dl.DLAnnotation;
import de.s42.dl.DLAnnotationFactory;
import de.s42.dl.DLCore;
import de.s42.dl.exceptions.DLException;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

	private final static Logger log = LogManager.getLogger(DLAnnotationHelper.class.getName());

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

	public static List<DLAnnotation> createIfDLAnnotationContainer(DLCore core, Annotation javaAnnotation, DLAnnotated container) throws DLException
	{
		assert javaAnnotation != null;

		try {

			// Check if the given java annotation is a tagged dl annotation
			if (!javaAnnotation.annotationType().isAnnotationPresent(DLAnnotationContainerType.class)) {
				return Collections.EMPTY_LIST;
			}

			List<DLAnnotation> result = new ArrayList<>();

			Annotation[] annotations = (Annotation[]) javaAnnotation.annotationType().getMethod("value").invoke(javaAnnotation);

			for (Annotation ann : annotations) {

				Optional<DLAnnotation> optDlAnnotation = createIfDLAnnotation(core, ann, container);

				if (optDlAnnotation.isPresent()) {
					result.add(optDlAnnotation.orElseThrow());
				}
			}

			return result;
		} catch (DLException | IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
			throw new DLException("Error getting dl annotation - " + ex.getMessage(), ex);
		}
	}

	public static Optional<DLAnnotation> createIfDLAnnotation(DLCore core, Annotation javaAnnotation, DLAnnotated container) throws DLException
	{
		assert javaAnnotation != null;

		try {

			// Check if the given java annotation is a tagged dl annotation
			if (!javaAnnotation.annotationType().isAnnotationPresent(DLAnnotationType.class)) {
				return Optional.empty();
			}

			String annotationName = javaAnnotation.annotationType().getSimpleName();

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
			
			Optional<DLAnnotationFactory> optAnnotationFactory = core.getAnnotationFactory(annotationName);
			
			if (optAnnotationFactory.isPresent()) {
				
				Object[] flatParameters = optAnnotationFactory.orElseThrow().toFlatParameters(namedParameters);

				DLAnnotation dlAnnotation = core.createAnnotation(annotationName, container, flatParameters);

				return Optional.of(dlAnnotation);
			}
			else {
				throw new DLException("Annotationfactory '" + annotationName + "' not found");
			}
		} catch (DLException | IllegalAccessException | IllegalArgumentException | SecurityException | InvocationTargetException ex) {
			throw new DLException("Error getting dl annotation - " + ex.getMessage(), ex);
		}
	}

	public static List<DLAnnotation> createDLAnnotations(DLCore core, Annotation[] javaAnnotations, DLAnnotated container) throws DLException
	{
		List<DLAnnotation> result = new ArrayList<>();

		for (Annotation javaAnnotation : javaAnnotations) {

			Optional<DLAnnotation> optDlAnnotation = createIfDLAnnotation(core, javaAnnotation, container);

			if (optDlAnnotation.isPresent()) {
				result.add(optDlAnnotation.orElseThrow());
			}

			List<DLAnnotation> annotationsFromContainer = createIfDLAnnotationContainer(core, javaAnnotation, container);

			for (DLAnnotation annotation : annotationsFromContainer) {
				result.add(annotation);
			}
		}

		return result;
	}

	public static List<DLAnnotation> createDLAnnotations(DLCore core, Class annotatedClass, DLAnnotated container) throws DLException
	{
		return createDLAnnotations(core, annotatedClass.getAnnotations(), container);
	}
}
