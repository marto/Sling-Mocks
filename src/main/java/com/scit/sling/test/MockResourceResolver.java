package com.scit.sling.test;

import static org.apache.commons.lang.StringUtils.isEmpty;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;

public class MockResourceResolver extends org.apache.sling.commons.testing.sling.MockResourceResolver {
	private MockAdapter mockAdaptable = new MockAdapter();

	public MockResourceResolver() {
		setSearchPath();
	}

	@Override
	public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
		return mockAdaptable.adaptTo(this, type);
	}

	public MockAdapter getMockAdaptable() {
		return mockAdaptable;
	}

	@Override
	public String[] getSearchPath() {
		return EMPTY;
	}

	/**
	 * Best effort implementation to check on resource type/superType
	 */
	@Override
	public boolean isResourceType(Resource resource, String s) {
		if (resource == null) {
			return false;
		}
		String resourceType = resource.getResourceType();
		if (isEmpty(resourceType) || isEmpty(s)) {
			return false;
		}
		String superType = resource.getResourceSuperType();
		if (StringUtils.equals(superType,s)) {
			return true;
		}
		return false;
	}

	private final String[] EMPTY = new String [0];
}
