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
package de.s42.dl.types;

import de.s42.base.beans.BeanHelper;
import de.s42.base.beans.InvalidBean;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Benjamin Schiller
 * @param <ChildType> allows to define which types of children are allowed in this container
 */
public interface DLContainer<ChildType extends Object>
{

	final static Logger log = LogManager.getLogger(DLContainer.class.getName());

	public void addChild(String name, ChildType child);

	/**
	 * If you want to support serialization of containers children simply add the method getChildren
	 *
	 * @return
	 */
	public List<ChildType> getChildren();

	/**
	 * Allows to find a child by name
	 *
	 * @param name
	 *
	 * @return
	 */
	public default Optional<ChildType> findChildByName(String name)
	{

		List<ChildType> children = getChildren();

		for (ChildType child : children) {
			try {
				if (BeanHelper.readProperty(child, "name", "").equals(name)) {
					return Optional.of(child);
				}
			} catch (InvalidBean ex) {
				log.error(ex, "Invalid bean in children - " + ex.getMessage());
			}
		}

		return Optional.empty();
	}
}
