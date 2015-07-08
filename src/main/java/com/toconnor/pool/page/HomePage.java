package com.toconnor.pool.page;

import java.text.NumberFormat;

import com.toconnor.pool.data.UserManager;
import com.toconnor.pool.data.WeekManager;
import com.toconnor.pool.model.User;
import com.toconnor.pool.model.Week;
import com.toconnor.pool.util.Formatter;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

/**
 * HomePage
 */
@PageInfo(id="home", title="Home")
public class HomePage extends AbstractPage
{
	@Override
	protected void render()
	{
		HorizontalLayout content = new HorizontalLayout();
		content.setSizeFull();
		content.setMargin(new MarginInfo(true, true, false, true));
		content.setSpacing(true);
		content.addStyleName("content");

		CustomLayout main = new CustomLayout("home");
		main.addStyleName("layout-panel");
		main.addStyleName("padded");
		content.addComponent(main);
		content.setExpandRatio(main, 0.7f);

		VerticalLayout right = new VerticalLayout();
		content.addComponent(right);
		content.setExpandRatio(right, 0.3f);

		VerticalLayout top = new VerticalLayout();
		top.setMargin(true);
		top.addStyleName("layout-panel");
		top.addStyleName("padded");
		right.addComponent(top);

		String winners = WeekManager.getPreviousWeekWinnerText();
		if(winners != null && winners.length() > 0)
		{
			Label congrats = new Label("Congratulations to Last Week's Winner!");
			congrats.addStyleName("h2");
			top.addComponent(congrats);

			Label names = new Label(winners);
			names.addStyleName("h1");
			names.addStyleName("good-message");
			top.addComponent(names);

			top.addComponent(new Label(""));
		}
		Week week = WeekManager.getCurrentWeek();
		if(week != null && week.getWeek() < 18)
		{
			Label current = new Label("Currently Week #" + week.getWeek());
			current.addStyleName("h2");
			top.addComponent(current);
		}
		right.addComponent(new Label(""));

		VerticalLayout status = new VerticalLayout();
		status.setMargin(true);
		status.addStyleName("layout-panel");
		status.addStyleName("padded");
		addStatusContent(status);
		right.addComponent(status);

		addComponent(content);
		setExpandRatio(content, 1);
	}

	private void addStatusContent(Layout layout)
	{
		User user = UserManager.getCurrentUser();

		Label welcome = new Label("Welcome " + user.fetchFullName());
		welcome.addStyleName("h1");
		welcome.addStyleName("good-message");
		layout.addComponent(welcome);

		layout.addComponent(new Label(""));

		Label email = new Label("Email: " + user.getEmail());
		email.addStyleName("h2");
		layout.addComponent(email);

		Label source = new Label("Provider: " + Formatter.formatProvider(user.getProvider()));
		source.addStyleName("h2");
		layout.addComponent(source);

		Label paid = new Label();
		paid.addStyleName("h2");
		if(user.isPaid())
		{
			paid.setValue("Payment Status: PAID");
		}
		else
		{
			paid.setValue("Payment Status: NOT PAID");
			paid.addStyleName("bad-message");
		}
		layout.addComponent(paid);

		String dollar = NumberFormat.getCurrencyInstance().format(user.getWinnings());
		Label winnings = new Label("Winnings: " + dollar);
		winnings.addStyleName("h2");
		layout.addComponent(winnings);

		layout.addComponent(new Label(""));
	}
}
