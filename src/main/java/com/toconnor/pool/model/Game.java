package com.toconnor.pool.model;

import java.io.Serializable;
import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.toconnor.pool.Team;
import com.toconnor.pool.util.Formatter;

/**
 * Game
 */
@Entity
@Cache(expirationSeconds = 300)
public class Game implements Serializable, Comparable<Game>
{
	private static final long serialVersionUID = 1L;

	@Id
	private String key;

	@Index
	private int week;
	private String awayTeam;
	private int awayScore;
	private String homeTeam;
	private int homeScore;
	private boolean finalScore;
	private String status;
	private Date kickoff;

	private Game()
	{
		//needed for persistence
	}

	public static String getKey(int week, Team awayTeam, Team homeTeam)
	{
		return week + "-" + awayTeam.getCode() + "@" + homeTeam.getCode();
	}

	public Game(int week, Team awayTeam, int awayScore, Team homeTeam, int homeScore, boolean finalScore, String status)
	{
		this.week = week;
		this.awayTeam = awayTeam.getCode();
		this.awayScore = awayScore;
		this.homeTeam = homeTeam.getCode();
		this.homeScore = homeScore;
		this.finalScore = finalScore;
		this.status = status;
		this.key = getKey(week, awayTeam, homeTeam);
	}

	public String getKey()
	{
		return key;
	}

	public int getWeek()
	{
		return week;
	}

	public Team getAwayTeam()
	{
		return Team.get(awayTeam);
	}

	public int getAwayScore()
	{
		return awayScore;
	}

	public void setAwayScore(int awayScore)
	{
		this.awayScore = awayScore;
	}

	public Team getHomeTeam()
	{
		return Team.get(homeTeam);
	}

	public int getHomeScore()
	{
		return homeScore;
	}

	public void setHomeScore(int homeScore)
	{
		this.homeScore = homeScore;
	}

	public boolean isFinalScore()
	{
		return finalScore;
	}

	public void setFinalScore(boolean finalScore)
	{
		this.finalScore = finalScore;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public Date getKickoff()
	{
		return kickoff;
	}

	public void setKickoff(Date kickoff)
	{
		this.kickoff = kickoff;
	}

	public String getGameTime()
	{
		return Formatter.formatDate(kickoff);
	}

	public boolean hasStarted()
	{
		return kickoff != null && kickoff.before(new Date());
	}

	public String fetchWinningTeamCode()
	{
		if(homeScore > awayScore)
		{
			return homeTeam;
		}
		else if(awayScore > homeScore)
		{
			return awayTeam;
		}
		return null;
	}

	public String fetchLabel()
	{
		return awayTeam + " @ " + homeTeam;
	}

	@Override
	public String toString()
	{
		return key;
	}

	@Override
	public int compareTo(Game game)
	{
		final int BEFORE = -1;
		final int AFTER = 1;

		if(game == null) return BEFORE;

		if(this.week < game.week) return BEFORE;
		if(this.week > game.week) return AFTER;

		if(this.kickoff != null && game.kickoff != null)
		{
			if(this.kickoff.before(game.kickoff)) return BEFORE;
			if(this.kickoff.after(game.kickoff)) return AFTER;
		}
		return this.key.compareTo(game.key);
	}
}
