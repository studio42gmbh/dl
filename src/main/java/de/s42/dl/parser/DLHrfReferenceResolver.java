// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 * 
 * Copyright 2023 Studio 42 GmbH ( https://www.s42m.de ).
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
package de.s42.dl.parser;

import de.s42.base.beans.BeanHelper;
import de.s42.base.beans.BeanInfo;
import de.s42.base.beans.BeanProperty;
import de.s42.base.beans.InvalidBean;
import de.s42.dl.DLCore;
import de.s42.dl.DLEntity;
import de.s42.dl.DLInstance;
import de.s42.dl.DLReferenceResolver;
import de.s42.dl.exceptions.ParserException;
import de.s42.dl.instances.ComplexTypeDLInstance;
import de.s42.dl.instances.SimpleTypeDLInstance;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author Benjamin Schiller
 */
public class DLHrfReferenceResolver implements DLReferenceResolver
{

	//private final static Logger log = LogManager.getLogger(DLHrfReferenceResolver.class.getName());
	protected Object resolveElement(Object element, String path)
	{
		assert element != null;
		assert path != null;

		// Resolve children or attribute of instances
		if (element instanceof DLInstance dLInstance) {

			// Resolve path as child in instance
			DLInstance child = dLInstance.getChild(path).orElse(null);
			if (child != null) {
				return child;
			}

			// Otherwise resolve path as value in instance
			return dLInstance.get(path);
		}

		// Resolve a map
		if (element instanceof Map map) {
			return map.get(path);
		}

		// A core will be checked for exported instances
		if (element instanceof DLCore dLCore) {

			return dLCore.getExported(path).orElse(null);
		}

		// Resolve bean properties of anything else
		try {
			BeanInfo info = BeanHelper.getBeanInfo(element.getClass());

			// Try to resolve the propery -> otherwise return null;
			BeanProperty property = (BeanProperty) info.getProperty(path).orElse(null);
			if (property == null) {
				return null;
			}

			return property.read(element);

		} catch (InvalidBean ex) {
			return null;
		}
	}

	/**
	 * Does some necessary unwrapping for return values
	 *
	 * @param resolved
	 *
	 * @return
	 */
	protected Optional<Object> finalizeReturn(Object resolved)
	{
		// https://github.com/studio42gmbh/dl/issues/13 Unwrap simple instances
		// @improvement this unwrapping should be done more generic if possible
		if (resolved instanceof SimpleTypeDLInstance simpleTypeDLInstance) {
			return Optional.of(simpleTypeDLInstance.getData());
		} else if (resolved instanceof ComplexTypeDLInstance complexTypeDLInstance) {
			return Optional.of(complexTypeDLInstance.getData());
		}

		return Optional.ofNullable(resolved);
	}

	/**
	 * Tries to resolve the path. If strict is true any invalid syntax will lead to a parser exception otherwise it will
	 * just return Optional.empty()
	 *
	 * @param context
	 * @param path
	 * @param strictOnFirstPathElement
	 *
	 * @return
	 *
	 * @throws ParserException
	 */
	@Override
	public Optional<Object> resolve(DLEntity context, String path, boolean strictOnFirstPathElement) throws ParserException
	{
		assert context != null;
		assert path != null;

		boolean strict = strictOnFirstPathElement;

		// Make sure the path is at least $ + <char>
		int length = path.length();
		if (length < 2) {
			throw new ParserException("Path has to be at least 2 signs long: $ + <char> but is '" + path + "'", 0, length);
		}

		// Make sure the expression starts with $
		if (path.charAt(0) != '$') {
			throw new ParserException("Missing $ at start in '" + path + "'", 0, length);
		}

		// Iterate along the path
		Object currentElement = context;
		int indexFrom = 1;
		int indexTo = path.indexOf('.');
		if (indexTo == -1 && indexFrom < length) {
			indexTo = length;
		}
		while (indexTo > -1) {

			// Needs at least 1 char for current element
			if (indexTo <= indexFrom) {
				if (strict) {
					throw new ParserException("Missing path element in '" + path + "'", indexFrom, indexTo);
				} else {
					return Optional.empty();
				}
			}

			// Check for ? on current element -> Optional resolvment
			if (path.charAt(indexFrom) == '?') {

				indexFrom++;

				// Needs at least 1 char for current element
				if (indexTo <= indexFrom) {
					if (strict) {
						throw new ParserException("Missing path element in '" + path + "'", indexFrom, indexTo);
					} else {
						return Optional.empty();
					}
				}

				currentElement = resolveElement(currentElement, path.substring(indexFrom, indexTo));

				// If null -> return empty
				if (currentElement == null) {
					return Optional.empty();
				}

			} // Has to resolve to something (= not null)
			else {

				currentElement = resolveElement(currentElement, path.substring(indexFrom, indexTo));

				// If null -> throw parser exception
				if (currentElement == null) {
					if (strict) {
						throw new ParserException("Path element '" + path.substring(indexFrom, indexTo) + "' could not get resolved in '" + path + "'", indexFrom, indexTo);
					} else {
						return Optional.empty();
					}
				}
			}

			// Proceed to next path element (fixing also the last path part)
			indexFrom = indexTo + 1;
			indexTo = path.indexOf('.', indexFrom);
			// Make sure sub paths are always treated strict
			strict = true;
			if (indexTo == -1 && indexFrom < length) {
				indexTo = length;
			}
		}

		return finalizeReturn(currentElement);
	}
}
