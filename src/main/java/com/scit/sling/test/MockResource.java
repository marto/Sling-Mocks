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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

/**
 * An mock {@link Resource} that can be adapted to {@link ValueMap} and possibly other adaptables.
 * 
 **/
public class MockResource extends org.apache.sling.commons.testing.sling.MockResource {
	private final Map<Class<?>, Object> adaptables = new HashMap<Class<?>, Object>();
	private Collection<AdapterFactory> factories = Collections.emptyList();

	public MockResource(ResourceResolver resourceResolver, String path,	String resourceType, String resourceSuperType) {
		super(resourceResolver, path, resourceType, resourceSuperType);
	}

	public MockResource(ResourceResolver resourceResolver, String path,	String resourceType) {
		super(resourceResolver, path, resourceType);
	}

	/**
	 * Setup the {@link #adaptTo(Class)} to be able to adapt to a specific target. See also, 
	 * {@link #setAdapterFactories(Collection)} if you want a test a factory instead.
	 * 
	 * @param type the Class object of the target type, such as Node.class
	 * @param target the adapter target that will be returned if the {@link #adaptTo(Class)} is called with type
	 */
	public <AdapterType> void addAdaptable(Class<AdapterType> type, AdapterType target) {
		adaptables.put(type, target);
	}

	/**
	 * Add an {@link AdapterFactory} for custom adaptations
	 * 
	 * @param adapterFactory
	 */
	public void setAdapterFactories(Collection<AdapterFactory> adapterFactory) {
		this.factories = adapterFactory;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
		AdapterType val;
		for (AdapterFactory factory : factories) {
			val = factory.getAdapter(this, type);
			if (val != null) {
				return val;
			}
		}
		val = (AdapterType) adaptables.get(type);
		return val;
	}
}