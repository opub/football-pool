package com.toconnor.pool;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;

/**
 * PoolTestCase
 */
public abstract class PoolTestCase extends TestCase
{
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	@Before
	public void setUp()
	{
		helper.setUp();
	}

	@After
	public void tearDown()
	{
		helper.tearDown();
	}
}
