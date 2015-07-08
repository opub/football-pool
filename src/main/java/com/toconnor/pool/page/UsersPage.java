package com.toconnor.pool.page;

import java.util.List;

import com.toconnor.pool.data.UserManager;
import com.toconnor.pool.model.User;
import com.toconnor.pool.util.Formatter;
import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import org.vaadin.dialogs.ConfirmDialog;

/**
 * UsersPage
 */
@PageInfo(id="users", title="Users", adminOnly=true)
public class UsersPage extends AbstractPage
{
	@Override
	protected void render()
	{
		CssLayout content = new CssLayout();
		content.addStyleName("content");

		final Table table = new Table();
		table.setEditable(true);
		table.setImmediate(true);
		table.setSortEnabled(true);
		table.setPageLength(20);
		table.setSizeFull();
		table.setRowHeaderMode(Table.RowHeaderMode.INDEX);
		table.setColumnWidth(null, 30);

		//define the names and data types of columns
		table.addContainerProperty("Display Name", String.class, null);
		table.addContainerProperty("First Name", String.class, null);
		table.addContainerProperty("Last Name", String.class, null);
		table.addContainerProperty("Email Address", String.class, null);
		table.addContainerProperty("Winnings", Double.class, null);
		table.addContainerProperty("Provider", Label.class, null);
		table.addContainerProperty("Last Login", Label.class, null);
		table.addContainerProperty("Active?", Boolean.class, null);
		table.addContainerProperty("Admin?", Boolean.class, null);
		table.addContainerProperty("Paid?", Boolean.class, null);
		table.addContainerProperty("Save", Button.class, null);
		table.addContainerProperty("Delete", Button.class, null);

		//add all users to table
		List<User> users = UserManager.getAllUsers();
		for (User user : users)
		{
			Button save = new Button("Save");
			save.setData(user);
			save.addClickListener(new Button.ClickListener()
			{
				public void buttonClick(Button.ClickEvent event)
				{
					User user = (User) event.getButton().getData();
					applyRowToUser((Table)event.getButton().getParent(), user);
					UserManager.saveUser(user);
					Notification.show(user.fetchFullName() + " has been updated.");
				}
			});
			save.addStyleName("link");

			Button delete = new Button("Delete");
			delete.setData(user);
			delete.addClickListener(new Button.ClickListener()
			{
				public void buttonClick(Button.ClickEvent event)
				{
					final User user = (User) event.getButton().getData();
					ConfirmDialog d = ConfirmDialog.show(getUI(), "Delete User", "Are you really sure you want to delete " + user.getDisplayName() + "?", "Yes", "No", new ConfirmDialog.Listener()
					{
						public void onClose(ConfirmDialog dialog)
						{
							if (dialog.isConfirmed())
							{
								UserManager.deleteUser(user);
								Notification.show(user.fetchFullName() + " has been deleted!");
							}
						}
					});
					d.addStyleName("confirm-dialog");
					d.setWidth(500, Unit.PIXELS);
					d.setHeight(150, Unit.PIXELS);
				}
			});
			delete.addStyleName("link");

			table.addItem(new Object[] {
					user.getDisplayName(),
					user.getFirstName(),
					user.getLastName(),
					user.getEmail(),
					user.getWinnings(),
					new Label(user.getProvider()),
					new Label(Formatter.formatIsoDate(user.getLastAccess())),
					user.isActive(),
					user.isAdmin(),
					user.isPaid(),
					save, delete},
					user.getKey()); //row ID
		}

		content.addComponent(table);

		addComponent(content);
		setExpandRatio(content, 1);
	}

	private void applyRowToUser(Table table, User user)
	{
		Item row = table.getItem(user.getKey());

		user.setDisplayName((String) row.getItemProperty("Display Name").getValue());
		user.setFirstName((String)row.getItemProperty("First Name").getValue());
		user.setLastName((String)row.getItemProperty("Last Name").getValue());
		user.setEmail((String)row.getItemProperty("Email Address").getValue());
		user.setWinnings((Double)row.getItemProperty("Winnings").getValue());
		user.setActive((Boolean)row.getItemProperty("Active?").getValue());
		user.setAdmin((Boolean)row.getItemProperty("Admin?").getValue());
		user.setPaid((Boolean)row.getItemProperty("Paid?").getValue());
	}
}
