package com.toconnor.pool.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.appengine.api.memcache.Stats;
import com.toconnor.pool.data.PlayoffManager;
import com.toconnor.pool.model.PlayoffTeam;
import com.toconnor.pool.util.Cache;
import com.toconnor.pool.util.Log;

import static com.toconnor.pool.util.ErrorHandler.handleError;

/**
 * CacheInspector
 * There have been various caching issues that this helps to diagnose.
 */
public class CacheInspector extends HttpServlet
{
	private static final Log LOG = new Log(CacheInspector.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			PrintWriter output = response.getWriter();
			output.print("<PRE>");

			//TODO this is very insecure
			if("true".equals(request.getParameter("clear")))
			{
				LOG.warn("CACHES MANUALLY CLEARED");
				Cache.clear();
				output.print("CACHE CLEARED");
			}
			else
			{
				LOG.warn("CACHES MANUALLY VIEWED");
				Stats stats = Cache.stats();
				output.print("CACHE STATS");
				output.print("\nitems: " + stats.getItemCount());
				output.print("\nbytes: " + stats.getTotalItemBytes());
				output.print("\nhits: " + stats.getHitCount());
				output.print("\nmisses: " + stats.getMissCount());
				output.print("\nage: " + stats.getMaxTimeWithoutAccess());

				//playoffs
				output.print("\n\nPLAYOFF TEAMS\n");
				List<PlayoffTeam> playoffTeams = PlayoffManager.getPlayoffs();
				for(PlayoffTeam pt : playoffTeams)
				{
					output.print(pt.getTeam().getCode() + ": " + pt.getWins() + " wins\n");
				}
			}

			output.print("</PRE>");
		}
		catch(Throwable t)
		{
			handleError("failed to update games", t, LOG);
		}
	}
}
