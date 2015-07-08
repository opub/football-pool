package com.toconnor.pool.model;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Embed;

/**
 * RankedUser
 */
@Embed
public class RankedUser implements Serializable, Comparable<RankedUser>
{
	private static final long serialVersionUID = 1L;

	private int rank;
	private int points;
	private String user;

	private RankedUser()
	{
		//needed for persistence
	}

	public RankedUser(String user, int points)
	{
		this.user = user;
		this.points = points;
	}

	public int getRank()
	{
		return rank;
	}

	public void setRank(int rank)
	{
		this.rank = rank;
	}

	public int getPoints()
	{
		return points;
	}

	public String getUserKey()
	{
		return user;
	}

	@Override
	public String toString()
	{
		return rank + "=" + points + "(" + user + ")";
	}

	@Override
	public int compareTo(RankedUser other)
	{
		final int BEFORE = -1;
		final int AFTER = 1;
		final int SAME = 0;

		if(other == null) return BEFORE;

		if(this.points > other.points) return BEFORE;
		if(this.points < other.points) return AFTER;

		return SAME;
	}
}
