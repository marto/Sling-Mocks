package com.scit.sling.test;

public class MockResourceResolver extends org.apache.sling.commons.testing.sling.MockResourceResolver {
	private MockAdaptable mockAdaptable = new MockAdaptable();

	@Override
	public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
		return mockAdaptable.adaptTo(type);
	}

	public MockAdaptable getMockAdaptable() {
		return mockAdaptable;
	}
}
