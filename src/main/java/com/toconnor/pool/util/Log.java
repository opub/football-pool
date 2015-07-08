package com.toconnor.pool.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.toconnor.pool.data.UserManager;
import com.toconnor.pool.model.User;

public final class Log
{
	private final Logger logger;

	public Log(Class<?> caller)
	{
		logger = Logger.getLogger(caller.getName());
	}

	private static String username()
	{
		User user = UserManager.getCurrentUser();
		return (user == null) ? "[NONE] " : "[" + user.fetchFullName() + "] ";
	}

	public void debug(String message, Throwable t)
	{
		if (logger.isLoggable(Level.FINE))
		{
			debug(message + "\n" + getStackTrace(t));
		}
	}

	public void debug(Throwable t)
	{
		if (logger.isLoggable(Level.FINE))
		{
			debug(getStackTrace(t));
		}
	}

	public void debug(String message, String... params)
	{
		if (logger.isLoggable(Level.FINE))
		{
			if(params != null && params.length > 0)
			{
				message = String.format(message, (Object[])params);
			}
			logger.fine(username() + message);
		}
	}

	public void info(String message, Throwable t)
	{
		if (logger.isLoggable(Level.INFO))
		{
			info(message + "\n" + getStackTrace(t));
		}
	}

	public void info(Throwable t)
	{
		if (logger.isLoggable(Level.INFO))
		{
			info(getStackTrace(t));
		}
	}

	public void info(String message, String... params)
	{
		if (logger.isLoggable(Level.INFO))
		{
			if(params != null && params.length > 0)
			{
				message = String.format(message, (Object[])params);
			}
			logger.info(username() + message);
		}
	}

	public void warn(String message, Throwable t)
	{
		if (logger.isLoggable(Level.WARNING))
		{
			warn(message + "\n" + getStackTrace(t));
		}
	}

	public void warn(Throwable t)
	{
		if (logger.isLoggable(Level.WARNING))
		{
			warn(getStackTrace(t));
		}
	}

	public void warn(String message, String... params)
	{
		if (logger.isLoggable(Level.WARNING))
		{
			if(params != null && params.length > 0)
			{
				message = String.format(message, (Object[])params);
			}
			logger.warning(username() + message);
		}
	}

	public void error(String message, Throwable t)
	{
		if (logger.isLoggable(Level.SEVERE))
		{
			error(message + "\n" + getStackTrace(t));
		}
	}

	public void error(Throwable t)
	{
		if (logger.isLoggable(Level.SEVERE))
		{
			error(getStackTrace(t));
		}
	}

	public void error(String message, String... params)
	{
		if (logger.isLoggable(Level.SEVERE))
		{
			if(params != null && params.length > 0)
			{
				message = String.format(message, (Object[])params);
			}
			logger.severe(username() + message);
		}
	}

	private String getStackTrace(Throwable t)
	{
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}
