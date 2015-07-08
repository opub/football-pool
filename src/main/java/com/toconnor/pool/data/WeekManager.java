package com.toconnor.pool.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.toconnor.pool.model.RankedUser;
import com.toconnor.pool.model.User;
import com.toconnor.pool.model.Week;
import com.toconnor.pool.util.Cache;
import com.toconnor.pool.util.Log;

import static com.toconnor.pool.util.ErrorHandler.handleError;

/**
 * WeekManager
 */
public class WeekManager
{
	private static final Log LOG = new Log(WeekManager.class);

	public static String getPreviousWeekWinnerText()
	{
		try
		{
			String winners = (String)Cache.get(Cache.PREVIOUS_WINNERS);
			if(winners == null)
			{
				LOG.info("WeekManager.getPreviousWeekWinnerText");

				winners = "";

				Week current = WeekManager.getCurrentWeek();
				if(current != null && current.getWeek() > 1)
				{
					//get previous unless playoffs are over
					boolean endOfSeason = (current.getWeek() == 18 && WeekManager.getWeek(18) != null);
					Week previous = WeekManager.getWeek(endOfSeason ? current.getWeek() : current.getWeek() - 1);
					for(RankedUser ru : previous.getRankedUsers())
					{
						if(ru.getRank() == 1)
						{
							User won = UserManager.getExistingUser(ru.getUserKey());
							if(won != null)
							{
								winners += (winners.length() == 0 ? won.fetchFullName() : " and " + won.fetchFullName());
							}
						}
					}
				}
				Cache.put(Cache.PREVIOUS_WINNERS, winners, Cache.HOUR * 4);
			}
			return winners;
		}
		catch(Throwable t)
		{
			handleError("failed to get current week", t, LOG);
			return null;
		}
	}

	public static Week getWeek(long week)
	{
		LOG.info("WeekManager.getWeek: " + week);
		try
		{
			if(week > 17)
			{
				return getPlayoffWeek();
			}
			else
			{
				return Service.ofy().load().type(Week.class).id(week).now();
			}
		}
		catch(Throwable t)
		{
			handleError("failed to get week " + week, t, LOG);
			return null;
		}
	}

	public static Week getCurrentWeek()
	{
		try
		{
			Week current = (Week)Cache.get(Cache.CURRENT_WEEK);
			if(current == null)
			{
				LOG.info("GameManager.getCurrentWeek");

				//TODO this wouldn't be needed if Week.complete was indexed beforehand
				Calendar now = Calendar.getInstance();
				now.add(Calendar.HOUR_OF_DAY, -4);

				current = Service.ofy().load().type(Week.class).filter("lastGame >", now.getTime()).order("lastGame").first().now();
				if(current == null)
				{
					int month = now.get(Calendar.MONTH);
					if(month < 6 || month > 10)
					{
						current = getPlayoffWeek();
					}
					else
					{
						handleError("could not determine current week", new NullPointerException(), LOG);
					}
				}
				Cache.put(Cache.CURRENT_WEEK, current, Cache.HOUR * 4);
			}
			return current;
		}
		catch(Throwable t)
		{
			handleError("failed to get current week", t, LOG);
			return null;
		}
	}

	private static Week getPlayoffWeek()
	{
		try
		{
			//TODO need better way to specify playoff "week"
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss z");
			return new Week(18, df.parse("20150103-162000 EST"), df.parse("20150201-18300 EST"), 11);
		}
		catch(Throwable t)
		{
			handleError("failed to get playoff week", t, LOG);
			return null;
		}
	}

	public static List<Week> getAllWeeks()
	{
		try
		{
			return Service.ofy().load().type(Week.class).order("week").list();
		}
		catch(Throwable t)
		{
			handleError("failed to get all weeks", t, LOG);
			return null;
		}
	}

	public static void saveWeek(Week week)
	{
		LOG.info("GameManager.saveWeek: " + week);
		try
		{
			Service.ofy().save().entity(week).now();
		}
		catch(Throwable t)
		{
			handleError("failed to save week", t, LOG);
		}
	}
}
