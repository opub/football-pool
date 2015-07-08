package com.toconnor.pool.data;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.toconnor.pool.model.Talk;
import com.toconnor.pool.util.Cache;
import com.toconnor.pool.util.Log;

import static com.toconnor.pool.util.ErrorHandler.handleError;

/**
 * TalkManager
 */
public class TalkManager
{
	private static final Log LOG = new Log(TalkManager.class);

	public static int getNewCount()
	{
		try
		{
			int count = 0;
			Date viewed = UserManager.getCurrentUser().getTalkViewed();
			Date last = (Date)Cache.get(Cache.LAST_TALK);
			if(last == null || viewed == null || last.after(viewed))
			{
				LOG.info("TalkManager.getNewCount");

				if(viewed == null)
				{
					count = Service.ofy().load().type(Talk.class).count();
				}
				else
				{
					count = Service.ofy().load().type(Talk.class).filter("posted >", viewed).count();
				}

				if(last == null)
				{
					Cache.put(Cache.LAST_TALK, new Date());
				}
			}
			return count;
		}
		catch(Throwable t)
		{
			handleError("failed to get new count", t, LOG);
			return 0;
		}
	}

	public static List<Talk> getTalk(int count)
	{
		LOG.info("TalkManager.getTalk: " + count);
		try
		{
			return Service.ofy().load().type(Talk.class).order("posted").limit(count).list();
		}
		catch(Throwable t)
		{
			handleError("failed to get talk " + count, t, LOG);
			return Collections.emptyList();
		}
	}

	public static void saveTalk(Talk talk)
	{
		LOG.info("TalkManager.saveTalk: " + talk);
		try
		{
			Service.ofy().save().entity(talk).now();
			Cache.put(Cache.LAST_TALK, new Date());
		}
		catch(Throwable t)
		{
			handleError("failed to save talk", t, LOG);
		}
	}
}
