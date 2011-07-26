package com.scit.sling.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.apache.sling.api.adapter.AdapterFactory;
import org.junit.Test;

public class TestMockAdapter {
	@Test
	public void testAdaptable() {
		MockResource r = new MockResource(null, "/somehwere", "whatever");
		r.getMockAdaptable().addAdaptable(String.class, "this was set to be returned");
		assertEquals("this was set to be returned", r.adaptTo(String.class));
		assertEquals(null, r.adaptTo(Integer.class));
	}

	@Test
	public void testAdaptableFactory() {
		MockResource r = new MockResource(null, "/somehwere", "whatever");
		r.getMockAdaptable().setAdapterFactories(Arrays.asList(new AdapterFactory[] { new AdapterFactory() {
				@SuppressWarnings("unchecked")
				@Override
				public <AdapterType> AdapterType getAdapter(Object adaptable, Class<AdapterType> type) {
					assertEquals("Adaptable must be a " + MockResource.class.getSimpleName(), MockResource.class, adaptable.getClass());
					if (type.equals(String.class)) {
						return (AdapterType) "something";
					}
					return null;
				}
			}
		}));
		
		assertEquals("something", r.adaptTo(String.class));
		assertEquals(null, r.adaptTo(Integer.class));
	}

	@Test
	public void testMockResourResolverAdaptability() {
		MockResourceResolver mrr = new MockResourceResolver();
		mrr.getMockAdaptable().addAdaptable(String.class, "this was set to be returned");
		assertEquals("this was set to be returned", mrr.adaptTo(String.class));
		assertEquals(null, mrr.adaptTo(Integer.class));
	}
}
