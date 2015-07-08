package com.toconnor.pool.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.toconnor.pool.model.Game;
import com.toconnor.pool.model.RankedGame;
import com.toconnor.pool.model.Week;
import com.toconnor.pool.util.Cache;
import com.toconnor.pool.util.Log;

import static com.toconnor.pool.util.ErrorHandler.handleError;

/**
 * GameManager
 */
public class GameManager
{
	private static final Log LOG = new Log(GameManager.class);

	public static Game getGame(String key)
	{
		LOG.info("GameManager.getGame: " + key);
		try
		{
			return Service.ofy().load().type(Game.class).id(key).now();
		}
		catch(Throwable t)
		{
			//this is expected for new games
			return null;
		}
	}

	public static List<Game> getFutureWeekGames()
	{
		LOG.info("GameManager.getFutureWeekGames");
		try
		{
			Week first = Service.ofy().load().type(Week.class).filter("firstGame >", new Date()).order("firstGame").first().now();
			return Service.ofy().load().type(Game.class).filter("week >=", first.getWeek()).list();
		}
		catch(Throwable t)
		{
			handleError("failed to get future games", t, LOG);
			return null;
		}
	}

	public static List<Game> getGames(int week)
	{
		List<Game> games = (List<Game>)Cache.get(Cache.WEEK_GAMES + week);
		if(games != null)
		{
			return games;
		}

		LOG.info("GameManager.getGames: " + week);
		try
		{
			games = Service.ofy().load().type(Game.class).filter("week", week).list();

			//update cache with serializable list
			games = new ArrayList<Game>(games);
			Cache.put(Cache.WEEK_GAMES + week, games, Cache.DAY);

			return games;
		}
		catch(Throwable t)
		{
			handleError("failed to get games for week " + week, t, LOG);
			return null;
		}
	}

	public static List<RankedGame> getRankedGames(int week)
	{
		List<RankedGame> games = (List<RankedGame>)Cache.get(Cache.RANKED_GAMES + week);
		if(games != null)
		{
			return games;
		}

		LOG.info("GameManager.getRankedGames: " + week);
		try
		{
			games = Service.ofy().load().type(RankedGame.class).filter("week", week).list();

			//update cache with serializable list
			games = new ArrayList<RankedGame>(games);
			Cache.put(Cache.RANKED_GAMES + week, games, Cache.DAY);

			return games;
		}
		catch(Throwable t)
		{
			handleError("failed to get ranked games for week " + week, t, LOG);
			return null;
		}
	}

	public static List<RankedGame> getUserRankedGames(int week)
	{
		LOG.info("GameManager.getUserRankedGames: " + week);
		try
		{
			String user = UserManager.getCurrentUser().getKey();
			return Service.ofy().cache(false).load().type(RankedGame.class).filter("week", week).filter("user", user).order("rank").list();
		}
		catch(Throwable t)
		{
			handleError("failed to get user ranked games for week " + week, t, LOG);
			return null;
		}
	}

	public static void saveGames(List<Game> games)
	{
		LOG.info("GameManager.saveGames: " + games.size());

		Collections.sort(games);

		//save week info
		int weekNumber = 1;
		try
		{
			int count = games.size();
			Game first = games.get(0);
			Game last = games.get(count - 1);
			weekNumber = first.getWeek();
			Week week = new Week(weekNumber, first.getKickoff(), last.getKickoff(), count);
			Service.ofy().save().entity(week).now();
		}
		catch(Throwable t)
		{
			handleError("failed to update week", t, LOG);
		}

		//save games
		try
		{
			Service.ofy().save().entities(games).now();
		}
		catch(Throwable t)
		{
			handleError("failed to save games", t, LOG);
		}

		//update cache
		Cache.put(Cache.WEEK_GAMES + weekNumber, games, Cache.DAY);
	}

	public static void saveRankedGames(List<RankedGame> games)
	{
		LOG.info("GameManager.saveRankedGames: " + games.size());
		try
		{
			//TODO this could be more precise
			//clear the week cache since something changed
			Cache.delete(Cache.RANKED_GAMES + games.get(0).getWeek());

			Service.ofy().save().entities(games).now();
		}
		catch(Throwable t)
		{
			handleError("failed to save ranked games", t, LOG);
		}
	}

	public static void saveRankedGame(RankedGame game)
	{
		LOG.info("GameManager.saveRankedGame: " + game);
		try
		{
			//TODO this could be more precise
			//clear the week cache since something changed
			Cache.delete(Cache.RANKED_GAMES + game.getWeek());

			Service.ofy().save().entity(game).now();
		}
		catch(Throwable t)
		{
			handleError("failed to save ranked game", t, LOG);
		}
	}
}
