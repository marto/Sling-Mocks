package com.scit.sling.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.junit.Test;

public class TestMockValueMap {
	private MockValueMap props = new MockValueMap();

	@Test
	public void testNullValues() {
		assertNull(props.get("non-existent", String.class));
	}
	
	@Test
	public void testDateConversion() {
		DateTime now = new DateTime(1299957529130L);
		props.put("some-date", now);
		Calendar dt = now.toCalendar(Locale.getDefault());
		assertEquals(dt, props.get("some-date", Calendar.class));
		assertEquals(dt.getTime(), props.get("some-date", Date.class));
		assertEquals(Long.valueOf(1299957529130L), props.get("some-date", Long.class));
		assertEquals("Sat Mar 12 2011 19:18:49 GMT+0000", props.get("some-date", String.class));
	}
	
	@Test
	public void testDateConversionFromString() {
		DateTime now = new DateTime(1299957529130L);
		props.put("some-date", now.toCalendar(Locale.getDefault()));
		assertEquals("Sat Mar 12 2011 19:18:49 GMT+0000", props.get("some-date", String.class));
		props.put("some-date", now.toCalendar(Locale.getDefault()).getTime());
		assertEquals("Sat Mar 12 2011 19:18:49 GMT+0000", props.get("some-date", String.class));
	}
	
	@Test
	public void testGetDefaultValue() {
		assertEquals("default-value", props.get("something", "default-value"));
	}
	
	@Test
	public void testGetNumerics() {
		props.put("some-int", "999");
		assertEquals(Integer.valueOf(999), props.get("some-int", Integer.class));
		assertEquals(Long.valueOf(999), props.get("some-int", Long.class));
		assertEquals(Float.valueOf(999), props.get("some-int", Float.class));
		assertEquals(Double.valueOf(999), props.get("some-int", Double.class));
		assertEquals(Short.valueOf("999"), props.get("some-int", Short.class));
		assertEquals(new BigDecimal("999"), props.get("some-int", BigDecimal.class));
		
		props.put("some-int", 999);
		assertEquals(Integer.valueOf(999), props.get("some-int", Integer.class));
		assertEquals(Long.valueOf(999), props.get("some-int", Long.class));
		assertEquals(Float.valueOf(999), props.get("some-int", Float.class));
		assertEquals(Double.valueOf(999), props.get("some-int", Double.class));
		assertEquals(Short.valueOf("999"), props.get("some-int", Short.class));
		assertEquals(new BigDecimal("999"), props.get("some-int", BigDecimal.class));
		
		props.put("some-int", 999L);
		assertEquals(Integer.valueOf(999), props.get("some-int", Integer.class));
	}
	
	@Test
	public void testGetArray() {
		props.put("string-array", new String[] {"zero", "one", "two"});
		String[] values = props.get("string-array", new String[0].getClass());
		assertNotNull(values);
		assertEquals(3, values.length);
		assertEquals("zero", values[0]);
		assertEquals("one", values[1]);
		assertEquals("two", values[2]);
	}

	@Test
	public void testArrayConversion() {
		props.put("string-array", new int[] { 0, 1, 2 });
		String[] values = props.get("string-array", new String[0].getClass());
		assertNotNull(values);
		assertEquals(3, values.length);
		assertEquals("0", values[0]);
		assertEquals("1", values[1]);
		assertEquals("2", values[2]);
	}
}
