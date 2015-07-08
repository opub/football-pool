package com.toconnor.pool.scores;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.toconnor.pool.PoolConstants;
import com.toconnor.pool.Team;
import com.toconnor.pool.data.GameManager;
import com.toconnor.pool.model.Game;
import com.toconnor.pool.util.Log;

import static com.toconnor.pool.util.ErrorHandler.handleError;

/**
 * EspnMobileScoreLoader
 */
class NFLScoreLoader implements IScoreLoader
{
/*
	<div id="scorebox-2013090804">
	<div class="new-score-box-wrapper">
		<div class="new-score-box-heading">
			<p>
				<span class="date">Sun, Sep 8</span>
				<span class="satellite">

				</span>
				<span class="network">fox</span>
				<span class="tickets-link"><a href="/tickets/detroit-lions?icampaign=Scores_Tickets" class="tickets-link">GET TICKETS</a></span>
			</p>
		</div>
		<div class="new-score-box">
			<div class="team-wrapper">
				<div class="away-team">
					<a href="/teams/profile?team=MIN"><img src="http://i.nflcdn.com/static/site/5.5/img/logos/teams-matte-80x53/MIN.png" class='team-logo' alt=""/></a>
					<div class="team-data">
						<div class="team-info">
							<p class="team-record">(0-0-0)</p>
							<p class="team-name"><a href="/teams/profile?team=MIN">Vikings</a></p>
						</div>
						<p class="total-score">--</p>
					</div>
				</div>
			</div>
			<div class="team-wrapper">
				<div class="home-team">
					<a href="/teams/profile?team=DET"><img src="http://i.nflcdn.com/static/site/5.5/img/logos/teams-matte-80x53/DET.png" class='team-logo' alt=""/></a>
					<div class="team-data">
						<div class="team-info">
							<p class="team-record">(0-0-0)</p>
							<p class="team-name"><a href="/teams/profile?team=DET">Lions</a></p>
						</div>
						<p class="total-score">--</p>
					</div>
				</div>
			</div>
			<div class="game-center-area">

				<a href="/gamecenter/2013090804/2013/REG1/vikings@lions" class="game-center-link"><img src="http://i.nflcdn.com/static/site/5.5/img/scores/game-center-regular.png" alt="Game Center" /></a>
				<p><span class="time-left" >1:00   PM ET</span></p>
				<div class="comments" style="clear:both"><a href="/gamecenter/2013090804/2013/REG1/vikings@lions#tab=discuss"></a></div>
			</div>
*/
	private static final Log LOG = new Log(NFLScoreLoader.class);

	private static final String SCORE_URL = "http://www.nfl.com/scores/" + PoolConstants.POOL_YEAR + "/REG";
	private static final String LIVE_JSON_URL = "http://www.nfl.com/liveupdate/scores/scores.json?random=";
	private static final String GAME_PREFIX = "id=\"scorebox-";
	private static final Splitter SPLITTER = Splitter.on(GAME_PREFIX).trimResults().omitEmptyStrings();

