package com.toconnor.pool.model;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Embed;
import com.toconnor.pool.Team;

/**
 * RankedTeam
 */
@Embed
public class RankedTeam implements Serializable
{
	private static final long serialVersionUID = 1L;

	private int rank;
	private String team;

	private RankedTeam()
	{
		//needed for persistence
	}

	public RankedTeam(int rank, Team team)
	{
		this.rank = rank;
		this.team = team.getCode();
	}

	public int getRank()
	{
		return rank;
	}

	public Team getTeam()
	{
		return Team.get(team);
	}

	@Override
	public String toString()
	{
		return rank + "=" + team;
	}
}
