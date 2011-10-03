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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

import org.apache.sling.api.resource.ValueMap;

/**
 * This class is used to represent a JRC subtree. You can use it directly to create a tree then build a {@link MockResourceResolver}
 * via {@link MockResourceFactory#buildMockRepository(ResourceNode)} or you can use the provided {@link MockResourceFactory#buildRepositoryFromJson(String, java.io.InputStream)}
 * factory methods.<br/>
 * 
 * <h1>Direct Usage</h1>
 * <code>
 * base = new ResourceNode("/content/geometrixx/en", "products");<br>
 * base.addProperty("jcr:createdBy", "system");<br>
 * base.addProperty("jcr:primaryType", "cq:Page");<br>
 * jcrContent = new ResourceNode("base", "jcr:content");<br>
 * base.addChild(jcrContent);<br>
 * jcrContent.addProperty("sling:resourceType", "geometrixx/components/contentpage");<br>
 * jcrContent.addProperty("jcr:createdBy", "system");<br>
 * </code>
 */
public class ResourceNode {
	private final String name;
	private final ResourceNode parent;
	private final String basePath;
	private final LinkedList<ResourceNode> children = new LinkedList<ResourceNode>();
	private final ValueMap properties = new MockValueMap();

	/**
	 * Create a root node that represents a location somewhere on a resource tree.
	 * 
	 * @param basePath the parent path of this node (blank if it's meant to be the root)
	 * @param name the name of this node
	 */
	public ResourceNode(String basePath, String name) {
		this(basePath, null, name);
	}

	public ResourceNode(ResourceNode parent, String name) {
		this(null, parent, name);
	}

	private ResourceNode(String basePath, ResourceNode parent, String name) {
		this.basePath = basePath;
		this.parent = parent;
		this.name = name;
	}

	public LinkedList<ResourceNode> getChildren() {
		return children;
	}

	/**
	 * Add a property node
	 * 
	 * @param name the property name
	 * @param value the value
	 */
	public void addProperty(String name, Object value) {
		properties.put(name, value);
	}

	/**
	 * @return the full "jcr:path" of the resource
	 */
	public String getPath() {
		return (basePath == null ? "" : (basePath + "/")) + (parent == null ? "" : (parent.getPath() + "/")) + name;
	}

	@Override
	public String toString() {
		return getPath() + ", " + properties.get("jcr:primaryType", "");
	}

	/**
	 * @return the whole tree (this node and all sub nodes) as a nicely formatted tree upto depth
	 */
	public String fullContent(int depth) {
		StringBuilder sb = new StringBuilder("{\n");
		toString("\t", sb, depth);
		sb.append("}");
		return sb.toString();
	}

	private void toString(String indent, StringBuilder sb, int depth) {
		sb.append(indent).append("jcr:path").append("= ").append(getPath()).append("\n");
		for (Map.Entry<String,Object> prop : properties.entrySet()) {
			sb.append(indent).append(prop.getKey()).append("= ").append(jsonStringValue(prop.getValue())).append("\n");
		}
		if (depth > 0) {
			depth--;
			if (children.size() > 0) {
				String newIndent = indent + "\t";
				for (ResourceNode child : children) {
					sb.append(indent).append(child.name).append(": {\n");
					child.toString(newIndent, sb, depth);
					sb.append(indent).append("}\n");
				}
			}
		}
	}

	private String jsonStringValue(Object value) {
		if (value == null) {
			return "";
		}
		if (value.getClass().isArray()) {
			return Arrays.asList((Object[])value).toString();
		}
		return value.toString();
	}

	/**
	 * Add a child and possibly another subtree
	 * 
	 * @param node
	 */
	public void addChild(ResourceNode node) {
		children.add(node);
	}

	/**
	 * @return a list of all properties
	 */
	public ValueMap getProperties() {
		return this.properties;
	}
}