	@Override
	public List<Game> getGames(int week)
	{
		List<Game> games = new ArrayList<>();
		try
		{
			URL url = new URL(SCORE_URL + week + "?random=" + System.currentTimeMillis());
			HTTPRequest request = new HTTPRequest(url, HTTPMethod.GET);
			request.getFetchOptions().setDeadline(30.0);

			HTTPResponse response = URLFetchServiceFactory.getURLFetchService().fetch(request);
			String content = new String(response.getContent());
			if(content.indexOf(GAME_PREFIX) > 0)
			{
				content = content.substring(content.indexOf(GAME_PREFIX));
			}
			else
			{
				return games;
			}

			Iterable<String> lines = SPLITTER.split(content);

			for(String line : lines)
			{
				String date = line.substring(0, 8);

				line = line.substring(line.indexOf("profile?team=") + 13);
				Team awayTeam = Team.get(line.substring(0, line.indexOf("\"")));
				int awayPoints = 0;
				try
				{
					String points = getXmlValue(line, "total-score");
					awayPoints = Integer.valueOf(points);
				}
				catch (Exception e)
				{
					//ignore as zero
				}

				//advance to home team
				line = line.substring(line.indexOf("team-wrapper"));

				line = line.substring(line.indexOf("profile?team=") + 13);
				Team homeTeam = Team.get(line.substring(0, line.indexOf("\"")));
				int homePoints = 0;
				try
				{
					String points = getXmlValue(line, "total-score");
					homePoints = Integer.valueOf(points);
				}
				catch (Exception e)
				{
					//ignore as zero
				}

				String time = getXmlValue(line, "time-left");

				boolean isFinal = time.toUpperCase().contains("FINAL");
				Game game = GameManager.getGame(Game.getKey(week, awayTeam, homeTeam));
				if(game == null)
				{
					game = new Game(week, awayTeam, awayPoints, homeTeam, homePoints, isFinal, time);
				}
				else if(!game.isFinalScore())
				{
					if(game.hasStarted() && awayPoints == 0 && homePoints == 0)
					{
						updateLiveGame(game);
					}
					else if(awayPoints > 0 || homePoints > 0)     //avoid resetting scores if page wasn't parsed right or was cached
					{
						game.setAwayScore(awayPoints);
						game.setHomeScore(homePoints);
						game.setFinalScore(isFinal);
					}
				}

//				LOG.debug("TODO FIX! {} : {} @ {}", game.getKey(), date, time);

				//TODO FIX UPDATE OF DATE AND TIME!
				if(time.contains(":") && time.contains("PM"))
				{
					Calendar cal = Calendar.getInstance();

					//game date
					cal.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
					cal.set(Calendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 1);
					cal.set(Calendar.DATE, Integer.parseInt(date.substring(6, 8)));

					//game time
					cal.set(Calendar.HOUR, Integer.parseInt(time.substring(0, time.indexOf(":"))));
					cal.set(Calendar.MINUTE, Integer.parseInt(time.substring(time.indexOf(":") + 1, time.indexOf(":") + 3)));
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					cal.set(Calendar.AM_PM, Calendar.PM);
					cal.setTimeZone(TimeZone.getTimeZone("America/New_York"));

					game.setKickoff(cal.getTime());
				}

				games.add(game);
			}
		}
		catch (Throwable t)
		{
			handleError("failed to retrieve NFL games", t, LOG);
		}

		return games;
	}

	private Map<String, ScoreJSON> liveScores;

	//live games get their data from a different JSON call
	private void updateLiveGame(Game game)
	{
		try
		{
			if(liveScores == null || liveScores.isEmpty())
			{
				URL url = new URL(LIVE_JSON_URL + System.currentTimeMillis());
				HTTPRequest request = new HTTPRequest(url, HTTPMethod.GET);
				request.getFetchOptions().setDeadline(30.0);

				HTTPResponse response = URLFetchServiceFactory.getURLFetchService().fetch(request);
				String json = new String(response.getContent());
				Gson gson = new Gson();
				Type type = new TypeToken<Map<String, ScoreJSON>>(){}.getType();
				liveScores = gson.fromJson(json, type);
			}

			for(ScoreJSON score : liveScores.values())
			{
				if(game.getAwayTeam().equals(Team.get(score.away.abbr)) && game.getHomeTeam().equals(Team.get(score.home.abbr)))
				{
					int awayPoints = 0;
					int homePoints = 0;
					try
					{
						awayPoints = score.away.score.get("T");
						homePoints = score.home.score.get("T");
					}
					catch (NullPointerException npe)
					{
						//ignore since value could be null (e.g. "T":null)
					}

					//avoid resetting scores if page wasn't parsed right or was cached
					if(awayPoints > 0 || homePoints > 0)
					{
						game.setAwayScore(awayPoints);
						game.setHomeScore(homePoints);
					}

					game.setFinalScore("FINAL".equalsIgnoreCase(score.qtr));

					break;
				}
			}
		}
		catch (Throwable t)
		{
			handleError("failed to retrieve live NFL game JSON", t, LOG);
		}
	}

	private static final String getXmlValue(String line, String attribute)
	{
		line = line.substring(line.indexOf(attribute));
		return line.substring(line.indexOf(">")+1, line.indexOf("<")).trim();
	}

	private static class ScoreJSON implements Serializable
	{
		TeamInfo home;
		TeamInfo away;
		String qtr;

		private static class TeamInfo implements Serializable
		{
			Map<String, Integer> score;
			String abbr;
			Integer to;
		}
	}
}
