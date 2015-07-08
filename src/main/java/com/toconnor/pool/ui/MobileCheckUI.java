package com.toconnor.pool.ui;

import java.io.Serializable;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

@Theme(Reindeer.THEME_NAME)
@Title("Football Pool")
public class MobileCheckUI extends UI implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Override
	protected void init(final VaadinRequest request)
	{
		setWidth("400px");
		setContent(new VerticalLayout()
		{
			{
				setMargin(true);
				addComponent(new Label("<h1>Football Pool</h1><h3>This application does not yet support mobile devices.</h3><p>If you wish, you can continue to <a href=\""
						+ request.getContextPath() + request.getPathInfo() + "?mobile=false\">load it anyway</a>.</p>", ContentMode.HTML));
			}
		});
	}
}
