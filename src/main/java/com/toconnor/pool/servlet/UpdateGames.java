package com.toconnor.pool.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.toconnor.pool.Team;
import com.toconnor.pool.data.GameManager;
import com.toconnor.pool.data.PlayoffManager;
import com.toconnor.pool.data.UserManager;
import com.toconnor.pool.data.WeekManager;
import com.toconnor.pool.model.Game;
import com.toconnor.pool.model.PlayoffTeam;
import com.toconnor.pool.model.RankedGame;
import com.toconnor.pool.model.RankedTeam;
import com.toconnor.pool.model.RankedUser;
import com.toconnor.pool.model.User;
import com.toconnor.pool.model.Week;
import com.toconnor.pool.scores.ScoreLoaderFactory;
import com.toconnor.pool.util.Log;

import static com.toconnor.pool.util.ErrorHandler.handleError;

/**
 * UpdateGames
 */
public class UpdateGames extends HttpServlet
{
	private static final Log LOG = new Log(UpdateGames.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			PrintWriter output = response.getWriter();
			output.print("<PRE>");

			if("true".equals(request.getParameter("force")) || shouldUpdate())
			{
				Week week;
				if(request.getParameter("week") != null)
				{
					week = WeekManager.getWeek(Long.valueOf(request.getParameter("week")));
				}
				else
				{
					week = WeekManager.getCurrentWeek();
				}

				if(week == null)
				{
					output.print("WEEK NOT FOUND");
				}
				else if(week.isComplete())
				{
					output.print("Week complete " + week);
				}
				else if(week.getWeek() < 18)
				{
					List<Game> games = ScoreLoaderFactory.get().getGames(week.getWeek());
					for(Game g : games)
					{
						output.print(g.toString() + " " + g.getAwayScore() + "-" + g.getHomeScore() + ", final=" + g.isFinalScore() + "\n");
					}
					GameManager.saveGames(games);

					boolean complete = true;
					for(Game g : games)
					{
						if(!g.isFinalScore())
						{
							complete = false;
							break;
						}
					}

					if(complete)
					{
						output.print("Completing week\n");
						completeWeek(week, games);
					}

					week.setUpdated(new Date());
					WeekManager.saveWeek(week);

					output.print("Updated " + week);
				}
				else
				{
					//get wins per team
					int wins = 0;
					List<PlayoffTeam> teams = PlayoffManager.getPlayoffs();
					Map<Team, Integer> teamWins = new HashMap<Team, Integer>();
					for(PlayoffTeam pt : teams)
					{
						teamWins.put(pt.getTeam(), pt.getWins());
						wins += pt.getWins();
					}

					if(wins == 11)
					{
						completePlayoffs(week, teamWins);

						week.setComplete(true);
						week.setUpdated(new Date());
						WeekManager.saveWeek(week);

						output.print("Updated Playoffs");
					}
					else
					{
						output.print("Playoffs still in progress");
					}
				}
			}
			else
			{
				output.print("No games playing now.");
			}
			output.print("</PRE>");
		}
		catch(Throwable t)
		{
			handleError("failed to update games", t, LOG);
		}
	}

	//this is similar to WeeklyStandingsPage logic
	private static void completeWeek(Week week, List<Game> games)
	{
		//the completion process should only happen once per week
		week.setComplete(true);

		//put week's games into map for easier lookup later
		Map<String, Game> gameMap = new HashMap<String, Game>();
		for(Game g : games)
		{
			gameMap.put(g.getKey(), g);
		}

		//seed map with 0 for each user in case they didn't rank teams prior to this week
		Map<String, Integer> userPoints = new HashMap<String, Integer>();
		for(User user : UserManager.getActiveUsers())
		{
			userPoints.put(user.getKey(), 0);
		}

		//determine total points for each user
		List<RankedGame> rankedGames = GameManager.getRankedGames(week.getWeek());
		for(RankedGame rg : rankedGames)
		{
			Integer pts = userPoints.get(rg.getUserKey());
			if(pts == null)
			{
				pts = 0;
			}

			Game g = gameMap.get(rg.getGameKey());
			if(rg.getWinner().equals(g.fetchWinningTeamCode()))
			{
				pts += rg.getRank();
			}

			userPoints.put(rg.getUserKey(), pts);
		}

		saveWeek(week, userPoints);
	}

	//this is similar to WeeklyStandingsPage logic
	private static void completePlayoffs(Week week, Map<Team, Integer> teamWins)
	{
		//populate user playoff points
		Map<String, Integer> userPoints = new HashMap<String, Integer>();
		List<User> users = UserManager.getActiveUsers();
		for(User user : users)
		{
			userPoints.put(user.getKey(), 0);

			if(user.getRankedTeams() != null && !user.getRankedTeams().isEmpty())
			{
				int pts = 12;
				int total = 0;
				for(RankedTeam rt : user.getRankedTeams())
				{
					if(teamWins.containsKey(rt.getTeam()))
					{
						total += pts * teamWins.get(rt.getTeam());
						pts--;
					}
				}

				userPoints.put(user.getKey(), total);
			}
		}

		saveWeek(week, userPoints);
	}

	private static void saveWeek(Week week, Map<String, Integer> userPoints)
	{
		//rank the users
		List<RankedUser> rankedUsers = new ArrayList<RankedUser>();
		for(Map.Entry<String, Integer> entry : userPoints.entrySet())
		{
			rankedUsers.add(new RankedUser(entry.getKey(), entry.getValue()));
		}
		Collections.sort(rankedUsers);

		//update individual rank values accounting for ties
		int rank = 1;
		int count = 1;
		int last = -1;
		for(RankedUser ru : rankedUsers)
		{
			if(ru.getPoints() != last)
			{
				rank = count;
				last = ru.getPoints();
			}
			ru.setRank(rank);
			count++;
		}

		//TODO update users' winnings

		week.setRankedUsers(rankedUsers);
	}

	private static boolean shouldUpdate()
	{
		Calendar now = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"));   //CST has easier game times

		int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
		if(dayOfWeek == Calendar.TUESDAY || dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.FRIDAY)
		{
			//no games these days
			return false;
		}

		if(dayOfWeek == Calendar.SUNDAY && now.get(Calendar.AM_PM) == Calendar.PM)
		{
			//sunday afternoon and night
			return true;
		}

		if((dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.THURSDAY) && now.get(Calendar.HOUR_OF_DAY) > 19)
		{
			//monday and thursday night
			return true;
		}

		if(dayOfWeek == Calendar.SATURDAY && now.get(Calendar.MONTH) == 11 && now.get(Calendar.AM_PM) == Calendar.PM)
		{
			//saturday afternoon in december
			return true;
		}

		if(dayOfWeek == Calendar.THURSDAY && now.get(Calendar.MONTH) == 10 && now.get(Calendar.DAY_OF_MONTH) > 20 && now.get(Calendar.AM_PM) == Calendar.PM)
		{
			//thursday afternoon in late november
			return true;
		}

		return false;
	}
}
