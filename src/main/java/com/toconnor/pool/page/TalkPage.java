package com.toconnor.pool.page;

import java.util.List;

import com.toconnor.pool.PoolUI;
import com.toconnor.pool.data.TalkManager;
import com.toconnor.pool.data.UserManager;
import com.toconnor.pool.model.Talk;
import com.toconnor.pool.util.Formatter;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * TalkPage
 */
@PageInfo(id="talk", title="Talk")
public class TalkPage extends AbstractPage
{
	@Override
	protected void render()
	{
		CssLayout content = new CssLayout();
		content.addStyleName("content");

		//reset that the talk page has been viewed
		UserManager.updateTalkViewed();
		if(this.getUI() != null)
		{
			((PoolUI)this.getUI()).clearTalkButtonBadge();
		}

		final VerticalLayout holder = new VerticalLayout();
		holder.setSpacing(true);
		content.addComponent(holder);

		HorizontalLayout form = new HorizontalLayout();
		form.setSpacing(true);
		final TextField text = new TextField();
		text.setWidth(500, Unit.PIXELS);
		Button post = new Button("Post New Message");
		post.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		post.addClickListener(new Button.ClickListener()
		{
			@Override
			public void buttonClick(Button.ClickEvent event)
			{
				if(text.getValue().trim().length() > 0)
				{
					Talk talk = new Talk(UserManager.getCurrentUser(), text.getValue().trim());
					TalkManager.saveTalk(talk);
					addTalkPanel(talk, holder);
					UserManager.updateTalkViewed();
				}
				text.setValue("");
			}
		});
		form.addComponent(text);
		form.addComponent(post);
		holder.addComponent(form);

		List<Talk> messages = TalkManager.getTalk(100);
		for(Talk talk : messages)
		{
			addTalkPanel(talk, holder);
		}

		addComponent(content);
		setExpandRatio(content, 1);
	}

	private void addTalkPanel(Talk talk, VerticalLayout holder)
	{
		Panel panel = new Panel();
		panel.setCaption(talk.getUserName() + " at " + Formatter.formatDateTime(talk.getPosted()));
		panel.setContent(new Label(talk.getMessage()));
		panel.addStyleName("login-panel");
		holder.addComponent(panel, 1);
	}
}
