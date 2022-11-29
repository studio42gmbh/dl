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

import de.s42.base.validation.IsSymbol;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.exceptions.InvalidAnnotation;
import de.s42.dl.*;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.types.DefaultDLType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Benjamin Schiller
 */
public class ContainDLAnnotation extends AbstractDLAnnotation<ContainDLAnnotation>
{

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.TYPE})
	@DLAnnotationType(DontPersistDLAnnotation.class)
	public static @interface contain
	{

		public int min() default 0;

		public int max() default Integer.MAX_VALUE;

		public String contain();
	}

	@DLAnnotationParameter(ordinal = 0, required = true, validation = IsSymbol.class)
	protected String contain;

	@DLAnnotationParameter(ordinal = 1, defaultValue = "0")
	protected int min;

	@DLAnnotationParameter(ordinal = 2, defaultValue = "2147483647")
	protected int max = Integer.MAX_VALUE;

	private ContainDLInstanceValidator validator;

	private class ContainDLInstanceValidator implements DLInstanceValidator
	{

		private DLType containedType;

		private ContainDLInstanceValidator(DLType containedType)
		{
			this.containedType = containedType;
		}

		@Override
		public void validate(DLInstance instance) throws InvalidInstance
		{
			assert instance != null;

			int count = instance.getChildren(containedType).size();

			if (count < min || count > max) {
				throw new InvalidInstance("Instance has to contain type '" + containedType.getCanonicalName() + "' between " + min + " and " + max + " times, but is contained " + count + " times");
			}
		}
	}

	@Override
	public synchronized void bindToType(DLCore core, DLType type) throws DLException
	{
		assert core != null;
		assert type != null;

		// Resolve one validator per annotation instance
		if (validator == null) {
			DLType containedType = core.getType(contain).orElseThrow(() -> {
				return new InvalidAnnotation("Type '" + contain + "' not found in core");
			});
			validator = new ContainDLInstanceValidator(containedType);
		}

		((DefaultDLType) type).addInstanceValidator(validator);
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public int getMin()
	{
		return min;
	}

	public void setMin(int min)
	{
		this.min = min;
	}

	public int getMax()
	{
		return max;
	}

	public void setMax(int max)
	{
		this.max = max;
	}

	public String getContain()
	{
		return contain;
	}

	public void setContain(String contain)
	{
		this.contain = contain;
	}
	//</editor-fold>
}
