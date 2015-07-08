package com.toconnor.pool.sso;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toconnor.pool.util.Log;

/**
 * DetailResponse
 */
public class DetailResponse
{
	private static final Log LOG = new Log(DetailResponse.class);

	private static final ObjectMapper mapper = new ObjectMapper();
	static
	{
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
	}

	private final String key;
	private final String userToken;
	private final String displayName;
	private final String firstName;
	private final String lastName;
	private final String email;
	private final String provider;

	public DetailResponse(String json)
	{
		if(json == null || json.length() == 0) throw new IllegalArgumentException("json missing");

		LOG.debug("SSO JSON: " + json);

		Map root;
		try
		{
			root = mapper.readValue(json, Map.class);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("invalid json", e);
		}

		Map response = (Map)root.get("response");
		if(response == null || response.isEmpty()) throw new IllegalArgumentException("response missing");

		Map result = (Map)response.get("result");
		if(result == null || result.isEmpty()) throw new IllegalArgumentException("result missing");

		Map data = (Map)result.get("data");
		if(data == null || data.isEmpty()) throw new IllegalArgumentException("data missing");

		Map user = (Map)data.get("user");
		if(user == null || user.isEmpty()) throw new IllegalArgumentException("user missing");

		userToken = (String)user.get("user_token");
		if(userToken == null || userToken.length() == 0) throw new IllegalArgumentException("user_token missing");

		key = userToken.replaceAll("-", "").substring(0, 20);

		Map identity = (Map)user.get("identity");
		if(identity == null || identity.isEmpty()) throw new IllegalArgumentException("identity missing");

		displayName = (String)identity.get("displayName");
		if(displayName == null || displayName.length() == 0) throw new IllegalArgumentException("displayName missing");

		provider = (String)identity.get("provider");
		if(provider == null || provider.length() == 0) throw new IllegalArgumentException("provider missing");

		Map name = (Map)identity.get("name");
		if(name == null || name.isEmpty()) throw new IllegalArgumentException("name missing");

		firstName = (String)name.get("givenName");
		if(firstName == null || firstName.length() == 0) throw new IllegalArgumentException("givenName missing");

		lastName = (String)name.get("familyName");
		if(lastName == null || lastName.length() == 0) throw new IllegalArgumentException("familyName missing");

		Map emails = (Map)((List)identity.get("emails")).get(0);
		if(emails == null || emails.isEmpty()) throw new IllegalArgumentException("emails missing");

		email = (String)emails.get("value");
		if(email == null || email.length() == 0) throw new IllegalArgumentException("email value missing");
	}

	public String getKey()
	{
		return key;
	}

	public String getUserToken()
	{
		return userToken;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public String getEmail()
	{
		return email;
	}

	public String getProvider()
	{
		return provider;
	}
}
