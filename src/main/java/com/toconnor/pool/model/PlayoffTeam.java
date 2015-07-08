package com.toconnor.pool.model;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.toconnor.pool.Team;

/**
 * PlayoffTeam
 */
@Entity
@Cache(expirationSeconds = 300)
public class PlayoffTeam implements Serializable, Comparable<PlayoffTeam>
{
	private static final long serialVersionUID = 1L;

	@Id
	private String team;
	private int wins;

	private PlayoffTeam()
	{
		//needed for persistence
	}

	public PlayoffTeam(int wins, Team team)
	{
		this.wins = wins;
		this.team = team.getCode();
	}

	public int getWins()
	{
		return wins;
	}

	public Team getTeam()
	{
		return Team.get(team);
	}

	@Override
	public String toString()
	{
		return team + "=" + wins;
	}

	@Override
	public int compareTo(PlayoffTeam playoffTeam)
	{
		return this.team.compareTo(playoffTeam.team);
	}
}
