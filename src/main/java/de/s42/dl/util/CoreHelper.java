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
package de.s42.dl.util;

import de.s42.base.beans.BeanHelper;
import de.s42.base.beans.BeanInfo;
import de.s42.base.beans.BeanProperty;
import de.s42.dl.exceptions.InvalidType;
import de.s42.dl.exceptions.InvalidAttribute;
import de.s42.dl.*;
import de.s42.dl.types.DefaultDLType;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.util.Optional;

/**
 *
 * @author Benjamin Schiller
 */
public final class CoreHelper
{

	private final static Logger log = LogManager.getLogger(CoreHelper.class.getName());

	private CoreHelper()
	{
	}

	@SuppressWarnings("UseSpecificCatch")
	public static void validateCoreJavaTypes(DLCore core) throws InvalidType, InvalidAttribute
	{
		int validatedTypes = 0;
		int validatedAttributes = 0;

		for (DLType type : core.getTypes()) {

			if (((DefaultDLType) type).getJavaDataType() != null) {
				String className = ((DefaultDLType) type).getJavaDataType().getName();
				Class typeInstanceClass = null;

				if (className != null) {
					try {
						typeInstanceClass = Class.forName(className);

						// instantiate an instance of that type if it is not abstract and not an enum
						if (!type.isAbstract() && !(type instanceof DLEnum)) {
							
							// @todo this may cause wild side effects
							//Object instance = typeInstanceClass.getConstructor().newInstance();

							//log.debug("Validated type " + type.getCanonicalName() + " is java class " + instance.getClass().getName());
						} else {
							//log.debug("Validated abstract type " + type.getCanonicalName() + " is java class " + typeInstanceClass.getName());
						}

						// validate if class is of all the classes referenced by the super types
						final Class fTypeInstanceClass = typeInstanceClass;
						type.getParents().forEach((DLType parent) -> {

							if (((DefaultDLType) parent).getJavaDataType() != null) {
								String parentClassName = ((DefaultDLType) type).getJavaDataType().getName();

								try {
									Class typeParentClass = Class.forName(parentClassName);

									if (!typeParentClass.isAssignableFrom(fTypeInstanceClass)) {
										throw new RuntimeException("Error validating type "
											+ type.getCanonicalName() + " Class "
											+ typeParentClass.getName() + " is not assignable from "
											+ fTypeInstanceClass.getName() + " (perhaps add @java?)");
									}

									//log.debug("Validated type parent " + typeParentClass.getName());
								} catch (Throwable ex) {
									throw new RuntimeException("Error validating type " + type.getCanonicalName() + " - " + ex.getMessage(), ex);
								}
							}
						});

						// validate if all attributes are reflected correclty in java class
						if (!type.isAbstract()) {

							BeanInfo info = BeanHelper.getBeanInfo(typeInstanceClass);

							for (DLAttribute attribute : type.getAttributes()) {

								String attributeName = attribute.getName();
								Class attributeType = attribute.getType().getJavaDataType();

								Optional<BeanProperty> optProperty = info.getProperty(attributeName);

								if (optProperty.isEmpty()) {
									throw new RuntimeException("Attribute " + attributeName
										+ " is not defined in " + typeInstanceClass.getName());
								}

								BeanProperty property = optProperty.orElseThrow();

								//log.debug("Validating attribute " + attributeName + " " + attributeType.getName());
								if (attribute.isReadable()) {

									if (!property.canRead()) {
										throw new RuntimeException("Attribute " + attributeName
											+ " has no read method in java object " + typeInstanceClass.getName());
									}

									/*
									if (!property.getPropertyClass().isPrimitive() && !attributeType.isAssignableFrom(property.getPropertyClass())) {
										throw new RuntimeException("Attribute " + attributeName
											+ " has an invalid read type in java object - is " + property.getPropertyClass().getName()
											+ " but should be " + attributeType.getName());
									}
									*/
								}

								if (attribute.isWritable()) {

									if (!property.canWrite()) {
										throw new InvalidAttribute("Attribute " + attributeName
											+ " has no write method in java object " + typeInstanceClass.getName());
									}

									/*
									if (!property.getPropertyClass().isPrimitive() && !attributeType.isAssignableFrom(property.getPropertyClass())) {
										throw new InvalidAttribute("Attribute " + attributeName
											+ " has an invalid write type in java object - is " + property.getPropertyClass().getName()
											+ " but should be " + attributeType.getName());
									}
									*/
								}

								validatedAttributes++;
							}
						}
					} catch (Throwable ex) {
						throw new InvalidType("Error validating type " + type.getCanonicalName() + " " + typeInstanceClass + " - " + ex.getMessage(), ex);
					}
				}
			}

			validatedTypes++;
		}

		log.debug("Successfully validated " + validatedTypes + " types and " + validatedAttributes + " attributes");
	}
}
