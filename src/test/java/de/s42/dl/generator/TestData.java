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
package de.s42.dl.generator;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.annotations.attributes.GreaterDLAnnotation.greater;
import de.s42.dl.exceptions.InvalidType;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 *
 * type TestData @generate(validate, serialize) @typeId("502c4735-d416-43a2-ae2d-43965838ceda")
 * {
 * int min @required : 0;
 * int max @required @greater(min) : 42;
 * int value @required @greaterEqual(min) @lesserEqual(max) : 0;
 * }
 *
 * @author Benjamin Schiller
 */
public class TestData
{

	public final static UUID TYPE_ID = UUID.fromString("502c4735-d416-43a2-ae2d-43965838ceda");

	@AttributeDL(required = true, defaultValue = "0")
	protected int min = 0;

	@AttributeDL(required = true, defaultValue = "42")
	@greater(other = "min")
	protected int max = 42;

	@AttributeDL(required = true, defaultValue = "0")
	@greater(other = "min")
	protected int value = 0;

	public TestData()
	{

	}

	public TestData(int min, int max, int value)
	{
		this.min = min;
		this.max = max;
		this.value = value;
	}

	/* SERIALIZE */
	public void write(DataOutputStream out, boolean withType) throws IOException
	{
		if (withType) {
			out.writeLong(TYPE_ID.getLeastSignificantBits());
			out.writeLong(TYPE_ID.getMostSignificantBits());
		}

		out.writeInt(value);
		out.writeInt(min);
		out.writeInt(max);
	}

	public void read(DataInputStream in, boolean withType) throws IOException, InvalidType
	{
		if (withType) {
			long lsb = in.readLong();
			long msb = in.readLong();

			if (TYPE_ID.getLeastSignificantBits() != lsb
				|| TYPE_ID.getMostSignificantBits() != msb) {

				throw new InvalidType("Type ID does not match - it is '" + new UUID(msb, lsb) + "' but should be '" + TYPE_ID + "'");
			}
		}

		value = in.readInt();
		min = in.readInt();
		max = in.readInt();
	}

	/* VALIDATE */
	public boolean isValid()
	{
		// max @greater("min")
		if (!(max > min)) {
			return false;
		}

		// value @greaterEqual("min")
		if (!(value >= min)) {
			return false;
		}

		// value @lesserEqual("max")
		if (!(value <= max)) {
			return false;
		}

		return true;
	}

	public ValidationResult validate()
	{
		return validate(new ValidationResult());
	}

	public ValidationResult validate(ValidationResult result)
	{
		// max @greater("min")
		if (!(max > min)) {
			result.addInvalid("greater", "max", "max has to be greater than min");
		}

		// value @greaterEqual("min")
		if (!(value >= min)) {
			result.addInvalid("greater", "value", "value has to be greater or equal than min");
		}

		// value @lesserEqual("max")
		if (!(value <= max)) {
			result.addInvalid("greater", "value", "value has to be lesser or equal than max");
		}

		return result;
	}

	/* GETTER / SETTER */
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

	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
	{
		this.value = value;
	}
}
