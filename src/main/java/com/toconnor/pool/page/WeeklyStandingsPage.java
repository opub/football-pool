package com.toconnor.pool.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.toconnor.pool.data.GameManager;
import com.toconnor.pool.data.PlayoffManager;
import com.toconnor.pool.data.UserManager;
import com.toconnor.pool.data.WeekManager;
import com.toconnor.pool.model.Game;
import com.toconnor.pool.model.PlayoffTeam;
import com.toconnor.pool.model.RankedGame;
import com.toconnor.pool.model.RankedTeam;
import com.toconnor.pool.model.User;
import com.toconnor.pool.model.Week;
import com.vaadin.data.Property;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

/**
 * WeeklyStandingsPage
 */
@PageInfo(id="week", title="Weekly Standings")
public class WeeklyStandingsPage extends AbstractPage
{
	private static class Points implements Serializable, Comparable<Points>
	{
		int won = 0;
		int winning = 0;
		int losing = 0;
		int lost;

		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (!(o instanceof Points)) return false;

			Points points = (Points) o;

			if (losing != points.losing) return false;
			if (lost != points.lost) return false;
			if (winning != points.winning) return false;
			return won == points.won;
		}

		@Override
		public int hashCode()
		{
			int result = won;
			result = 31 * result + winning;
			result = 31 * result + losing;
			result = 31 * result + lost;
			return result;
		}

