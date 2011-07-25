/**
 * Copyright (c) 2007-2011 Southern Cross IT. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.scit.sling.test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

import org.apache.commons.lang.NotImplementedException;
import org.apache.sling.api.resource.ValueMap;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * A Mock {@link ValueMap} that has some basic type conversion.
 * Dates should be stored as {@link DateTime} objects.
 */
public class MockValueMap extends LinkedHashMap<String, Object> implements ValueMap {
	private static final long serialVersionUID = 1L;

	public <T> T get(String name, Class<T> type) {
		return convertType(get(name), type);
	}

	public <T> T get(String name, T defaultValue) {
		@SuppressWarnings("unchecked")
		T val = (T) get(name, defaultValue.getClass());
		if (val == null) {
			return defaultValue;
		}
		return val;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T convertType(Object o, Class<T> type) {
		if (o == null) {
			return null;
		}
		if (o.getClass().isAssignableFrom(type)) {
			return (T) o;
		}
		if (String.class.isAssignableFrom(type)) {
			// Format dates
			if (o instanceof Calendar) {
				return (T) formatDate((Calendar)o);
			} else if (o instanceof Date) {
				return (T) formatDate((Date)o);
			} else if (o instanceof DateTime) {
				return (T) formatDate((DateTime)o);
			}
			return (T) String.valueOf(o);
		} else if (o instanceof DateTime) {
			DateTime dt = (DateTime)o;
			if (Calendar.class.isAssignableFrom(type)) {
				return (T) dt.toCalendar(Locale.getDefault());
			} else if (Date.class.isAssignableFrom(type)) {
				return (T) dt.toDate();
			} else if (Number.class.isAssignableFrom(type)) {
				return convertType(dt.getMillis(), type);
			}
		} else if (o instanceof Number && Number.class.isAssignableFrom(type)) {
			if (Byte.class.isAssignableFrom(type)) {
				return (T)(Byte)((Number)o).byteValue();
			} else if (Double.class.isAssignableFrom(type)) {
				return (T)(Double)((Number)o).doubleValue();
			} else if (Float.class.isAssignableFrom(type)) {
				return (T)(Float)((Number)o).floatValue();
			} else if (Integer.class.isAssignableFrom(type)) {
				return (T)(Integer)((Number)o).intValue();
			} else if (Long.class.isAssignableFrom(type)) {
				return (T)(Long)((Number)o).longValue();
			} else if (Short.class.isAssignableFrom(type)) {
				return (T)(Short)((Number)o).shortValue();
			} else if (BigDecimal.class.isAssignableFrom(type)) {
				return (T) new BigDecimal(o.toString());
			}
		} else if (o instanceof String && Number.class.isAssignableFrom(type)) {
			if (Byte.class.isAssignableFrom(type)) {
				return (T)new Byte((String)o);
			} else if (Double.class.isAssignableFrom(type)) {
				return (T)new Double((String)o);
			} else if (Float.class.isAssignableFrom(type)) {
				return (T)new Float((String)o);
			} else if (Integer.class.isAssignableFrom(type)) {
				return (T)new Integer((String)o);
			} else if (Long.class.isAssignableFrom(type)) {
				return (T)new Long((String)o);
			} else if (Short.class.isAssignableFrom(type)) {
				return (T)new Short((String)o);
			} else if (BigDecimal.class.isAssignableFrom(type)) {
				return (T) new BigDecimal((String)o);
			}
		}
		throw new NotImplementedException("Can't handle conversion from " + o.getClass().getName() + " to " + type.getName());
	}

	private String formatDate(DateTime o) {
		return DATE_TIME_FORMAT.print(o);
	}

	private String formatDate(Date o) {
		return DATE_TIME_FORMAT.print(new DateTime(o));
	}

	private String formatDate(Calendar o) {
		return DATE_TIME_FORMAT.print(new DateTime(o));
	}

	public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("EEE MMM dd YYYY HH:mm:ss zZ");
}