package com.toconnor.pool;

import java.io.Serializable;

import com.vaadin.navigator.NavigationStateManager;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.UI;

/**
 * PoolNavigator is required to workaround bug within Navigator.navigateTo() that initializes the view class twice.
 * See https://vaadin.com/forum#!/thread/3395652
 */
public class PoolNavigator extends Navigator implements Serializable
{
	public PoolNavigator(UI ui, ComponentContainer container)
	{
		super(ui, container);
	}

	public PoolNavigator(UI ui, SingleComponentContainer container)
	{
		super(ui, container);
	}

	public PoolNavigator(UI ui, ViewDisplay display)
	{
		super(ui, display);
	}

	public PoolNavigator(UI ui, NavigationStateManager stateManager, ViewDisplay display)
	{
		super(ui, stateManager, display);
	}

	/**
	 * This opens up protected getStateManager() so state can be set once instead of forcing extra navigateTo call.
	 * @return
	 */
	public NavigationStateManager getNavigationStateManager()
	{
		return super.getStateManager();
	}
}
