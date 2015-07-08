package com.toconnor.pool.scores;

import java.util.List;

import com.toconnor.pool.PoolTestCase;
import com.toconnor.pool.model.Game;
import org.junit.Test;

/**
 * ScoreLoaderTest
 */
public class ScoreLoaderTest extends PoolTestCase
{
//	@Test
//	public void testEspnMobileScoreLoader() throws Exception
//	{
//		runLoader(new EspnMobileScoreLoader());
//	}

	@Test
	public void testNFLScoreLoader() throws Exception
	{
		runLoader(new NFLScoreLoader());
	}

	private void runLoader(IScoreLoader loader)
	{
		List<Game> games = loader.getGames(1);
		assertNotNull(games);
		assertEquals(16, games.size());
		for(Game game : games)
		{
			assertFalse(game.toString(), game.getAwayTeam().equals(game.getHomeTeam()));
		}

		games = loader.getGames(8);
		assertNotNull(games);
		assertEquals(15, games.size());

		games = loader.getGames(17);
		assertNotNull(games);
		assertEquals(16, games.size());
	}
}
