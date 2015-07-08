package com.toconnor.pool.scores;

/**
 * ScoreLoaderFactory
 */
public class ScoreLoaderFactory
{
	public static IScoreLoader get()
	{
		return new NFLScoreLoader();
	}

	private ScoreLoaderFactory() {}
}
