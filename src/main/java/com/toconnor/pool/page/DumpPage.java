package com.toconnor.pool.page;

import java.util.Date;
import java.util.List;

import com.toconnor.pool.data.WeekManager;
import com.toconnor.pool.model.Week;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Dumps database values
 */
@PageInfo(id="dump", title="Database Dump", adminOnly=true)
public class DumpPage extends AbstractPage
{
	@Override
	protected void render()
	{
		CssLayout content = new CssLayout();
		content.addStyleName("content");

		VerticalLayout panel = new VerticalLayout();
		panel.addStyleName("layout-panel");
		panel.addStyleName("padded");
		content.addComponent(panel);

		Label time = new Label("NOW = " + new Date());
		panel.addComponent(time);

		Label weeksLabel = new Label("Weeks");
		weeksLabel.addStyleName("subtitle");
		panel.addComponent(weeksLabel);

		List<Week> weeks = WeekManager.getAllWeeks();
		if(weeks != null)
		{
			for(Week w : weeks)
			{
				panel.addComponent(new Label(w.toString()));
			}
		}

		addComponent(content);
		setExpandRatio(content, 1);
	}
}
