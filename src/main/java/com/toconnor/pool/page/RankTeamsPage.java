package com.toconnor.pool.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.toconnor.pool.Team;
import com.toconnor.pool.data.GameManager;
import com.toconnor.pool.data.UserManager;
import com.toconnor.pool.data.WeekManager;
import com.toconnor.pool.model.Game;
import com.toconnor.pool.model.RankedGame;
import com.toconnor.pool.model.RankedTeam;
import com.toconnor.pool.model.User;
import com.toconnor.pool.model.Week;
import com.toconnor.pool.page.layout.SortableLayout;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;

/**
 * RankTeamsPage
 */
@PageInfo(id="teams", title="Rank Teams")
public class RankTeamsPage extends AbstractPage implements SortableLayout.ISortListener
{
	@Override
	protected void render()
    {
        CssLayout content = new CssLayout();
        content.addStyleName("content");

	    HorizontalLayout layout = new HorizontalLayout();
	    layout.setMargin(new MarginInfo(true, true, false, true));
	    layout.setSpacing(true);
	    content.addComponent(layout);

	    Week week = WeekManager.getCurrentWeek();
	    boolean active = week.getWeek() < 18 || !week.hasStarted();

	    final SortableLayout sortable = new SortableLayout(this);
        sortable.setSizeUndefined();
        sortable.setHeight("850px");
        for (Component component : createComponents(active))
        {
            sortable.addComponent(component, active);
        }
	    layout.addComponent(sortable);

	    if(week.getWeek() < 18)
	    {
		    Button apply = new Button("Apply to Future Weeks");
		    apply.addClickListener(new Button.ClickListener()
		    {
			    @Override
			    public void buttonClick(Button.ClickEvent event)
			    {
				    Notification.show("Game ranks for future weeks are being updated now.  It may take a \nminute or so before the new ranks are reflected on the Games page.");
				    List<RankedTeam> rankedTeams = getRankedTeams(sortable.getComponents());
				    updateGames(rankedTeams);
			    }
		    });
		    layout.addComponent(apply);
	    }

        addComponent(content);
        setExpandRatio(content, 1);
    }

	//TODO THIS IS TOO EXPENSIVE.  NEEDS MORE CACHING.
	private void updateGames(List<RankedTeam> rankedTeams)
	{
		List<Game> games = GameManager.getFutureWeekGames();
		if(games != null && games.size() > 0)
		{
			User user = UserManager.getCurrentUser();

			Map<Team, Integer> teamMap = new HashMap<>();
			for(RankedTeam rt : rankedTeams)
			{
				teamMap.put(rt.getTeam(), rt.getRank());
			}

			Map<String, Game> gameMap = new HashMap<>();
			for(Game g : games)
			{
				gameMap.put(g.getKey(), g);
			}

			List<RankedGame> weekGames = new ArrayList<>();
			while(!games.isEmpty())
			{
				Game game = games.remove(0);
				weekGames.add(new RankedGame(game, user));
				if(games.isEmpty() || games.get(0).getWeek() != game.getWeek())
				{
					updateWeekGames(teamMap, gameMap, weekGames);
					weekGames.clear();
				}
			}
		}
	}

	private void updateWeekGames(final Map<Team, Integer> teamMap, final Map<String, Game> gameMap, List<RankedGame> weekGames)
	{
		Collections.sort(weekGames, new Comparator<RankedGame>()
		{
			@Override
			public int compare(RankedGame game1, RankedGame game2)
			{
				final int BEFORE = -1;
				final int AFTER = 1;

				Game g1 = gameMap.get(game1.getGameKey());
				Game g2 = gameMap.get(game2.getGameKey());

				int home1 = teamMap.get(g1.getHomeTeam());
				int home2 = teamMap.get(g2.getHomeTeam());
				int away1 = teamMap.get(g1.getAwayTeam());
				int away2 = teamMap.get(g2.getAwayTeam());

				//side effect of setting winner
				game1.setWinner((home1 > away1) ? g1.getAwayTeam() : g1.getHomeTeam());
				game2.setWinner((home2 > away2) ? g2.getAwayTeam() : g2.getHomeTeam());

				int abs1 = Math.abs(home1 - away1);
				int abs2 = Math.abs(home2 - away2);

				if(abs1 > abs2) return BEFORE;
				if(abs2 > abs1) return AFTER;

				return g1.compareTo(g2);
			}
		});

		int rank = 16;
		for(RankedGame rg : weekGames)
		{
			rg.setRank(rank--);
		}

		GameManager.saveRankedGames(weekGames);
	}

	private List<Component> createComponents(boolean active)
    {
        List<Component> components = new ArrayList<>();

        int index = 1;

	    Week week = WeekManager.getCurrentWeek();
	    int value = 12;

	    List<Team> rankedTeams = getPreviouslyRankedTeams();

        for(Team team : rankedTeams)
        {
            HorizontalLayout layout = new HorizontalLayout();
            layout.addStyleName(active ? "sortable-item" : "unsortable-item");
            layout.setHeight("20px");

            Label rank = week.getWeek() < 18 ? new Label("" + index++) : new Label("" + value--);
            rank.setWidth("35px");
            layout.addComponent(rank);

            Label label = new Label(team.getName());
            label.setWidth("250px");
            layout.addComponent(label);

            components.add(layout);
        }

        return components;
    }

    @Override
    public void handleSortEvent(List<Component> components)
    {
	    List<RankedTeam> rankedTeams = getRankedTeams(components);

	    //persist changes on user
	    User user = UserManager.getCurrentUser();
	    user.setRankedTeams(rankedTeams);
	    UserManager.saveUser(user);
    }

	private List<RankedTeam> getRankedTeams(List<Component> components)
	{
		List<RankedTeam> rankedTeams = new ArrayList<>();
		int index = 0;

		Week week = WeekManager.getCurrentWeek();
		int value = 12;

		for(Component comp : components)
		{
		    HorizontalLayout row = (HorizontalLayout)comp;

			//update rank label based on new position
			Label rank = (Label)row.getComponent(0);
			if(week.getWeek() < 18)
			{
				rank.setValue(String.valueOf(index + 1));
			}
			else
			{
				rank.setValue(String.valueOf(value--));
			}

			Label label = (Label)row.getComponent(1);
			String teamName = label.getValue();

			//build ranked team
			RankedTeam rt = new RankedTeam(index++, Team.get(teamName));
			rankedTeams.add(rt);
		}

		return rankedTeams;
	}

	private List<Team> getPreviouslyRankedTeams()
	{
		User user = UserManager.getCurrentUser();
		Week week = WeekManager.getCurrentWeek();

		List<RankedTeam> rankedTeams = user.getRankedTeams();
		if(rankedTeams.isEmpty() || week.getWeek() < 18 && rankedTeams.size() < 32)
		{
			return week.getWeek() < 18 ? Team.ALL_TEAMS : Team.PLAYOFF_TEAMS;
		}

		List<Team> teams = new ArrayList<>();
		for(RankedTeam rt : rankedTeams)
		{
			if(week.getWeek() < 18 || Team.PLAYOFF_TEAMS.contains(rt.getTeam()))
			{
				teams.add(rt.getTeam());
			}
		}
		return teams;
	}
}
