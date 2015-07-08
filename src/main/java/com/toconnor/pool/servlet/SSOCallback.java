package com.toconnor.pool.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.toconnor.pool.data.UserManager;
import com.toconnor.pool.model.User;
import com.toconnor.pool.sso.DetailResponse;
import com.toconnor.pool.util.Log;
import org.apache.commons.codec.binary.Base64;

import static com.toconnor.pool.util.ErrorHandler.handleError;

/**
 * SSOCallback
 */
public class SSOCallback extends HttpServlet
{
	private static final Log LOG = new Log(SSOCallback.class);

	//TODO POPULATE oneall.com SETTINGS FOR SSO (also see loginWidget.js for related URL)
	private static final String DETAIL_URL = "https://TODO.api.oneall.com/connections/";
	private static final String PUBLIC_KEY = "TODO";
	private static final String PRIVATE_KEY = "TODO";
	private static final String KEY_HEADER = "Basic " + new String(Base64.encodeBase64((PUBLIC_KEY + ":" + PRIVATE_KEY).getBytes()));

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			String token = request.getParameter("connection_token");
			if(token == null || token.length() != 36)
			{
				LOG.info("no token provided");
				response.setStatus(404);
				return;
			}

			//retrieve user details
			User userDetail = requestUserDetails(token);
			User existingUser = UserManager.getExistingUser(userDetail.getKey());

			if(existingUser == null)
			{
				LOG.info(userDetail.fetchFullName() + " registered");
				existingUser = userDetail;
			}
			else
			{
				LOG.info(existingUser.fetchFullName() + " logged in");
			}

			existingUser.setLastAccess(new Date());
			UserManager.saveUser(existingUser);

			UserManager.setCurrentUser(existingUser, request);
		}
		catch(Throwable t)
		{
			handleError("failed to establish user session", t, LOG);
		}

		response.sendRedirect("/");
	}

	private User requestUserDetails(String token)
	{
		try
		{
			URL url = new URL(DETAIL_URL + token + ".json");
			HTTPRequest request = new HTTPRequest(url, HTTPMethod.GET);
			HTTPHeader basicAuthHeader = new HTTPHeader("Authorization", KEY_HEADER);
			request.addHeader(basicAuthHeader);

			HTTPResponse response = URLFetchServiceFactory.getURLFetchService().fetch(request);
			String content = new String(response.getContent());

			DetailResponse detail = new DetailResponse(content);

			User user = new User();

			user.setKey(detail.getKey());
			user.setActive(false);
			user.setAdmin(false);
			user.setPaid(false);
			user.setWinnings(0);
			user.setProvider(detail.getProvider());
			user.setDisplayName(detail.getDisplayName());
			user.setEmail(detail.getEmail());
			user.setFirstName(detail.getFirstName());
			user.setLastName(detail.getLastName());

			return user;
		}
		catch(Throwable t)
		{
			handleError("failed to retrieve remote user details", t, LOG);
			return null;
		}
	}
}
