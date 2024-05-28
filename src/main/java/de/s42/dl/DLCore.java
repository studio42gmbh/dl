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
package de.s42.dl;

import de.s42.dl.annotations.DLAnnotated;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidPragma;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.exceptions.InvalidType;
import java.util.*;

/**
 * A core is not intended to be used to parse in multiple threads!
 *
 * @author Benjamin Schiller
 */
public interface DLCore extends DLEntity
{

	// CLASLOADING
	ClassLoader getClassLoader();

	void setClassLoader(ClassLoader classLoader);

	//PATHS
	DLPathResolver getPathResolver();

	void setPathResolver(DLPathResolver resolver);

	//REFERENCES
	DLReferenceResolver getReferenceResolver();

	void setReferenceResolver(DLReferenceResolver resolver);

	// MODULES
	DLModule parse(String moduleId) throws DLException;

	DLModule parse(String moduleId, String data) throws DLException;

	DLModule createModule() throws DLException;

	DLModule createModule(String name) throws DLException;

	// INSTANCES
	DLInstance createInstance(DLType type);

	DLInstance createInstance(DLType type, String name);

	DLInstance convertFromJavaObject(Object object) throws DLException;

	Object convertFromInstance(DLInstance instance) throws InvalidInstance;

	// TYPES
	DLType createType();

	DLType createType(String typeName);

	DLType createType(Class<?> typeClass) throws DLException;

	DLType defineAliasForType(String alias, DLType type) throws DLException;

	DLType declareType(String typeName) throws DLException;

	DLType defineType(DLType type, String... aliases) throws DLException;

	boolean hasType(String name);

	boolean hasType(Class javaType);

	Optional<DLType> getType(Class javaType);

	Optional<DLType> getType(String name);

	Optional<DLType> getType(String name, List<DLType> genericTypes);

	List<DLType> getTypes();

	<AnnotationType extends DLAnnotation> List<DLType> getTypes(Class<AnnotationType> annotationType);

	boolean isAllowDefineTypes();

	void setAllowDefineTypes(boolean allowDefineTypes);

	/**
	 * Returns the shortest mapped alias or its own name
	 *
	 * @param type
	 *
	 * @return
	 */
	String getShortestName(DLType type);

	// ENUMS
	DLEnum createEnum();

	DLEnum createEnum(String name);

	DLEnum createEnum(String name, Class<? extends Enum> enumImpl);

	DLEnum createEnum(Class<? extends Enum> enumImpl);

	List<DLEnum> getEnums();

	boolean hasEnum(String name);

	// ATTRIBUTES
	DLAttribute createAttribute(String attributeName, String typeName, DLType container) throws DLException;

	DLAttribute createAttribute(String attributeName, DLType type, DLType container) throws DLException;

	// ANNOTATIONS
	DLAnnotation createAnnotation(String name, DLAnnotated container, Object[] flatParameters) throws DLException;

	DLAnnotationFactory defineAnnotationFactory(DLAnnotationFactory factory, String name, String... aliases) throws DLException;

	DLAnnotationFactory defineAliasForAnnotationFactory(String alias, String name) throws DLException;

	boolean hasAnnotationFactory(String name);

	Optional<DLAnnotationFactory> getAnnotationFactory(String name);

	List<DLAnnotationFactory> getAnnotationFactories();

	boolean isAllowDefineAnnotationFactories();

	void setAllowDefineAnnotationsFactories(boolean allowDefineAnnotationFactories);

	// EXPORTS
	void addExported(DLInstance instance) throws InvalidInstance;

	void addExported(Collection<DLInstance> instances) throws InvalidInstance;

	boolean hasExported(String name);

	List<DLInstance> getExported();

	Optional<DLInstance> getExported(String name);

	Optional<Object> getExportedAsJavaObject(String name);

	<JavaType> Optional<JavaType> getExportedAsJavaObject(String name, Class<JavaType> javaType) throws InvalidType;

	<JavaType> Optional<JavaType> getExportedAsJavaObject(Class<JavaType> javaType) throws InvalidType;

	<JavaType> List<DLInstance> getExportedByJavaType(Class<JavaType> javaType) throws InvalidType;

	<AnnotationType extends DLAnnotation> List<DLInstance> getExported(Class<AnnotationType> annotationType);

	// PRAGMAS
	DLPragma definePragma(DLPragma pragma, String... aliases) throws DLException;

	DLPragma defineAliasForPragma(String alias, DLPragma pragma) throws DLException;

	boolean hasPragma(String name);

	Optional<DLPragma> getPragma(String name);

	List<DLPragma> getPragmas();

	void doPragma(String pragmaName, Object... parameters) throws InvalidPragma;

	boolean isAllowDefinePragmas();

	void setAllowDefinePragmas(boolean allowDefinePragmas);

	boolean isAllowUsePragmas();

	void setAllowUsePragmas(boolean allowUsePragmas);

	// ASSERTS
	boolean isAllowUseAsserts();

	void setAllowUseAsserts(boolean allowUseAsserts);

	// REQUIRE
	boolean isAllowRequire();

	void setAllowRequire(boolean allowRequire);
}
