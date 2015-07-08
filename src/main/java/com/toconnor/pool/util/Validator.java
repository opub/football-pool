package com.toconnor.pool.util;

/**
 * Validator
 *
 */
public class Validator
{
	public static void assertTrue(boolean test, String message, Object...args)
	{
		if(!test)
		{
			throw new ValidationException(message, args);
		}
	}

	public static void assertNotNull(Object test, String message, Object...args)
	{
		if(test == null)
		{
			throw new ValidationException(message, args);
		}
	}

	private static class ValidationException extends RuntimeException
	{
		private ValidationException(String message, Object...args)
		{
			super(String.format(message, args));
		}
	}
}
