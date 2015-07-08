package com.toconnor.pool.util;

import java.util.logging.Level;

import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.memcache.Stats;

/**
 * Cache
 */
public class Cache
{
	//common cache keys
	public static final String LAST_TALK = "LAST_TALK_CACHE";
	public static final String CURRENT_WEEK = "CURRENT_WEEK_CACHE";
	public static final String PREVIOUS_WINNERS = "PREVIOUS_WINNERS_CACHE";
	public static final String WEEK_GAMES = "WEEK_GAMES_";
	public static final String RANKED_GAMES = "RANKED_GAMES_";
	public static final String PLAYOFF_TEAMS = "PLAYOFF_TEAMS_CACHE";

	//expiration times
	public static final int MINUTE = 60;
	public static final int HOUR = MINUTE * 60;
	public static final int DAY = HOUR * 24;

	public static Object get(Object key)
	{
		return get().get(key);
	}

	public static void put(Object key, Object value)
	{
		get().put(key, value);
	}

	public static void put(Object key, Object value, int expireSeconds)
	{
		get().put(key, value, Expiration.byDeltaSeconds(expireSeconds));
	}

	public static void delete(Object key)
	{
		get().delete(key);
	}

	public static void clear()
	{
		get().clearAll();
	}

	public static Stats stats()
	{
		return get().getStatistics();
	}

	private static MemcacheService get()
	{
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
		return syncCache;
	}
}
