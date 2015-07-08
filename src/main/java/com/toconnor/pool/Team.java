package com.toconnor.pool;

import java.util.List;

import com.google.common.collect.Lists;
import com.googlecode.objectify.annotation.Embed;

/**
 * Team
 *
 */
@Embed
public class Team
{
	public static final Team ARI = new Team("ARI", "Arizona Cardinals");
	public static final Team ATL = new Team("ATL", "Atlanta Falcons");
	public static final Team BAL = new Team("BAL", "Baltimore Ravens");
	public static final Team BUF = new Team("BUF", "Buffalo Bills");
	public static final Team CAR = new Team("CAR", "Carolina Panthers");
	public static final Team CHI = new Team("CHI", "Chicago Bears");
	public static final Team CIN = new Team("CIN", "Cincinnati Bengals");
	public static final Team CLE = new Team("CLE", "Cleveland Browns");
	public static final Team DAL = new Team("DAL", "Dallas Cowboys");
	public static final Team DEN = new Team("DEN", "Denver Broncos");
	public static final Team DET = new Team("DET", "Detroit Lions");
	public static final Team GBP = new Team("GBP", "Green Bay Packers");
	public static final Team HOU = new Team("HOU", "Houston Texans");
	public static final Team IND = new Team("IND", "Indianapolis Colts");
	public static final Team JAC = new Team("JAC", "Jacksonville Jaguars");
	public static final Team KCC = new Team("KCC", "Kansas City Chiefs");
	public static final Team MIA = new Team("MIA", "Miami Dolphins");
	public static final Team MIN = new Team("MIN", "Minnesota Vikings");
	public static final Team NEP = new Team("NEP", "New England Patriots");
	public static final Team NOS = new Team("NOS", "New Orleans Saints");
	public static final Team NYG = new Team("NYG", "New York Giants");
	public static final Team NYJ = new Team("NYJ", "New York Jets");
	public static final Team OAK = new Team("OAK", "Oakland Raiders");
	public static final Team PHI = new Team("PHI", "Philadelphia Eagles");
	public static final Team PIT = new Team("PIT", "Pittsburgh Steelers");
	public static final Team SDC = new Team("SDC", "San Diego Chargers");
	public static final Team SFF = new Team("SFF", "San Francisco 49ers");
	public static final Team SEA = new Team("SEA", "Seattle Seahawks");
	public static final Team SLR = new Team("SLR", "St. Louis Rams");
	public static final Team TBB = new Team("TBB", "Tampa Bay Buccaneers");
	public static final Team TEN = new Team("TEN", "Tennessee Titans");
	public static final Team WAS = new Team("WAS", "Washington Redskins");

	public static final List<Team> ALL_TEAMS = Lists.newArrayList(ARI, ATL, BAL, BUF, CAR, CHI, CIN, CLE, DAL, DEN, DET, GBP, HOU, IND, JAC, KCC, MIA, MIN, NEP, NOS, NYG, NYJ, OAK, PHI, PIT, SDC, SFF, SEA, SLR, TBB, TEN, WAS);

	//TODO create a better way of identifying who the playoff teams are
	public static final List<Team> PLAYOFF_TEAMS = Lists.newArrayList(ARI, BAL, CAR, CIN, DAL, DEN, DET, GBP, IND, NEP, PIT, SEA);

	public static Team get(String find)
	{
		for(Team team : ALL_TEAMS)
		{
			if(team.code.equalsIgnoreCase(find))
			{
				return team;
			}
			if(team.name.equalsIgnoreCase(find))
			{
				return team;
			}
		}

		//special cases for score loaders
		if("GB".equalsIgnoreCase(find)) return GBP;
		if("KC".equalsIgnoreCase(find)) return KCC;
		if("NE".equalsIgnoreCase(find)) return NEP;
		if("NO".equalsIgnoreCase(find)) return NOS;
		if("SD".equalsIgnoreCase(find)) return SDC;
		if("SF".equalsIgnoreCase(find)) return SFF;
		if("STL".equalsIgnoreCase(find)) return SLR;
		if("TB".equalsIgnoreCase(find)) return TBB;
		if("WSH".equalsIgnoreCase(find)) return WAS;

		throw new RuntimeException("Unknown team: " + find);
	}

	private String code;
    private String name;

	private Team()
	{
		//needed for persistence
	}

    private Team(String code, String name)
	{
		this.code = code;
		this.name = name;
	}

	public String getCode()
	{
		return code;
	}

	public String getName()
	{
		return name;
	}

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Team team = (Team) o;

        if (!code.equals(team.code)) return false;
        if (!name.equals(team.name)) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = code.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

	@Override
	public String toString()
	{
		return code;
	}
}
