package com.toconnor.pool;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import com.toconnor.pool.data.TalkManager;
import com.toconnor.pool.data.UserManager;
import com.toconnor.pool.data.WeekManager;
import com.toconnor.pool.model.User;
import com.toconnor.pool.model.Week;
import com.toconnor.pool.page.Login;
import com.toconnor.pool.page.PoolPage;
import com.toconnor.pool.ui.ConverterFactory;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

/**
 * PoolUI
 */
@Theme("dashboard")
@Title(PoolConstants.POOL_FULL_TITLE)
public class PoolUI extends UI implements PoolConstants, Serializable
{
    private static final long serialVersionUID = 1L;

    CssLayout root = new CssLayout();
    CssLayout menu = new CssLayout();
    CssLayout content = new CssLayout();

    HashMap<String, Button> viewNameToMenuButton = new HashMap<>();

    Navigator navigator;
    VerticalLayout loginLayout;

    HashMap<String, Class<? extends View>> routes = new HashMap<String, Class<? extends View>>()
    {
        {
            for(PoolPage page : PoolPage.ALL_PAGES)
            {
                put(page.getNav(), page.getPageClass());
            }
        }
    };

    @Override
    protected void init(VaadinRequest request)
    {
        getSession().setConverterFactory(new ConverterFactory());
	    getSession().addBootstrapListener(new BootstrapListener()
	    {
		    @Override
		    public void modifyBootstrapPage(BootstrapPageResponse response)
		    {
			    Element script = new Element(Tag.valueOf("script"), "");
			    script.attr("type", "text/javascript");
			    script.attr("src", "/VAADIN/js/loginWidget.js");
			    response.getDocument().head().appendChild(script);
		    }

		    @Override
		    public void modifyBootstrapFragment(BootstrapFragmentResponse response)
		    {
			    //no-op
		    }
	    });

        setLocale(Locale.US);

        setContent(root);
        root.addStyleName("root");
        root.setSizeFull();

	    User user = UserManager.getCurrentUser();
        if (user != null)
        {
            buildMainView(user);
        }
        else
        {
            buildLoginView(false);
        }
    }

    public void buildLoginView(boolean exit)
    {
        if (exit)
        {
	        UserManager.clearCurrentUser();
            root.removeAllComponents();
        }
        else
        {
            // Unfortunate to use an actual widget here, but since CSS generated
            // elements can't be transitioned yet, we must
            Label bg = new Label();
            bg.setSizeUndefined();
            bg.addStyleName("login-bg");
            root.addComponent(bg);
        }

        addStyleName("login");

        loginLayout = Login.getLoginView(this);
        root.addComponent(loginLayout);
    }

    public void buildMainView(final User user)
    {
        navigator = new PoolNavigator(this, content);

        for (String route : routes.keySet())
        {
            navigator.addView(route, routes.get(route));
        }

        root.addComponent(new HorizontalLayout()
        {
            {
                setSizeFull();
                addStyleName("main-view");
                addComponent(new VerticalLayout()
                {
                    // Sidebar
                    {
                        addStyleName("sidebar");
                        setWidth(null);
                        setHeight("100%");

                        // Branding element
                        addComponent(new CssLayout()
                        {
                            {
                                addStyleName("branding");
                                Label logo = new Label("<span>" + POOL_TITLE + "</span> " + POOL_YEAR, ContentMode.HTML);
                                logo.setSizeUndefined();
                                addComponent(logo);
                            }
                        });

                        // Main menu
                        addComponent(menu);
                        setExpandRatio(menu, 1);

                        // User menu
                        addComponent(new VerticalLayout()
                        {
                            {
                                setSizeUndefined();
                                addStyleName("user");
                                Image profilePic = new Image(null, new ThemeResource("img/profile-pic.png"));
                                profilePic.setWidth("34px");
                                addComponent(profilePic);
                                Label userName = new Label(user.getDisplayName());
                                userName.setSizeUndefined();
                                addComponent(userName);
                            }
                        });
                    }
                });
                // Content
                addComponent(content);
                content.setSizeFull();
                content.addStyleName("view-content");
                setExpandRatio(content, 1);
            }

        });

        menu.removeAllComponents();

	    Week week = WeekManager.getCurrentWeek();

        //main pool page menu links
        for(PoolPage page : PoolPage.NAV_PAGES)
        {
            if(!page.isAdmin() || user.isAdmin())
            {
	            if(week.getWeek() < 18 || !PoolPage.RANK_GAMES.equals(page))
	            {
			        final String id = page.getId();
		            final String nav = page.getNav();
		            String caption = id.toUpperCase();
		            Button b = new NativeButton(caption);
		            b.addStyleName("icon-" + id);
		            b.addClickListener(new ClickListener()
		            {
		                @Override
		                public void buttonClick(ClickEvent event)
		                {
		                    clearMenuSelection();
		                    event.getButton().addStyleName("selected");
		                    if (!navigator.getState().equals(nav)) navigator.navigateTo(nav);
		                }
		            });

		            //talk button gets an unread badge
		            if(PoolPage.TALK.equals(page))
		            {
			            int talks = TalkManager.getNewCount();
			            if(talks > 0)
			            {
				            b.setHtmlContentAllowed(true);
							b.setCaption(caption + "<span class=\"badge\">" + talks + "</span>");
			            }
		            }

		            menu.addComponent(b);
		            viewNameToMenuButton.put(nav, b);
	            }
            }
        }

	    Button exit = new NativeButton("EXIT");
	    exit.addStyleName("icon-logout");
	    exit.setDescription("Sign Out");
	    exit.addClickListener(new ClickListener()
	    {
		    @Override
		    public void buttonClick(ClickEvent event)
		    {
			    buildLoginView(true);
		    }
	    });
	    menu.addComponent(exit);

        menu.addStyleName("menu");
        menu.setHeight("100%");

        String f = Page.getCurrent().getUriFragment();
        if (f != null && f.startsWith("!"))
        {
            f = f.substring(1);
        }
        if (f == null || f.equals("") || f.equals("/"))
        {
	        navTo(PoolPage.HOME.getNav());
            menu.getComponent(0).addStyleName("selected");
        }
        else
        {
	        navTo(f);
            if(viewNameToMenuButton.containsKey(f))
            {
	            viewNameToMenuButton.get(f).addStyleName("selected");
            }
        }
    }

	@SuppressWarnings("deprecation")
	private void clearMenuSelection()
    {
	    for (Iterator<Component> it = menu.getComponentIterator(); it.hasNext(); )
        {
            Component next = it.next();
            if (next instanceof NativeButton)
            {
                next.removeStyleName("selected");
            } else if (next instanceof DragAndDropWrapper)
            {
                // Wow, this is ugly (even uglier than the rest of the code)
                ((DragAndDropWrapper) next).iterator().next().removeStyleName("selected");
            }
        }
    }

	private void navTo(String page)
	{
		if(navigator instanceof PoolNavigator)
		{
			//need to handle non-PoolNavigator due to deserialization issues when in GAE
			((PoolNavigator)navigator).getNavigationStateManager().setState(page);
		}
		else
		{
			navigator.navigateTo(page);
		}
	}

    public void clearTalkButtonBadge()
    {
        viewNameToMenuButton.get(PoolPage.TALK.getNav()).setCaption(PoolPage.TALK.getId().toUpperCase());
    }
}
