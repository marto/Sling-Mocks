package com.scit.sling.test;

public class MockResourceResolver extends org.apache.sling.commons.testing.sling.MockResourceResolver {
	private MockAdapter mockAdaptable = new MockAdapter();

	@Override
	public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
		return mockAdaptable.adaptTo(this, type);
	}

	public MockAdapter getMockAdaptable() {
		return mockAdaptable;
	}
}
