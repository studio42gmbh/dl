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
import de.s42.dl.exceptions.InvalidType;
import de.s42.dl.exceptions.InvalidAttribute;
import de.s42.dl.*;
import de.s42.dl.types.DefaultDLType;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.lang.reflect.Method;
import java.util.Map;

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

			// @todo DL assuming it is a DefaultType
			if (((DefaultDLType) type).getJavaType() != null) {
				String className = ((DefaultDLType) type).getJavaType().getName();
				Class typeInstanceClass = null;

				if (className != null) {
					try {
						typeInstanceClass = Class.forName(className);

						if (!type.isAbstract()) {
							Object instance = typeInstanceClass.getConstructor().newInstance();

							//log.debug("Validated type " + type.getCanonicalName() + " is java class " + instance.getClass().getName());
						} else {
							//log.debug("Validated abstract type " + type.getCanonicalName() + " is java class " + typeInstanceClass.getName());
						}

						// validate if class is of all the classes referenced by the super types
						final Class fTypeInstanceClass = typeInstanceClass;
						type.getParents().forEach((DLType parent) -> {

							// @todo DL assuming it is a DefaultType
							if (((DefaultDLType) parent).getJavaType() != null) {
								String parentClassName = ((DefaultDLType) type).getJavaType().getName();

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

							Map<String, Method> readProperties = BeanHelper.getReadProperties(typeInstanceClass);
							Map<String, Method> writeProperties = BeanHelper.getWriteProperties(typeInstanceClass);

							for (DLAttribute attribute : type.getAttributes()) {

								String attributeName = attribute.getName();
								Class attributeType = attribute.getType().getJavaDataType();

								//log.debug("Validating attribute " + attributeName + " " + attributeType.getName());
								if (attribute.isReadable()) {

									Method readMethod = readProperties.get(attributeName);

									if (readMethod == null) {
										throw new RuntimeException("Attribute " + attributeName
											+ " has no read method in java object " + typeInstanceClass.getName()
											+ " " + readProperties);
									}

									if (!readMethod.getReturnType().isPrimitive() && !attributeType.isAssignableFrom(readMethod.getReturnType())) {
										throw new RuntimeException("Attribute " + attributeName
											+ " has an invalid read type in java object - is " + readMethod.getReturnType().getName()
											+ " but should be " + attributeType.getName());
									}
								}

								if (attribute.isWritable()) {

									Method writeMethod = writeProperties.get(attributeName);

									if (writeMethod == null) {
										throw new InvalidAttribute("Attribute " + attributeName
											+ " has no write method in java object " + typeInstanceClass.getName()
											+ " " + writeProperties);
									}

									if (!writeMethod.getReturnType().isPrimitive() && !attributeType.isAssignableFrom(writeMethod.getParameterTypes()[0])) {
										throw new InvalidAttribute("Attribute " + attributeName
											+ " has an invalid write type in java object - is" + writeMethod.getReturnType().getName()
											+ " but should be " + attributeType.getName());
									}
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
