package com.toconnor.pool.util;

/**
 * Error
 */
public class ErrorHandler
{
	public static void handleError(String message, Throwable t, Log log)
	{
		handleErrorSilently(message, t, log);
		throw new PoolException(t);
	}

	public static void handleErrorSilently(String message, Throwable t, Log log)
	{
		log.error(message, t);
	}
}
