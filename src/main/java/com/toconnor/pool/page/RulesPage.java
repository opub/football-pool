package com.toconnor.pool.page;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;

/**
 * RulesPage
 */
@PageInfo(id="rules", title="Rules")
public class RulesPage extends AbstractPage
{
	@Override
	protected void render()
    {
        CssLayout content = new CssLayout();
        content.addStyleName("content");

        CustomLayout panel = new CustomLayout("rules");
        panel.addStyleName("layout-panel");
        panel.addStyleName("padded");
        content.addComponent(panel);

        addComponent(content);
        setExpandRatio(content, 1);
    }
}