		@Override
		public int compareTo(Points p)
		{
			final int BEFORE = -1;
			final int SAME = 0;
			final int AFTER = 1;

			if(p == null) return BEFORE;

			if(this.lost < p.lost) return BEFORE;
			if(this.won > p.won) return BEFORE;
			if(this.losing < p.losing) return BEFORE;
			if(this.winning > p.winning) return BEFORE;

			if(this.equals(p)) return SAME;

			return AFTER;
		}
	}

	private int currentWeek;
	private Table table;
	private Map<String, Game> gameMap;
	private Map<String, Points> userPoints;
	private Map<String, RankedGame> rankedGames;

	@Override
	protected void render()
	{
		if(!UserManager.getCurrentUser().isPaid())
		{
			CssLayout content = new CssLayout();
			content.addStyleName("content");
			Label payup = new Label("This page can only be viewed after you have paid your entry fee.");
			payup.addStyleName("error");
			content.addComponent(payup);
			addComponent(content);
			setExpandRatio(content, 1);
		}
		else
		{
			currentWeek = getDefaultWeekToDisplay();
			populateTable(currentWeek);

			addComponent(table);
			setExpandRatio(table, 1);

			weekSelect.setValue(currentWeek);
			weekSelect.addValueChangeListener(new Field.ValueChangeListener()
			{
				@Override
				public void valueChange(Property.ValueChangeEvent event)
				{
					int week = Integer.parseInt(event.getProperty().getValue().toString());
					if(week != currentWeek)
					{
						currentWeek = week;
						removeComponent(table);
						table.removeAllItems();
						populateTable(week);
						addComponent(table);
						setExpandRatio(table, 1);
					}
				}
			});
		}
	}

	private int getDefaultWeekToDisplay()
	{
		Week current = WeekManager.getCurrentWeek();
		int number = current.getWeek();
		Date start = current.getFirstGame();
		if(start != null && start.after(new Date()) && number > 1)
		{
			//display previous week until this week starts
			return number - 1;
		}
		return number;
	}

	private void populateTable(int week)
	{
		table = new Table();
		table.setEditable(false);
		table.setImmediate(true);
		table.setSortEnabled(true);
		table.setPageLength(35);
		table.setSizeFull();
		table.setFooterVisible(true);
		table.addStyleName("borderless");
		table.addStyleName("tighten");
		table.setRowHeaderMode(Table.RowHeaderMode.INDEX);
		table.setColumnWidth(null, 30);

		table.setCellStyleGenerator(new Table.CellStyleGenerator()
		{
			@Override
			public String getStyle(Table t, Object itemID, Object propertyID)
			{
				String userKey = (String)itemID;
				String gameKey = (String)propertyID;
				if(gameKey == null)
				{
					return userKey.equals(UserManager.getCurrentUser().getKey()) ? "player" : null;
				}
				else if(gameMap != null)
				{
					Game game = gameMap.get(gameKey);

					//only apply special style when game has started and one team is ahead
					if(game != null && game.hasStarted() && game.fetchWinningTeamCode() != null)
					{
						RankedGame ranked = rankedGames.get(RankedGame.getKey(gameKey, userKey));
						if(ranked != null)
						{
							if(game.isFinalScore())
							{
								return game.fetchWinningTeamCode().equals(ranked.getWinner()) ? "won" : "lost";
							}
							else
							{
								return game.fetchWinningTeamCode().equals(ranked.getWinner()) ? "winning" : "losing";
							}
						}
					}
					return null;
				}
				else
				{
					return null;
				}
			}
		});

		if(week < 18)
		{
			populateRegularWeek(week);
		}
		else
		{
			populatePlayoffs();
		}
	}

	private void populatePlayoffs()
	{
		List<PlayoffTeam> teams = PlayoffManager.getPlayoffs();

		Map<String, PlayoffTeam> playoffTeams = new HashMap<>();

		//define the names and data types of columns
		table.addContainerProperty("Player", String.class, null);
		for(PlayoffTeam pt : teams)
		{
			String code = pt.getTeam().getCode();
			table.addContainerProperty(code, Integer.class, null, code, null, Table.Align.CENTER);
			table.setColumnWidth(code, 60);
			table.setColumnFooter(code, code + " " + pt.getWins());
			playoffTeams.put(code, pt);
		}
		table.addContainerProperty("PtsWon", Integer.class, null, "Pts Won", null, Table.Align.CENTER);

		boolean started = WeekManager.getWeek(18).hasStarted();

		//populate user rows
		List<User> users = UserManager.getActiveUsers();
		for(User user : users)
		{
			if(user.getRankedTeams() != null && !user.getRankedTeams().isEmpty())
			{
				int pts = 12;
				int total = 0;
				Map<String, Integer> ranks = new HashMap<>();
				for(RankedTeam rt : user.getRankedTeams())
				{
					String code = rt.getTeam().getCode();
					if(playoffTeams.containsKey(code))
					{
						total += pts * playoffTeams.get(code).getWins();
						ranks.put(code, pts--);
					}
				}

				List<Object> row = new ArrayList<>();
				row.add(user.getDisplayName());

				for(PlayoffTeam pt : teams)
				{
					if(started)
					{
						row.add(ranks.get(pt.getTeam().getCode()));
					}
					else
					{
						row.add(0);
					}
				}

				row.add(total);

				if(row.size() > 4)
				{
					table.addItem(row.toArray(), user.getKey());
				}
			}
		}

		//default table sort is by won points desc then name
		table.sort(new Object[]{"PtsWon", "Player"}, new boolean[]{false, true});
	}

	private void populateRegularWeek(int week)
	{
		//get game data
		List<Game> games = GameManager.getGames(week);
		Collections.sort(games);
		populateMaps(week, games);

		//define the names and data types of columns
		table.addContainerProperty("Player", String.class, null);
		for(Game game : games)
		{
			String key = game.getKey();
			table.addContainerProperty(key, String.class, null, game.fetchLabel(), null, Table.Align.CENTER);
			table.setColumnWidth(key, 60);
			table.setColumnFooter(key, formatGameFooter(game));
			gameMap.put(key, game);
		}
		table.addContainerProperty("PtsWon", Integer.class, null, "Pts Won", null, Table.Align.CENTER);
		table.addContainerProperty("PtsLost", Integer.class, null, "Pts Lost", null, Table.Align.CENTER);

		//populate user rows
		List<User> users = UserManager.getActiveUsers();
		for(User user : users)
		{
			List<Object> row = new ArrayList<>();
			row.add(user.getDisplayName());

			for(Game game : games)
			{
				if(game.hasStarted())
				{
					RankedGame rg = rankedGames.get(RankedGame.getKey(game.getKey(), user.getKey()));
					if(rg != null)
					{
						row.add(rg.getWinner() + "\n" + rg.getRank());
					}
				}
				else
				{
					row.add("?");
				}
			}

			Points pts = userPoints.get(user.getKey());
			if(pts != null)
			{
				row.add(pts.winning);
				row.add(pts.losing);
			}
			else
			{
				row.add(0);
				row.add(0);
			}

			if(row.size() > 4)
			{
				table.addItem(row.toArray(), user.getKey());
			}
		}

		//default table sort is by lost points, won points desc then name
		table.sort(new Object[]{"PtsLost", "PtsWon", "Player"}, new boolean[]{true, false, true});
	}

	private String formatGameFooter(Game game)
	{
		return game.getAwayTeam().getCode() + " " + game.getAwayScore() + "<br>" + game.getHomeTeam().getCode() + " " + game.getHomeScore();
	}

	private void populateMaps(int week, List<Game> games)
	{
		gameMap = new HashMap<>();
		userPoints = new HashMap<>();
		rankedGames = new HashMap<>();

		//put week's games into map for easier lookup later
		Map<String, Game> gameMap = new HashMap<>();
		for(Game g : games)
		{
			gameMap.put(g.getKey(), g);
		}

		//determine total points for each user
		List<RankedGame> rankedList = GameManager.getRankedGames(week);
		for(RankedGame rg : rankedList)
		{
			Points pts = userPoints.get(rg.getUserKey());
			if(pts == null)
			{
				pts = new Points();
			}

			Game g = gameMap.get(rg.getGameKey());
			if(rg.getWinner().equals(g.fetchWinningTeamCode()))
			{
				pts.winning += rg.getRank();
				if(g.isFinalScore())
				{
					pts.won += rg.getRank();
				}
			}
			else if(g.fetchWinningTeamCode() != null)
			{
				pts.losing += rg.getRank();
				if(g.isFinalScore())
				{
					pts.lost += rg.getRank();
				}
			}

			userPoints.put(rg.getUserKey(), pts);
			rankedGames.put(rg.getKey(), rg);
		}
	}
}
