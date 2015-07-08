package com.toconnor.pool.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.toconnor.pool.Team;
import com.toconnor.pool.data.GameManager;
import com.toconnor.pool.data.UserManager;
import com.toconnor.pool.data.WeekManager;
import com.toconnor.pool.model.Game;
import com.toconnor.pool.model.RankedGame;
import com.toconnor.pool.model.User;
import com.toconnor.pool.page.layout.SortableLayout;
import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * RankGamesPage
 */
@PageInfo(id="games", title="Rank Games")
public class RankGamesPage extends AbstractPage implements SortableLayout.ISortListener
{
	private SortableLayout sortable;

	@Override
	protected void render()
	{
		CssLayout content = new CssLayout();
		content.addStyleName("content");

		int week = WeekManager.getCurrentWeek().getWeek();

		List<Game> games = GameManager.getGames(week);
		List<RankedGame> rankedGames = GameManager.getUserRankedGames(week);
		if(games.size() != rankedGames.size())
		{
			Label error = new Label("You must rank your teams first.  Please note that game updates might take a minute to propagate after you apply your team rankings.");
			error.addStyleName("error");
			content.addComponent(error);
		}
		else
		{
			VerticalLayout layout = new VerticalLayout();
			content.addComponent(layout);

			sortable = new SortableLayout(this);
			sortable.setSizeUndefined();
			sortable.setHeight("850px");
			populateSortable(games, rankedGames);
			layout.addComponent(sortable);
		}

		addComponent(content);
		setExpandRatio(content, 1);

		weekSelect.addValueChangeListener(new Field.ValueChangeListener()
		{
			@Override
			public void valueChange(Property.ValueChangeEvent event)
			{
				int week = Integer.parseInt(event.getProperty().getValue().toString());
				List<Game> games = GameManager.getGames(week);
				List<RankedGame> rankedGames = GameManager.getUserRankedGames(week);
				populateSortable(games, rankedGames);
			}
		});
	}

	private void populateSortable(List<Game> games, List<RankedGame> rankedGames)
	{
		if(sortable.getComponentCount() > 0)
		{
			sortable.removeAllComponents();
		}

		for (GameComponent gc : createComponents(games, rankedGames))
		{
			sortable.addComponent(gc.component, !gc.game.hasStarted());
		}
	}

	private List<GameComponent> createComponents(List<Game> games, List<RankedGame> rankedGames)
	{
		List<GameComponent> components = new ArrayList<GameComponent>();

		Map<String, Game> gameMap = new HashMap<String, Game>();
		for(Game g : games)
		{
			gameMap.put(g.getKey(), g);
		}

		Collections.sort(rankedGames);

		int index = 16;

		for(RankedGame rankedGame : rankedGames)
		{
			Game game = gameMap.get(rankedGame.getGameKey());
			components.add(createGameComponent(rankedGame, game, index));
			index--;
		}

		return components;
	}

	private GameComponent createGameComponent(final RankedGame rankedGame, Game game, int index)
	{
		boolean started = game.hasStarted();

		HorizontalLayout layout = new HorizontalLayout();
		layout.addStyleName(started ? "unsortable-item" : "sortable-item");
		layout.setHeight("20px");

		//rank
		Label rank = new Label("" + index);
		rank.setId(game.getKey());
		rank.setWidth("50px");
		layout.addComponent(rank);

		//date and time
		Label date = new Label(game.getGameTime());
		date.setWidth("150px");
		layout.addComponent(date);

		//away team
		Team awayTeam = game.getAwayTeam();
		final Button away = getGameButton(awayTeam, rankedGame.getWinner(), started);
		layout.addComponent(away);

		//at
		Label at = new Label(" at ");
		at.setWidth("40px");
		layout.addComponent(at);

		//home team
		Team homeTeam = game.getHomeTeam();
		final Button home = getGameButton(homeTeam, rankedGame.getWinner(), started);
		layout.addComponent(home);

		if(!started)
		{
			away.addClickListener(new Button.ClickListener()
			{
				@Override
				public void buttonClick(Button.ClickEvent event)
				{
					toggleWinner(away, home, rankedGame.getWeek());
				}
			});
			home.addClickListener(new Button.ClickListener()
			{
				@Override
				public void buttonClick(Button.ClickEvent event)
				{
					toggleWinner(home, away, rankedGame.getWeek());
				}
			});
		}

		return new GameComponent(game, layout);
	}

	private Button getGameButton(Team team, String winner, boolean started)
	{
		Button button = new Button(team.getName());
		button.setWidth("180px");
		button.addStyleName("game");
		if(team.getCode().equals(winner))
		{
			button.setData(true);
			button.addStyleName(started ? "game-winner-started" : "game-winner");
		}
		else
		{
			button.setData(false);
			button.addStyleName(started ? "game-loser-started" : "game-loser");
		}
		return button;
	}

	private void toggleWinner(Button winner, Button loser, int week)
	{
		winner.removeStyleName("game-loser");
		winner.addStyleName("game-winner");
		winner.setData(true);

		loser.removeStyleName("game-winner");
		loser.addStyleName("game-loser");
		loser.setData(false);

		saveRankedGame(winner.getParent(), week);
	}

	private void saveRankedGame(Component c, int week)
	{
		User user = UserManager.getCurrentUser();
		HorizontalLayout row = (HorizontalLayout)c;
		RankedGame rg = getRankedGameFromRow(row, week, user);

		GameManager.saveRankedGame(rg);
	}

	@Override
	public void handleSortEvent(List<Component> components)
	{
		int week = Integer.parseInt(weekSelect.getValue().toString());
		User user = UserManager.getCurrentUser();

		int rank = 16;

		List<RankedGame> rankedGames = new ArrayList<RankedGame>();

		for(Component c : components)
		{
			HorizontalLayout row = (HorizontalLayout)c;

			//update displayed rank in row since we just resorted
			Label label = (Label)row.getComponent(0);
			label.setValue(String.valueOf(rank--));

			RankedGame rg = getRankedGameFromRow(row, week, user);
			rankedGames.add(rg);
		}

		GameManager.saveRankedGames(rankedGames);
	}

	private RankedGame getRankedGameFromRow(HorizontalLayout row, int week, User user)
	{
		//update rank label based on new position
		Label label = (Label)row.getComponent(0);
		int rank = Integer.valueOf(label.getValue());
		String gameKey = label.getId();

		Team winner = null;
		Button away = (Button)row.getComponent(2);
		if(away.getData() != null && (Boolean)away.getData())
		{
			winner = Team.get(away.getCaption());
		}
		else
		{
			Button home = (Button)row.getComponent(4);
			if(home.getData() != null && (Boolean)home.getData())
			{
				winner = Team.get(home.getCaption());
			}
		}

		return new RankedGame(rank, week, gameKey, user, winner);
	}

	private static class GameComponent
	{
		final Game game;
		final Component component;

		private GameComponent(Game game, Component component)
		{
			this.game = game;
			this.component = component;
		}
	}
}
