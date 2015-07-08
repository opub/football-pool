package com.toconnor.pool.data;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.toconnor.pool.model.Game;
import com.toconnor.pool.model.PlayoffTeam;
import com.toconnor.pool.model.RankedGame;
import com.toconnor.pool.model.Talk;
import com.toconnor.pool.model.User;
import com.toconnor.pool.model.Week;

/**
 * Service
 */
class Service
{
	static
	{
		//See https://code.google.com/p/objectify-appengine/wiki/UpgradeVersion4ToVersion5 for why this is needed.
		factory().setSaveWithNewEmbedFormat(true);

		factory().register(Week.class);
		factory().register(User.class);
		factory().register(Game.class);
		factory().register(RankedGame.class);
		factory().register(Talk.class);
		factory().register(PlayoffTeam.class);
	}

	static Objectify ofy()
	{
		return ObjectifyService.ofy();
	}

	static ObjectifyFactory factory()
	{
		return ObjectifyService.factory();
	}
}
