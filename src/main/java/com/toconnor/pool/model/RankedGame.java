package com.toconnor.pool.model;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.toconnor.pool.Team;

/**
 * RankedGame
 */
@Entity
@Cache(expirationSeconds = 30)
public class RankedGame implements Serializable, Comparable<RankedGame>
{
	private static final long serialVersionUID = 1L;

	@Id
	private String key;

	@Index
	private int week;
	@Index
	private String user;
	@Index
	private int rank;
	private String game;
	private String winner;

	private RankedGame()
	{
		//needed for persistence
	}

	public static String getKey(String gameKey, String userKey)
	{
		return gameKey + "-" + userKey;
	}

	public RankedGame(Game game, User user)
	{
		this.week = game.getWeek();
		this.user = user.getKey();
		this.game = game.getKey();
		this.key = getKey(this.game, this.user);
	}

	public RankedGame(int rank, int week, String game, User user, Team winner)
	{
		this.rank = rank;
		this.week = week;
		this.user = user.getKey();
		this.game = game;
		this.winner = winner != null ? winner.getCode() : null;
		this.key = getKey(this.game, this.user);
	}

	public String getUserKey()
	{
		return user;
	}

	public String getGameKey()
	{
		return game;
	}

	public String getWinner()
	{
		return winner;
	}

	public int getRank()
	{
		return rank;
	}

	public int getWeek()
	{
		return week;
	}

	public void setRank(int rank)
	{
		this.rank = rank;
	}

	public void setWinner(Team team)
	{
		this.winner = team.getCode();
	}

	public String getKey()
	{
		return key;
	}

	@Override
	public int compareTo(RankedGame rankedGame)
	{
		final int BEFORE = -1;
		final int SAME = 0;
		final int AFTER = 1;

		if(rankedGame == null) return BEFORE;

		if(this.week < rankedGame.week) return BEFORE;
		if(this.week > rankedGame.week) return AFTER;

		if(this.rank > rankedGame.rank) return BEFORE;
		if(this.rank < rankedGame.rank) return AFTER;

		return SAME;
	}

	@Override
	public String toString()
	{
		return key + "-" + winner;
	}
}
