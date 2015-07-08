package com.toconnor.pool.page;

import java.util.List;

import com.google.common.collect.Lists;
import com.toconnor.pool.util.Validator;


/**
 * Page
 *
 */
public final class PoolPage
{
	public static final PoolPage HOME = new PoolPage(HomePage.class);
	public static final PoolPage RANK_TEAMS = new PoolPage(RankTeamsPage.class);
	public static final PoolPage RANK_GAMES = new PoolPage(RankGamesPage.class);
	public static final PoolPage WEEKLY_STANDINGS = new PoolPage(WeeklyStandingsPage.class);
	public static final PoolPage SEASON_STANDINGS = new PoolPage(SeasonStandingsPage.class);
    public static final PoolPage TALK = new PoolPage(TalkPage.class);
	public static final PoolPage RULES = new PoolPage(RulesPage.class);
	public static final PoolPage CONFIRM = new PoolPage(ConfirmPage.class);

	public static final PoolPage CONFIG = new PoolPage(ConfigPage.class);
	public static final PoolPage DUMP = new PoolPage(DumpPage.class);
	public static final PoolPage USERS = new PoolPage(UsersPage.class);

	public static final List<PoolPage> NAV_PAGES = Lists.newArrayList(HOME, RANK_TEAMS, RANK_GAMES, WEEKLY_STANDINGS, SEASON_STANDINGS, TALK, RULES, USERS);
	public static final List<PoolPage> ALL_PAGES = Lists.newArrayList(HOME, RANK_TEAMS, RANK_GAMES, WEEKLY_STANDINGS, SEASON_STANDINGS, TALK, RULES, USERS, CONFIG, DUMP, CONFIRM);

	public static PoolPage get(String find)
	{
		for(PoolPage page : ALL_PAGES)
		{
			if(page.id.equalsIgnoreCase(find))
			{
				return page;
			}
		}
		throw new RuntimeException("Unknown page: " + find);
	}

    public static PoolPage get(Class<? extends AbstractPage> find)
    {
        for(PoolPage page : ALL_PAGES)
        {
            if(page.pageClass.equals(find))
            {
                return page;
            }
        }
        throw new RuntimeException("Unknown page: " + find.getName());
    }

	private final String id;
	private final String title;
	private Class<? extends AbstractPage> pageClass;
	private final boolean admin;

	private PoolPage(Class<? extends AbstractPage> pageClass)
	{
		this.pageClass = pageClass;

		PageInfo info = pageClass.getAnnotation(PageInfo.class);
		Validator.assertNotNull(info, "%s is missing PageInfo annotation", pageClass.getName());
		this.id = info.id();
        this.title = info.title();
		this.admin = info.adminOnly();
	}

	public String getId()
	{
		return id;
	}

	public String getTitle()
	{
		return title;
	}

    public String getNav()
    {
        return "/" + id;
    }

	public Class<? extends AbstractPage> getPageClass()
	{
		return pageClass;
	}

	public boolean isAdmin()
	{
		return admin;
	}
}
