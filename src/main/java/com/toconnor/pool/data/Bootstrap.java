package com.toconnor.pool.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.toconnor.pool.model.Game;
import com.toconnor.pool.model.User;
import com.toconnor.pool.scores.ScoreLoaderFactory;
import com.toconnor.pool.util.Log;

/**
 * Bootstrap
 */
public class Bootstrap implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Log LOG = new Log(Bootstrap.class);

	private static boolean initialized = false;

	public static void init()
	{
		if(!initialized)
		{
			LOG.info("initializing system");
			initialized = true;

			try
			{
				seedGames();
			}
			catch(Throwable t)
			{
				LOG.error("bootstrap initialization failed", t);
			}
		}
	}

	private static void seedGames()
	{
		//if week 17 is populated then we assume they are all ok
		if(GameManager.getGames(17).size() != 16)
		{
			for(int week = 1; week <= 17; week++)
			{
				List<Game> games = GameManager.getGames(week);

				//make an attempt to check games per week.  bye weeks have inconsistent game counts so not perfect
				if(((week < 4 || week > 12) && games.size() != 16) || (week > 3 && week < 13 && games.size() < 13))
				{
					LOG.info("seeding games week " + week);

					games = ScoreLoaderFactory.get().getGames(week);
					GameManager.saveGames(games);
				}
			}

			clearRanks();
		}
		else
		{
			LOG.info("games already seeded");
		}
	}

	private static void clearRanks()
	{
		List<User> users = UserManager.getAllUsers();
		for(User user : users)
		{
			LOG.info("resetting ranked teams for " + user.fetchFullName());
			user.setRankedTeams(Collections.EMPTY_LIST);
			UserManager.saveUser(user);
		}
	}
}
