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

/**
 *
 * @author Benjamin Schiller
 */
public class RequiredOrDLAnnotation extends AbstractDLAnnotation
{

	public final static String DEFAULT_SYMBOL = "requiredOr";

	/*
	private static class RequiredOrDLInstanceValidator implements DLInstanceValidator
	{

		private final DLType type;
		private final List<DLAttribute> attributes = new ArrayList<>();
		private Object[] parameters;

		RequiredOrDLInstanceValidator(DLType type, DLAttribute attribute, Object... parameters)
		{
			assert attribute != null;

			attributes.add(attribute);
			this.type = type;

			this.parameters = parameters;
		}

		@Override
		public void validate(DLInstance instance) throws InvalidInstance
		{
			assert instance != null;

			boolean valid = false;

			if (parameters != null) {

				for (Object parameter : parameters) {

					if (!(parameter instanceof String)) {
						throw new InvalidInstance("Or attribute parameter'" + parameter + "' is required to be a String");
					}

					DLAttribute other = type.getAttribute((String) parameter).orElseThrow();

					attributes.add(other);
				}

				parameters = null;
			}

			for (DLAttribute attribute : attributes) {
				Object val = instance.get(attribute.getName());

				if (val != null) {
					valid = true;
					break;
				}
			}

			if (!valid) {
				String attributeNames = attributes
					.stream()
					.map((t) -> {
						return t.getName();
					})
					.collect(Collectors.joining(", "));
				throw new InvalidInstance("All of the attribute values were null but at least one is required to be not null " + attributeNames);
			}
		}
	}

	public RequiredOrDLAnnotation()
	{
		this(DEFAULT_SYMBOL);
	}

	public RequiredOrDLAnnotation(String name)
	{
		super(name);
	}

	@Override
	public void bindToAttribute(DLCore core, DLType type, DLAttribute attribute, Object... parameters) throws InvalidAnnotation
	{
		assert type != null;
		assert attribute != null;

		if (parameters == null || parameters.length == 0) {
			throw new InvalidAnnotation("has to have parameters");
		}

		((DefaultDLType) type).addInstanceValidator(new RequiredOrDLInstanceValidator(type, attribute, parameters));
	}
	 */
}
