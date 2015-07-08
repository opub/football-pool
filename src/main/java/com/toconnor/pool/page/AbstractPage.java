package com.toconnor.pool.page;

import com.toconnor.pool.data.UserManager;
import com.toconnor.pool.data.WeekManager;
import com.toconnor.pool.model.User;
import com.toconnor.pool.model.Week;
import com.toconnor.pool.util.Log;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;

/**
 * AbstractPage
 *
 */
public abstract class AbstractPage extends VerticalLayout implements View
{
	private static final Log LOG = new Log(AbstractPage.class);

	//primary method for page to render its contents
	protected abstract void render();

	protected NativeSelect weekSelect = null;

	protected AbstractPage()
	{
        setSizeFull();
        addStyleName("dashboard-view");

		if(allowRender())
		{
			String pageTitle = getTitle();
			LOG.info("PAGE: " + pageTitle);

			//common page title
			Label title = new Label(pageTitle);
			title.setHeight("65px");
			title.addStyleName("h1");
			title.addStyleName("toolbar");

			if(PoolPage.RANK_GAMES.getPageClass() == this.getClass() || PoolPage.WEEKLY_STANDINGS.getPageClass() == this.getClass())
			{
				HorizontalLayout header = new HorizontalLayout();

				header.addComponent(title);
				header.setMargin(false);
				header.setSpacing(false);
				header.setExpandRatio(title, 0.5f);

				weekSelect = getWeekSelect();
				weekSelect.addStyleName("week-select");
				header.addComponent(weekSelect);
				header.setExpandRatio(weekSelect, 0.5f);

				addComponent(header);
				setExpandRatio(header, 0);
			}
			else
			{
				addComponent(title);
				setExpandRatio(title, 0);
			}
		}
	}

    @Override
    public final void enter(ViewChangeListener.ViewChangeEvent event)
    {
	    try
	    {
		    User user = UserManager.getCurrentUser();
		    if(isAdmin())
		    {
			    if(user == null || !user.isAdmin())
			    {
				    //force non-admins back home
				    this.getUI().getNavigator().navigateTo(PoolPage.HOME.getNav());
			    }
		    }
		    else if(!this.getClass().equals(PoolPage.CONFIRM.getPageClass()) && (user == null || !user.isActive()))
		    {
			    //force inactive users to confirmation
			    this.getUI().getNavigator().navigateTo(PoolPage.CONFIRM.getNav());
		    }

		    if(allowRender())
		    {
   			    //perform page specific rendering
			    render();
		    }
	    }
	    catch(Throwable t)
	    {
		    Log log = new Log(this.getClass());
		    log.error("Page rendering failed", t);
	    }
    }

	protected boolean allowRender()
	{
		//this ensures that null or inactive users are not possible in normal pages
		User user = UserManager.getCurrentUser();
		return user != null && user.isActive();
	}

	protected final String getTitle()
	{
		return PoolPage.get(this.getClass()).getTitle();
	}

	protected final boolean isAdmin()
	{
		return PoolPage.get(this.getClass()).isAdmin();
	}

	protected NativeSelect getWeekSelect()
	{
		NativeSelect select = new NativeSelect();
		for (int i = 1; i <= 17; i++)
		{
			select.addItem(i);
			select.setItemCaption(i, "Week " + i);
		}

		select.setNullSelectionAllowed(false);
		select.setImmediate(true);

		Week week = WeekManager.getCurrentWeek();
		if(week.getWeek() == 18)
		{
			select.addItem(18);
			select.setItemCaption(18, "Playoffs");
		}

		select.setValue(week.getWeek());

		return select;
	}
}
