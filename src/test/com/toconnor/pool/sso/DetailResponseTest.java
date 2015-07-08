package com.toconnor.pool.sso;

import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.toconnor.pool.PoolTestCase;
import org.junit.Test;

/**
 * DetailResponseTest
 */
public class DetailResponseTest extends PoolTestCase
{
	@Test
	public void testGoogleUser() throws Exception
	{
		DetailResponse response = testUser("google");
		assertEquals("toconnor@gmail.com", response.getEmail());
	}

	@Test
	public void testFacebookUser() throws Exception
	{
		DetailResponse response = testUser("facebook");
		assertEquals("toconnor@gmail.com", response.getEmail());
	}

//	@Test
//	public void testWindowsLiveUser() throws Exception
//	{
//		DetailResponse response = testUser("windowslive");
//		assertEquals("msn@toconnor.com", response.getEmail());
//	}

	@Test
	public void testLinkedInUser() throws Exception
	{
		DetailResponse response = testUser("linkedin");
		assertEquals("toconnor@wingspan.com", response.getEmail());
	}

	@Test
	public void testYahooUser() throws Exception
	{
		DetailResponse response = testUser("yahoo");
		assertEquals("oconnmi@yahoo.com", response.getEmail());
	}

	private DetailResponse testUser(String provider) throws Exception
	{
		URL url = Resources.getResource("user-" + provider + ".json");
		String json = Resources.toString(url, Charsets.UTF_8);
		assertNotNull(json);
		assertTrue(json.length() > 0);

		DetailResponse response = new DetailResponse(json);

		assertNotNull(response);
		assertEquals(provider, response.getProvider());
		assertEquals("Ted", response.getFirstName());
		if("yahoo".equals(provider))
		{
			assertEquals("O Connor", response.getLastName());
			assertEquals("Ted O Connor", response.getDisplayName());
		}
		else
		{
			assertEquals("O'Connor", response.getLastName());
			assertEquals("Ted O'Connor", response.getDisplayName());
		}

		return response;
	}
}
