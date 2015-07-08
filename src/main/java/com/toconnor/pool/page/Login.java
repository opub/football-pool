package com.toconnor.pool.page;

import com.toconnor.pool.PoolConstants;
import com.toconnor.pool.PoolUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Login
 */
public class Login implements PoolConstants
{
	private static final String LOGIN_WIDGET = "window.poolLoginWidget();";

    public static VerticalLayout getLoginView(final PoolUI pool)
    {
	    VerticalLayout loginPage = new VerticalLayout();
        loginPage.setSizeFull();
        loginPage.addStyleName("login-layout");

	    GridLayout loginPanel = new GridLayout();
	    loginPanel.setWidth("640px");
	    loginPanel.setHeight("225px");
	    loginPanel.setRows(2);
	    loginPanel.setColumns(2);
	    loginPanel.addStyleName("login-panel");

        Label title = new Label(POOL_FULL_TITLE);
	    title.setWidth("275px");
	    title.addStyleName("h1");
	    loginPanel.addComponent(title, 0, 0);
	    loginPanel.setComponentAlignment(title, Alignment.MIDDLE_LEFT);

	    Label welcome = new Label("How would you like to login?");
	    welcome.setWidth("240px");
	    welcome.addStyleName("h2");
	    loginPanel.addComponent(welcome, 1, 0);
	    loginPanel.setComponentAlignment(welcome, Alignment.MIDDLE_RIGHT);

	    Label widget = new Label("LOGIN WIDGET");
	    widget.setId("social_login_container");
	    widget.setWidth("335px");
	    loginPanel.addComponent(widget, 0, 1, 1, 1);
	    loginPanel.setComponentAlignment(widget, Alignment.TOP_CENTER);

        loginPage.addComponent(loginPanel);
        loginPage.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);

	    pool.getPage().getJavaScript().execute(LOGIN_WIDGET);

	    return loginPage;
    }
}
