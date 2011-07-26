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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Locale;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.codehaus.jackson.JsonParseException;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

public class TestMockResource {
	private ResourceNode root;
	private MockResourceResolver repo;

	@Before
	public void setup() throws JsonParseException, IOException {
		this.root = MockResourceFactory.loadRepositoryFromJson("/content/geometrixx/en/products",this.getClass().getResourceAsStream("/example.json"));
		this.repo = MockResourceFactory.buildMockRepository(root);
	}

	@Test
	public void testBasicLoading() throws JsonParseException, IOException {
		Resource r = repo.getResource("/content/geometrixx/en/products");
		assertNotNull(r);
	}
	
	@Test
	public void testAdaptToValueMap() {
		Resource r = repo.getResource("/content/geometrixx/en/products");
		assertNotNull(r);
		ValueMap props = r.adaptTo(ValueMap.class);
		assertNotNull(props);
		assertTrue(props instanceof ValueMap);
	}
	
	@Test
	public void testCalendarAndDateConversion() {
		// Test Calendar Conversion
		Resource r = repo.getResource("/content/geometrixx/en/products");
		ValueMap props = r.adaptTo(ValueMap.class);
		assertTrue(props.get("jcr:created", Calendar.class) instanceof Calendar);
		assertEquals(new DateTime("2011-03-27T17:50:20.000+01:00").toCalendar(Locale.getDefault()), props.get("jcr:created", Calendar.class));

	}
	
	@Test
	public void testGetPath() {
		// Test Path
		assertEquals("/content/geometrixx/en/products", repo.getResource("/content/geometrixx/en/products").getPath());
	}
	
	@Test
	public void testGetChildrenAndGetPath() {
		// Test children
		assertEquals("/content/geometrixx/en/products/jcr:content", repo.getResource("/content/geometrixx/en/products/jcr:content").getPath());
		assertEquals("/content/geometrixx/en/products/jcr:content", repo.getResource("/content/geometrixx/en/products").getChild("jcr:content").getPath());
	}
	
	@Test
	public void testParsingWholeJsonInput() throws IOException {
		assertEquals(getResource("/example.json.dump"), root.fullContent(Integer.MAX_VALUE) + "\n");
	}
	
	@Test
	public void testParsingWholeJsonInputButPrintingOnly2Levels() throws IOException {
		assertEquals(getResource("/example.json.2.dump"), root.fullContent(2) + "\n");
	}
	
	@Test
	public void testBuildMockRepository() throws IOException {
		assertEquals("Expected 166 Children + 1 root node", 167, assertResource(root, 0));
	}

	private int assertResource(ResourceNode node, int count) {
		assertNotNull(repo.getResource(node.getPath()));
		for (ResourceNode child : node.getChildren()) {
			count += assertResource(child, 0);
		}
		return count + 1;
	}
	
	private String getResource(String resource) throws IOException {
		CharArrayWriter out = new CharArrayWriter();
		InputStream in = this.getClass().getResourceAsStream(resource);
		try {
			if (in != null) {
				BufferedReader r = new BufferedReader(new InputStreamReader(in));
				BufferedWriter w = new BufferedWriter(out);
				int c ;
				while ((c = r.read()) != -1) {
					w.write(c);
				}
				w.flush();
				return out.toString();
			}
		} finally {
			in.close();
			out.close();
		}
		throw new FileNotFoundException(resource);
	}
}