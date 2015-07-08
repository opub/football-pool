package com.toconnor.pool.scores;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.common.base.Splitter;
import com.toconnor.pool.Team;
import com.toconnor.pool.model.Game;
import com.toconnor.pool.util.Log;

import static com.toconnor.pool.util.ErrorHandler.handleError;

/**
 * EspnMobileScoreLoader
 * NB: THIS CLASS IS NOT CURRENTLY USED SINCE NFL VERSION WAS MORE RELIABLE
 */
class EspnMobileScoreLoader implements IScoreLoader
{
	private static final Log LOG = new Log(EspnMobileScoreLoader.class);

	//TODO CHANGE DATE PER WEEK
	private static final String SCORE_URL = "http://m.espn.go.com/nfl/scoreboard?date=20130910&wjb";
	private static final String GAME_PREFIX = "href=\"/nfl/gamecast?gameId=";
	private static final Splitter SPLITTER = Splitter.on(GAME_PREFIX).trimResults().omitEmptyStrings();

	@Override
	public List<Game> getGames(int week)
	{
		List<Game> games = new ArrayList<Game>();
		try
		{
			URL url = new URL(SCORE_URL);
			HTTPRequest request = new HTTPRequest(url, HTTPMethod.GET);

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
				String gameLine = line.substring(line.indexOf(">")+1, line.indexOf("<")).replaceAll("\\*", "");

				// ATL 16 MIA 6 Final
				// BAL 7 @ *NYJ 6 11:55 3rd, ESPN
				// NYG at PHI 7:30 PM

				String[] gamePieces = gameLine.split(" ");

				Team awayTeam = Team.get(gamePieces[0]);
				int awayPoints = 0;

				Team homeTeam = Team.get(gamePieces[2]);
				int homePoints = 0;

				StringBuilder status = new StringBuilder();
				int statusIndex = 3;

				if (!"at".equalsIgnoreCase(gamePieces[1]))
				{
					try
					{
						awayPoints = Integer.valueOf(gamePieces[1]);
					}
					catch (Exception e)
					{
						//ignore as zero
					}
					try
					{
						homePoints = Integer.valueOf(gamePieces[3]);
					}
					catch (Exception e)
					{
						//ignore as zero
					}

					statusIndex = 4;
				}

				while (statusIndex < gamePieces.length)
				{
					status.append(gamePieces[statusIndex++]);
					if (statusIndex < gamePieces.length)
					{
						status.append(" ");
					}
				}

				String testStatus = status.toString().trim().toUpperCase();
				boolean isFinal = testStatus.startsWith("FINAL") || testStatus.startsWith("F/OT");
				Game game = new Game(week, awayTeam, awayPoints, homeTeam, homePoints, isFinal, status.toString());
				games.add(game);
			}
		}
		catch (Throwable t)
		{
			handleError("failed to retrieve ESPN Mobile games", t, LOG);
		}

		return games;
	}
}
