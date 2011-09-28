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

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Factory for creating {@link MockResource}/{@link MockResourceResolver}.
 */
public class MockResourceFactory {
	/**
	 * Helper factory method that does:<br>
	 * <code>
	 * ResourceNode root = loadRepositoryFromJson(basePath, in);<br>
     * return buildMockRepository(root);
	 * </code>
	 * 
	 * @param basePath the parent path of this node (blank if it's meant to be the root)
	 * @param in the input stream to load the JSON representation
	 * 
	 * @return 
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public static MockResourceResolver buildMockRepositoryJson(String basePath, InputStream in) throws JsonParseException, IOException {
		ResourceNode root = loadRepositoryFromJson(basePath, in);
		return buildMockRepository(root);
	}

	/**
	 * Build a {@link MockResourceResolver} with all {@link MockResource} and {@link MockValueMap} setup from a {@link ResourceNode} that
	 * has possibly been loaded from {@link #loadRepositoryFromJson(String, InputStream)} or built manually<br>
	 * 
	 * @param baseNode
	 * @return
	 */
	public static MockResourceResolver buildMockRepository(ResourceNode baseNode) {
		MockResourceResolver resolver = new MockResourceResolver();
		addResource(baseNode, buildResource(resolver, baseNode), resolver);
		return resolver;
	}

	private static MockResourceResolver addResource(ResourceNode baseNode, MockResource baseResource, MockResourceResolver resolver) {
		LinkedList<Resource> children = new LinkedList<Resource>();
		for (ResourceNode child : baseNode.getChildren()) {
			MockResource childResource = buildResource(resolver, child);
			resolver.addResource(childResource);
			children.add(childResource);
			addResource(child, childResource, resolver);
		}
		resolver.addResource(baseResource);
		resolver.addChildren(baseResource, children);
		return resolver;
	}

	private static MockResource buildResource(MockResourceResolver resolver, ResourceNode node) {
		MockResource r = new MockResource(resolver, node.getPath(), node.getProperties().get("jcr:primaryType", ""));
		r.getMockAdaptable().addAdaptable(ValueMap.class, node.getProperties());
		return r;
	}

	/**
	 * Build a {@link MockResourceResolver} with all {@link MockResource} and {@link MockValueMap}.<br>
	 * The format of the JSON input is compatible with the Apache Sling json representations of the JCR, i.e. 
	 * <blockquote>curl -s -u admin:admin http://localhost:8080/content/path/to/node.infinity.json</blockquote>
	 * 
	 * @param basePath the parent path of this node (blank if it's meant to be the root)
	 * @param in the input stream to load the JSON representation
	 * 
	 * @return 
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public static ResourceNode loadRepositoryFromJson(String basePath, InputStream in) throws JsonParseException, IOException {
		JsonFactory jsonFactory = new JsonFactory();
		JsonParser jp = jsonFactory.createJsonParser(in);
		ResourceNode root = new ResourceNode(StringUtils.substringBeforeLast(basePath, "/"), StringUtils.substringAfterLast(basePath, "/"));
		JsonToken token = jp.nextToken();
		if (token == JsonToken.START_OBJECT) {
			parseJsonObject(root, jp);
		}
		return root;
	}

	private static ResourceNode parseJsonObject(ResourceNode context, JsonParser jp) throws JsonParseException, IOException {
		JsonToken token;
		String fieldname = "";
		while ((token = jp.nextToken()) != JsonToken.END_OBJECT) {
			switch (token) {
			case START_OBJECT:
				ResourceNode child = new ResourceNode(context, fieldname);
				context.addChild(child);
				parseJsonObject(child, jp);
				break;
			case FIELD_NAME:
				fieldname = jp.getCurrentName();
				break;
			case START_ARRAY:
				LinkedList<Object> array = new LinkedList<Object>();
				while ((token = jp.nextToken()) != JsonToken.END_ARRAY) {
					array.add(parseValue(token, jp));
				}
				context.addProperty(fieldname, array);
				break;
			default:
				if (token.isScalarValue()) {
					context.addProperty(fieldname, parseValue(token, jp));
				}
				// log.warnd ???
			}
		}
		return context;
	}

	private static Object parseValue(JsonToken token, JsonParser jp) throws JsonParseException, IOException {
		if (token.isNumeric()) {
			switch(jp.getNumberType()) {
			case INT:
				return jp.getIntValue();
			case LONG:
				return jp.getLongValue();
			case BIG_INTEGER:
				return jp.getBigIntegerValue();
			case FLOAT:
				return jp.getFloatValue();
			case DOUBLE:
				return jp.getDoubleValue();
			case BIG_DECIMAL:
				return jp.getDecimalValue();
			default:
				//log.warn("Don't know how to handle '" + fieldname + "' of type " + jp.getNumberType() + " with value '" + jp.getText() + "'");
				return dateOrText(jp.getText());
			}
		}
		switch (token) {
			case VALUE_FALSE:
			case VALUE_TRUE:
				return jp.getBooleanValue();
			case VALUE_STRING:
				return dateOrText(jp.getText());
			case VALUE_NULL:
				break;
		}
		return dateOrText(jp.getText());
	}

	private static Object dateOrText(String text) {
		try {
			return DATE_TIME_FORMAT.parseDateTime(text.replaceAll(" [A-Z][A-Z][A-Z]([\\+\\-])"," $1"));
		} catch (IllegalArgumentException e) {
			return text;
		}
	}

	public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("EEE MMM dd YYYY HH:mm:ss Z");
}