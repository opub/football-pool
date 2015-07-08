package com.toconnor.pool.scores;

import java.util.List;

import com.toconnor.pool.model.Game;

/**
 * IScoreLoader
 */
public interface IScoreLoader
{
	public List<Game> getGames(int week);
}
