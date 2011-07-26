package com.scit.sling.test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.adapter.AdapterFactory;

/**
 * A Mock Adaptable that can be easily setup
 */
public class MockAdapter {
	private final Map<Class<?>, Object> adaptables;
	private Collection<AdapterFactory> factories = Collections.emptyList();

	public MockAdapter() {
		adaptables = new HashMap<Class<?>, Object>();
	}

	public MockAdapter(Map<Class<?>, Object> adaptables, Collection<AdapterFactory> factories) {
		this.adaptables = adaptables;
		this.factories = factories;
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

	@SuppressWarnings("unchecked")
	public <AdapterType> AdapterType adaptTo(Object adaptable, Class<AdapterType> type) {
		AdapterType val;
		for (AdapterFactory factory : factories) {
			val = factory.getAdapter(adaptable, type);
			if (val != null) {
				return val;
			}
		}
		val = (AdapterType) adaptables.get(type);
		return val;
	}
}