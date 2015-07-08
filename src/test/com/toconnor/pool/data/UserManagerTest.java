package com.toconnor.pool.data;

import java.util.UUID;

import com.toconnor.pool.PoolTestCase;
import com.toconnor.pool.model.User;
import org.junit.Test;

/**
 * UserManagerTest
 */
public class UserManagerTest extends PoolTestCase
{
	@Test
	public void testGetMissingUser() throws Exception
	{
		User user = UserManager.getExistingUser("not a real user");
		assertNull(user);
	}

	@Test
	public void testGetExistingUser() throws Exception
	{
		String token = UUID.randomUUID().toString();

		User user = UserManager.getExistingUser(token);
		assertNull(user);

		user = new User();
		user.setDisplayName("UserManager Test");
		user.setFirstName("UserManager");
		user.setLastName("Test");
		user.setEmail("usermanagertest@toconnor.com");
		user.setKey(token);

		UserManager.saveUser(user);

		User test = UserManager.getExistingUser(token);
		assertNotNull(test);
		assertEquals(user, test);
	}
}
