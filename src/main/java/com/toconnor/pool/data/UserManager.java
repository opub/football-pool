package com.toconnor.pool.data;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

import com.toconnor.pool.model.User;
import com.toconnor.pool.util.Log;
import com.vaadin.server.VaadinService;

import static com.toconnor.pool.util.ErrorHandler.handleError;

/**
 * UserManager
 */
public class UserManager
{
	private static final Log LOG = new Log(UserManager.class);

	private static final String CURRENT_USER = "com.toconnor.pool.data.UserManager.CURRENT_USER";

	public static User getCurrentUser()
	{
		return hasSession() ? (User)VaadinService.getCurrentRequest().getWrappedSession().getAttribute(CURRENT_USER) : null;
	}

	public static void setCurrentUser(User user, HttpServletRequest request)
	{
		request.getSession().setAttribute(CURRENT_USER, user);
	}

	public static void updateCurrentUser(User user)
	{
		if(hasSession())
		{
			VaadinService.getCurrentRequest().getWrappedSession().setAttribute(CURRENT_USER, user);
		}
	}

	public static void clearCurrentUser()
	{
		if(hasSession())
		{
			VaadinService.getCurrentRequest().getWrappedSession().removeAttribute(CURRENT_USER);
		}
	}

	private static boolean hasSession()
	{
		return VaadinService.getCurrentRequest() != null && VaadinService.getCurrentRequest().getWrappedSession() != null;
	}

	public static List<User> getAllUsers()
	{
		LOG.info("UserManager.getAllUsers");
		try
		{
			return Service.ofy().cache(false).load().type(User.class).list();
		}
		catch(Throwable t)
		{
			handleError("failed to get all users", t, LOG);
			return null;
		}
	}

	public static List<User> getActiveUsers()
	{
		LOG.info("UserManager.getActiveUsers");
		try
		{
			return Service.ofy().load().type(User.class).filter("active", true).list();
		}
		catch(Throwable t)
		{
			handleError("failed to get active users", t, LOG);
			return null;
		}
	}

	public static User getExistingUser(String key)
	{
		LOG.info("UserManager.getExistingUser: " + key);
		try
		{
			return Service.ofy().load().type(User.class).id(key).now();
		}
		catch(Throwable t)
		{
			//this is expected for new users
			return null;
		}
	}

	public static void saveUser(User user)
	{
		LOG.info("UserManager.saveUser: " + user);
		try
		{
			if(user.getDisplayName() == null) throw new IllegalArgumentException("displayName missing");
			if(user.getKey() == null) throw new IllegalArgumentException("token missing");
			if(!user.getRankedTeams().isEmpty() && user.getRankedTeams().size() != 32 && user.getRankedTeams().size() != 12) throw new IllegalArgumentException("invalid rankedTeams " + user.getRankedTeams().size());

			Service.ofy().save().entity(user).now();

			//update cached info for current user
			User current = getCurrentUser();
			if(current != null && current.getKey().equals(user.getKey()))
			{
				updateCurrentUser(user);
			}
		}
		catch(Throwable t)
		{
			handleError("failed to save user " + user.getDisplayName(), t, LOG);
		}
	}

	public static void deleteUser(User user)
	{
		LOG.info("UserManager.deleteUser: " + user);
		try
		{
			Service.ofy().delete().entity(user).now();
		}
		catch(Throwable t)
		{
			handleError("failed to delete user " + user.getDisplayName(), t, LOG);
		}
	}

	public static void updateTalkViewed()
	{
		User user = getCurrentUser();
		user.setTalkViewed(new Date());
		saveUser(user);
	}
}
