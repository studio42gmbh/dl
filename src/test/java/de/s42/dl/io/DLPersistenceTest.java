// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 *
 * Copyright 2024 Studio 42 GmbH ( https://www.s42m.de ).
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
package de.s42.dl.io;

import de.s42.base.strings.StringHelper;
import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.DLModule;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.types.DLContainer;
import de.s42.dl.util.DLHelper;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class DLPersistenceTest
{

	private final static Logger log = LogManager.getLogger(DLPersistenceTest.class.getName());

	public enum TestDataOptions
	{
		OptionA, OptionB, OptionC
	}

	public static class TestChildData implements DLContainer<TestChildData>
	{

		protected String name;

		protected boolean booleanValue;

		protected String stringValue;

		@AttributeDL(ignore = true)
		protected final List<TestChildData> children;

		public TestChildData()
		{
			children = new ArrayList<>();
		}

		public boolean isBooleanValue()
		{
			return booleanValue;
		}

		public void setBooleanValue(boolean booleanValue)
		{
			this.booleanValue = booleanValue;
		}

		public String getStringValue()
		{
			return stringValue;
		}

		public void setStringValue(String stringValue)
		{
			this.stringValue = stringValue;
		}

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		@Override
		public void addChild(String name, TestChildData child)
		{
			assert child != null : "child != null";

			this.children.add(child);
		}

		@Override
		public List<TestChildData> getChildren()
		{
			return Collections.unmodifiableList(children);
		}

		@Override
		public int hashCode()
		{
			int hash = 7;
			hash = 59 * hash + Objects.hashCode(this.name);
			hash = 59 * hash + (this.booleanValue ? 1 : 0);
			hash = 59 * hash + Objects.hashCode(this.stringValue);
			hash = 59 * hash + Objects.hashCode(this.children);
			return hash;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final TestChildData other = (TestChildData) obj;
			if (this.booleanValue != other.booleanValue) {
				return false;
			}
			if (!Objects.equals(this.name, other.name)) {
				return false;
			}
			if (!Objects.equals(this.stringValue, other.stringValue)) {
				return false;
			}
			return Objects.equals(this.children, other.children);
		}
	}

	public static class TestData implements DLContainer<TestChildData>
	{

		protected boolean booleanValue;

		protected String stringValue;

		protected int intValue;

		protected long longValue;

		protected float floatValue;

		protected double doubleValue;

		protected Date dateValue;

		protected TestDataOptions enumValue;

		@AttributeDL(ignore = true)
		protected final List listValue;

		protected final Map<String, Object> mapValue;

		@AttributeDL(ignore = true)
		protected final List<TestChildData> children;

		public TestData()
		{
			listValue = new ArrayList<>();
			mapValue = new HashMap<>();
			children = new ArrayList<>();
		}

		public boolean isBooleanValue()
		{
			return booleanValue;
		}

		public void setBooleanValue(boolean booleanValue)
		{
			this.booleanValue = booleanValue;
		}

		public String getStringValue()
		{
			return stringValue;
		}

		public void setStringValue(String stringValue)
		{
			this.stringValue = stringValue;
		}

		public int getIntValue()
		{
			return intValue;
		}

		public void setIntValue(int intValue)
		{
			this.intValue = intValue;
		}

		public long getLongValue()
		{
			return longValue;
		}

		public void setLongValue(long longValue)
		{
			this.longValue = longValue;
		}

		public float getFloatValue()
		{
			return floatValue;
		}

		public void setFloatValue(float floatValue)
		{
			this.floatValue = floatValue;
		}

		public double getDoubleValue()
		{
			return doubleValue;
		}

		public void setDoubleValue(double doubleValue)
		{
			this.doubleValue = doubleValue;
		}

		public Date getDateValue()
		{
			return dateValue;
		}

		public void setDateValue(Date dateValue)
		{
			this.dateValue = dateValue;
		}

		public Object[] getListAsArray()
		{
			return listValue.toArray();
		}

		public void setListAsArray(Object[] listData)
		{
			assert listData != null : "listData != null";

			this.listValue.clear();
			this.listValue.addAll(Arrays.asList(listData));
		}

		public List getListValue()
		{
			return Collections.unmodifiableList(listValue);
		}

		public void setListValue(List listValue)
		{
			assert listValue != null : "listValue != null";

			this.listValue.clear();
			this.listValue.addAll(listValue);
		}

		public TestDataOptions getEnumValue()
		{
			return enumValue;
		}

		public void setEnumValue(TestDataOptions enumValue)
		{
			this.enumValue = enumValue;
		}

		public Map<String, Object> getMapValue()
		{
			return Collections.unmodifiableMap(mapValue);
		}

		public void setMapValue(Map<String, Object> mapValue)
		{
			assert mapValue != null : "mapValue != null";

			log.warn(mapValue);

			this.mapValue.clear();
			this.mapValue.putAll(mapValue);
		}

		@Override
		public void addChild(String name, TestChildData child)
		{
			assert child != null : "child != null";

			this.children.add(child);
		}

		@Override
		public List<TestChildData> getChildren()
		{
			return Collections.unmodifiableList(children);
		}

		@Override
		public int hashCode()
		{
			int hash = 5;
			hash = 59 * hash + (this.booleanValue ? 1 : 0);
			hash = 59 * hash + Objects.hashCode(this.stringValue);
			hash = 59 * hash + this.intValue;
			hash = 59 * hash + (int) (this.longValue ^ (this.longValue >>> 32));
			hash = 59 * hash + Float.floatToIntBits(this.floatValue);
			hash = 59 * hash + (int) (Double.doubleToLongBits(this.doubleValue) ^ (Double.doubleToLongBits(this.doubleValue) >>> 32));
			hash = 59 * hash + Objects.hashCode(this.dateValue);
			hash = 59 * hash + Objects.hashCode(this.enumValue);
			hash = 59 * hash + Objects.hashCode(this.listValue);
			hash = 59 * hash + Objects.hashCode(this.mapValue);
			hash = 59 * hash + Objects.hashCode(this.children);
			return hash;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final TestData other = (TestData) obj;
			if (this.booleanValue != other.booleanValue) {
				return false;
			}
			if (this.intValue != other.intValue) {
				return false;
			}
			if (this.longValue != other.longValue) {
				return false;
			}
			if (Float.floatToIntBits(this.floatValue) != Float.floatToIntBits(other.floatValue)) {
				return false;
			}
			if (Double.doubleToLongBits(this.doubleValue) != Double.doubleToLongBits(other.doubleValue)) {
				return false;
			}
			if (!Objects.equals(this.stringValue, other.stringValue)) {
				return false;
			}
			if (!Objects.equals(this.dateValue, other.dateValue)) {
				return false;
			}
			if (this.enumValue != other.enumValue) {
				return false;
			}
			if (!Objects.equals(this.listValue, other.listValue)) {
				return false;
			}
			if (!Objects.equals(this.mapValue, other.mapValue)) {
				return false;
			}
			return Objects.equals(this.children, other.children);
		}
	}

	@Test
	public void serializeObjectWithDL() throws DLException
	{
		DefaultCore core = new DefaultCore();
		core.defineType(TestDataOptions.class);
		core.defineType(TestChildData.class);
		core.defineType(TestData.class);

		// Thats how you can find out about what DL understood of a type mapping
		log.info("TestData:\n" + DLHelper.describe(core.getType(TestData.class).orElseThrow()));
		log.info("TestChildData:\n" + DLHelper.describe(core.getType(TestChildData.class).orElseThrow()));

		TestData data = new TestData();
		data.setDateValue(new Date());
		data.setDoubleValue(1.3567);
		data.setEnumValue(TestDataOptions.OptionB);
		data.setFloatValue(56.814f);
		data.setIntValue(42);
		data.setLongValue(38);

		// ATTENTION: 42L has to be long as otherwise DL will decode it as long as this is the DL Defaulr
		data.setListValue(List.of("AList", 42L, true));

		data.setMapValue(Map.of(
			"AMap", "AMapValue",
			// ATTENTION: 42L has to be long as otherwise DL will decode it as long as this is the DL Defaulr
			"BMap", 42L,
			"CMap", true
		));
		data.setStringValue("Test");

		TestChildData child1 = new TestChildData();
		child1.setName("child1");
		child1.setBooleanValue(true);
		child1.setStringValue("Child 1");
		data.addChild("child1", child1);

		TestChildData child2 = new TestChildData();
		child2.setName("child2");
		child2.setBooleanValue(false);
		child2.setStringValue("Child 2");
		data.addChild("child2", child2);

		TestChildData child21 = new TestChildData();
		child21.setName("child21");
		child21.setBooleanValue(false);
		child21.setStringValue("Child 2 1");
		child2.addChild("child21", child21);

		String persistedData = DLHelper.toString(core.convertFromJavaObject(data), true, 1);

		// Writing the DL to a file now is easy
		// FilesHelper.writeStringToFile(path, persistedData);
		log.info("TestData serialized:\n" + persistedData);

		DLModule module = core.parse("serializeObjectWithDL", persistedData);

		// Describes but not too well - will be improved
		log.info("TestData module deserialized:\n" + DLHelper.describe(module));

		TestData dataRestored = module.getChildAsJavaObject(TestData.class).orElseThrow();

		log.info("TestData deserialized:\n" + StringHelper.toString(dataRestored));

		assertEquals(data, dataRestored);
	}
}
