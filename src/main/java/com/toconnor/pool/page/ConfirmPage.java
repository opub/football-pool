package com.toconnor.pool.page;

import com.toconnor.pool.PoolUI;
import com.toconnor.pool.data.UserManager;
import com.toconnor.pool.model.User;
import com.toconnor.pool.util.Formatter;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * ConfirmPage
 */
@PageInfo(id="confirm", title="Confirm New Account")
public class ConfirmPage extends AbstractPage
{
	@Override
	protected void render()
	{
		final User user = UserManager.getCurrentUser();
		if(user.isActive())
		{
			//this page is only for first time inactive users
			return;
		}

		CssLayout content = new CssLayout();
		content.addStyleName("content");

		VerticalLayout panel = new VerticalLayout();
		panel.addStyleName("layout-panel");
		panel.addStyleName("padded");
		panel.setMargin(true);
		content.addComponent(panel);

		Label about = new Label("This is the first time that you are attempting to use this account.  " +
				"If this is your first time accessing the site this season then please just verify the account information below.  " +
				"If this is not your first time then it means that you are using a different account than your first visit.  " +
				"This might be because you chose the wrong login provider or are using a shared computer and someone else is logged in.");
		about.addStyleName("h2");
		about.addStyleName("v-table-cell-content-losing");
		panel.addComponent(about);

		panel.addComponent(new Label(""));

		Label name = new Label("Name: " + user.fetchFullName());
		name.addStyleName("h1");
		panel.addComponent(name);

		panel.addComponent(new Label(""));

		Label email = new Label("Email: " + user.getEmail());
		email.addStyleName("h1");
		panel.addComponent(email);

		panel.addComponent(new Label(""));

		Label source = new Label("Provider: " + Formatter.formatProvider(user.getProvider()));
		source.addStyleName("h1");
		panel.addComponent(source);

		panel.addComponent(new Label(""));

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setMargin(true);
		buttons.setWidth(650, Unit.PIXELS);
		buttons.setHeight(100, Unit.PIXELS);
		panel.addComponent(buttons);

		Button yes = new Button("Yes, this is the account I want to use.");
		yes.addClickListener(new Button.ClickListener()
		{
			@Override
			public void buttonClick(Button.ClickEvent clickEvent)
			{
				user.setActive(true);
				UserManager.saveUser(user);
				UserManager.updateCurrentUser(user);
				UI.getCurrent().getNavigator().navigateTo(PoolPage.HOME.getNav());
			}
		});
		buttons.addComponent(yes);
		buttons.setComponentAlignment(yes, Alignment.MIDDLE_CENTER);

		Button oops = new Button("Oops, I logged in with the wrong account.");
		oops.addClickListener(new Button.ClickListener()
		{
			@Override
			public void buttonClick(Button.ClickEvent clickEvent)
			{
				UserManager.deleteUser(user);
				((PoolUI)UI.getCurrent()).buildLoginView(true);
			}
		});
		buttons.addComponent(oops);
		buttons.setComponentAlignment(oops, Alignment.MIDDLE_CENTER);

		addComponent(content);
		setExpandRatio(content, 1);
	}

	protected boolean allowRender()
	{
		//this ensures that only inactive users are confirmed
		User user = UserManager.getCurrentUser();
		return user != null && !user.isActive();
	}
}
