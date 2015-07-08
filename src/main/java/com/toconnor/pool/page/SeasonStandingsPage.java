package com.toconnor.pool.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.toconnor.pool.data.UserManager;
import com.toconnor.pool.data.WeekManager;
import com.toconnor.pool.model.RankedUser;
import com.toconnor.pool.model.User;
import com.toconnor.pool.model.Week;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

/**
 * SeasonStandingsPage
 */
@PageInfo(id="season", title="Season Standings")
public class SeasonStandingsPage extends AbstractPage
{
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
			Table table = createStandingsTable();
			addComponent(table);
			setExpandRatio(table, 1);
		}
	}

	private Table createStandingsTable()
	{
		Table table = new Table();
		table.setEditable(false);
		table.setImmediate(true);
		table.setSortEnabled(true);
		table.setPageLength(35);
		table.setSizeFull();
		table.setFooterVisible(true);
		table.addStyleName("borderless");
		table.setRowHeaderMode(Table.RowHeaderMode.INDEX);
		table.setColumnWidth(null, 30);

		//get week data
		List<Week> weeks = WeekManager.getAllWeeks();
		Map<String, List<Integer>> userPoints = new HashMap<String, List<Integer>>();
		final Map<String, Set<String>> weekWinners = new HashMap<String, Set<String>>();

		//define the names and data types of columns
		table.addContainerProperty("Player", String.class, null);
		for(Week week : weeks)
		{
			//set column info
			String key = week.getWeek() < 18 ? "Wk " + week.getWeek() : "Playoffs";
			table.addContainerProperty(key, Integer.class, null, key, null, Table.Align.CENTER);
			table.setColumnWidth(key, 50);

			if(week.isComplete())
			{
				Set<String> winners = new HashSet<String>();
				weekWinners.put(key, winners);

				for(RankedUser ru : week.getRankedUsers())
				{
					List<Integer> points = userPoints.get(ru.getUserKey());
					if(points == null)
					{
						points = new ArrayList<Integer>();
					}
					points.add(ru.getPoints());
					userPoints.put(ru.getUserKey(), points);

					if(ru.getRank() == 1)
					{
						winners.add(ru.getUserKey());
					}
				}
			}
		}
		if(weeks.size() < 18)
		{
			table.addContainerProperty("Playoffs", Integer.class, null, "Playoffs", null, Table.Align.CENTER);
		}
		table.setColumnWidth("Playoffs", 70);

		table.addContainerProperty("Total", Integer.class, null, "Total", null, Table.Align.CENTER);

		Week week = WeekManager.getCurrentWeek();

		//populate user rows
		List<User> users = UserManager.getActiveUsers();
		for(User user : users)
		{
			List<Object> row = new ArrayList<Object>();
			row.add(user.getDisplayName());

			int total = 0;
			List<Integer> points = userPoints.get(user.getKey());
			if(points != null)
			{
				row.addAll(points);
				for(int pt : points)
				{
					total += pt;
				}
			}

			while(row.size() < week.getWeek())
			{
				//user missed ranking teams for first week(s)
				row.add(1, 0);
			}

			//TODO must account for playoffs
			Object[] cells = row.toArray(new Object[20]);
			cells[19] = total;

			table.addItem(cells, user.getKey());
		}

		//default table sort is by total points desc
		table.sort(new Object[]{"Total"}, new boolean[]{false});

		table.setCellStyleGenerator(new Table.CellStyleGenerator()
		{
			@Override
			public String getStyle(Table t, Object itemID, Object propertyID)
			{
				String userKey = (String)itemID;
				String weekKey = (String)propertyID;
				if(weekKey == null)
				{
					return userKey.equals(UserManager.getCurrentUser().getKey()) ? "player" : null;
				}
				else
				{
					//format weekly winner cells
					Set<String> winners = weekWinners.get(weekKey);
					if(winners != null && winners.contains(userKey))
					{
						return "won";
					}
				}
				return null;
			}
		});

		return table;
	}
}
