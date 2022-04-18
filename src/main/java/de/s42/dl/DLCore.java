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

import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidPragma;
import de.s42.dl.exceptions.InvalidInstance;
import java.nio.file.Path;
import java.util.*;

/**
 *
 * @author Benjamin Schiller
 */
public interface DLCore
{

	public DLModule parse(String moduleId) throws DLException;

	public DLModule parse(String moduleId, String data) throws DLException;

	public DLInstance createInstance(DLType type);

	public DLInstance createInstance(DLType type, String name);

	public DLModule createModule();

	public DLModule createModule(String name);

	public DLType createType();

	public DLType createType(String typeName);

	public DLType createType(Class typeClass) throws DLException;

	public DLAnnotation createAnnotation(Class<? extends DLAnnotation> annotationImpl) throws DLException;

	public DLEnum createEnum();

	public DLEnum createEnum(String name);

	public DLEnum createEnum(String name, Class<? extends Enum> enumImpl);

	public DLEnum createEnum(Class<? extends Enum> enumImpl);

	public DLAttribute createAttribute(String attributeName, String typeName);

	public DLAttribute createAttribute(String attributeName, DLType type);

	public DLInstance convertFromJavaObject(Object object) throws DLException;

	public <ObjectType> ObjectType convertFromInstance(DLInstance instance) throws InvalidInstance;

	public DLAnnotation addAnnotationToAttribute(DLType type, DLAttribute attribute, String annotationName, Object... parameters) throws DLException;

	public DLAnnotation addAnnotationToType(DLType type, String annotationName, Object... parameters) throws DLException;

	public DLAnnotation addAnnotationToInstance(DLModule module, DLInstance instance, String annotationName, Object... parameters) throws DLException;

	public DLType defineAliasForType(String alias, DLType type) throws DLException;
	
	public void addExported(DLInstance instance) throws InvalidInstance;

	public void addExported(Collection<DLInstance> instances) throws InvalidInstance;

	public boolean hasExported(String name);

	public List<DLInstance> getExported();

	public Optional<DLInstance> getExported(String name);

	public Object resolveExportedPath(String path);

	// @todo DL will have to solve the basic name handling in types (types have 1 name but can be mapped with different aliases)
	public DLType defineType(DLType type, String... aliases) throws DLException;

	public boolean hasType(String name);

	public boolean hasType(Class javaType);

	public boolean hasEnum(String name);

	public Optional<DLType> getType(Class javaType);

	public Optional<DLType> getType(String name);

	public Optional<DLType> getType(String name, List<DLType> genericTypes) throws DLException;

	public List<DLType> getTypes();

	public List<DLType> getComplexTypes();

	public List<DLType> getSimpleTypes();

	public List<DLEnum> getEnums();

	// @todo DL will have to solve the basic name handling in annotations (annotations have 1 name but can be mapped with different aliases)
	public DLAnnotation defineAnnotation(DLAnnotation annotation, String... aliases) throws DLException;

	public DLAnnotation defineAliasForAnnotation(String alias, DLAnnotation annotation) throws DLException;
	
	public boolean hasAnnotation(String name);

	public Optional<DLAnnotation> getAnnotation(String name);

	public List<DLAnnotation> getAnnotations();

	// @todo DL will have to solve the basic name handling in pragmas (pragmas have 1 name but can be mapped with different aliases)
	public DLPragma definePragma(DLPragma pragma, String... aliases) throws DLException;
	
	public DLPragma defineAliasForPragma(String alias, DLPragma pragma) throws DLException;
	
	public boolean hasPragma(String name);

	public Optional<DLPragma> getPragma(String name);

	public List<DLPragma> getPragmas();

	public void doPragma(String pragmaName, Object... parameters) throws InvalidPragma;

	public Path getBasePath();

	public boolean isAllowDefineTypes();

	public void setAllowDefineTypes(boolean allowDefineTypes);

	public boolean isAllowDefineAnnotations();

	public void setAllowDefineAnnotations(boolean allowDefineAnnotations);

	public boolean isAllowDefinePragmas();

	public void setAllowDefinePragmas(boolean allowDefinePragmas);
}
