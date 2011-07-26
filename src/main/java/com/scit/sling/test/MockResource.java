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

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

/**
 * An mock {@link Resource} that can be adapted to {@link ValueMap} and possibly other adaptables.
 * 
 **/
public class MockResource extends org.apache.sling.commons.testing.sling.MockResource {
	private MockAdaptable mockAdaptable = new MockAdaptable();

	public MockResource(ResourceResolver resourceResolver, String path,	String resourceType, String resourceSuperType) {
		super(resourceResolver, path, resourceType, resourceSuperType);
	}

	public MockResource(ResourceResolver resourceResolver, String path,	String resourceType) {
		super(resourceResolver, path, resourceType);
	}

	@Override
	public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
		return mockAdaptable.adaptTo(type);
	}

	public MockAdaptable getMockAdaptable() {
		return mockAdaptable;
	}
}