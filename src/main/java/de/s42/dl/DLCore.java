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
import java.nio.file.Path;
import java.util.*;

/**
 * A core is not intended to be used to parse in multiple threads!
 * @author Benjamin Schiller
 */
public interface DLCore extends DLEntity
{

	public Path getBasePath();

	public ClassLoader getClassLoader();

	// MODULES
	public DLModule parse(String moduleId) throws DLException;

	public DLModule parse(String moduleId, String data) throws DLException;

	public DLModule createModule() throws DLException;

	public DLModule createModule(String name) throws DLException;

	// INSTANCES
	public DLInstance createInstance(DLType type);

	public DLInstance createInstance(DLType type, String name);

	public DLInstance convertFromJavaObject(Object object) throws DLException;

	public Object convertFromInstance(DLInstance instance) throws InvalidInstance;

	// TYPES
	public DLType createType();

	public DLType createType(String typeName);

	public DLType createType(Class<?> typeClass) throws DLException;

	public DLType defineAliasForType(String alias, DLType type) throws DLException;

	public DLType declareType(String typeName) throws DLException;

	public DLType defineType(DLType type, String... aliases) throws DLException;

	public boolean hasType(String name);

	public boolean hasType(Class javaType);

	public Optional<DLType> getType(Class javaType);

	public Optional<DLType> getType(String name);

	public Optional<DLType> getType(String name, List<DLType> genericTypes);

	public List<DLType> getTypes();

	public List<DLType> getTypes(Class<? extends DLAnnotation> annotationType);

	public boolean isAllowDefineTypes();

	public void setAllowDefineTypes(boolean allowDefineTypes);

	// ENUMS
	public DLEnum createEnum();

	public DLEnum createEnum(String name);

	public DLEnum createEnum(String name, Class<? extends Enum> enumImpl);

	public DLEnum createEnum(Class<? extends Enum> enumImpl);

	public List<DLEnum> getEnums();

	public boolean hasEnum(String name);

	// ATTRIBUTES
	public DLAttribute createAttribute(String attributeName, String typeName, DLType container) throws DLException;

	public DLAttribute createAttribute(String attributeName, DLType type, DLType container) throws DLException;

	// ANNOTATIONS
	public DLAnnotation createAnnotation(String name, DLAnnotated container, Object[] flatParameters) throws DLException;

	public DLAnnotationFactory defineAnnotationFactory(DLAnnotationFactory factory, String name, String... aliases) throws DLException;

	public DLAnnotationFactory defineAliasForAnnotationFactory(String alias, String name) throws DLException;

	public boolean hasAnnotationFactory(String name);

	public Optional<DLAnnotationFactory> getAnnotationFactory(String name);

	public List<DLAnnotationFactory> getAnnotationFactories();

	public boolean isAllowDefineAnnotationFactories();

	public void setAllowDefineAnnotationsFactories(boolean allowDefineAnnotationFactories);

	// EXPORTS
	public void addExported(DLInstance instance) throws InvalidInstance;

	public void addExported(Collection<DLInstance> instances) throws InvalidInstance;

	public boolean hasExported(String name);

	public List<DLInstance> getExported();

	public Optional<DLInstance> getExported(String name);

	public List<DLInstance> getExported(Class<? extends DLAnnotation> annotationType);

	// PRAGMAS
	public DLPragma definePragma(DLPragma pragma, String... aliases) throws DLException;

	public DLPragma defineAliasForPragma(String alias, DLPragma pragma) throws DLException;

	public boolean hasPragma(String name);

	public Optional<DLPragma> getPragma(String name);

	public List<DLPragma> getPragmas();

	public void doPragma(String pragmaName, Object... parameters) throws InvalidPragma;

	public boolean isAllowDefinePragmas();

	public void setAllowDefinePragmas(boolean allowDefinePragmas);

	public boolean isAllowUsePragmas();

	public void setAllowUsePragmas(boolean allowUsePragmas);

	// ASSERTS
	public boolean isAllowUseAsserts();

	public void setAllowUseAsserts(boolean allowUseAsserts);
	
	// REQUIRE
	public boolean isAllowRequire();

	public void setAllowRequire(boolean allowRequire);
	
	// CONFIG (intended for tools using cores not inside of DL)
	public Object getConfig(String key);
	
	public Object getConfig(String key, Object defaultValue);	
	
	public Object setConfig(String key, Object value);
	
	public Set<String> getConfigKeys();
}
