package com.toconnor.pool.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.toconnor.pool.Team;
import com.toconnor.pool.model.PlayoffTeam;
import com.toconnor.pool.util.Cache;
import com.toconnor.pool.util.Log;

/**
 * PlayoffManager
 */
public class PlayoffManager
{
	private static final Log LOG = new Log(PlayoffManager.class);

	public static List<PlayoffTeam> getPlayoffs()
	{
		List<PlayoffTeam> playoffTeams = (List<PlayoffTeam>)Cache.get(Cache.PLAYOFF_TEAMS);
		if(playoffTeams != null)
		{
			return playoffTeams;
		}

		LOG.info("PlayoffManager.getPlayoffs");
		try
		{
			playoffTeams = Service.ofy().load().type(PlayoffTeam.class).list();
		}
		catch(Throwable t)
		{
			//this is expected before start of playoffs
			LOG.error(t);
		}

		if(playoffTeams == null || playoffTeams.isEmpty())
		{
			if(Team.PLAYOFF_TEAMS.isEmpty())
			{
				return Collections.EMPTY_LIST;
			}
			else
			{
				//initialize new playoffs
				playoffTeams = new ArrayList<>();
				for(Team team : Team.PLAYOFF_TEAMS)
				{
					PlayoffTeam pt = new PlayoffTeam(0, team);

					LOG.info("PlayoffManager.getPlayoffs.save");
					Service.ofy().save().entity(pt).now();

					playoffTeams.add(pt);
				}
			}
		}
		else
		{
			//this is needs to make ths list serializable
			playoffTeams = new ArrayList<>(playoffTeams);
		}

		Collections.sort(playoffTeams);

		//update cache
		Cache.put(Cache.PLAYOFF_TEAMS, playoffTeams, Cache.HOUR * 6);

		return playoffTeams;
	}
}
