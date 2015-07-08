package com.toconnor.pool.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Week
 */
@Entity
@Cache(expirationSeconds = 14400)
public class Week implements Serializable, Comparable<Week>
{
	private static final long serialVersionUID = 1L;

	@Id
	private long week;

	@Index
	private Date firstGame;
	@Index
	private Date lastGame;
	private int games;
	private Date updated;
	@Index
	private boolean complete;
	private List<RankedUser> rankedUsers = new ArrayList<RankedUser>();

	private Week()
	{
		//needed for persistence
	}

	public Week(int week, Date firstGame, Date lastGame, int games)
	{
		this.week = week;
		this.firstGame = firstGame;
		this.lastGame = lastGame;
		this.games = games;
	}

	public int getWeek()
	{
		return (int)week;
	}

	public Date getFirstGame()
	{
		return firstGame;
	}

	public void setFirstGame(Date firstGame)
	{
		this.firstGame = firstGame;
	}

	public Date getLastGame()
	{
		return lastGame;
	}

	public int getGames()
	{
		return games;
	}

	public void setUpdated(Date updated)
	{
		this.updated = updated;
	}

	public Date getUpdated()
	{
		return updated;
	}

	public boolean isComplete()
	{
		return complete;
	}
	
	public void setComplete(boolean complete)
	{
		this.complete = complete;
	}

	public List<RankedUser> getRankedUsers()
	{
		return rankedUsers;
	}

	public void setRankedUsers(List<RankedUser> rankedUsers)
	{
		this.rankedUsers = rankedUsers;
	}

	public boolean hasStarted()
	{
		return firstGame != null && firstGame.before(new Date());
	}
	
	@Override
	public String toString()
	{
		return "Wk #" + week + ", " + games + " games, " + firstGame + " - " + lastGame;
	}

	@Override
	public int compareTo(Week other)
	{
		final int BEFORE = -1;
		final int AFTER = 1;
		final int SAME = 0;

		if(other == null) return BEFORE;

		if(this.week < other.week) return BEFORE;
		if(this.week > other.week) return AFTER;

		return SAME;
	}
}
