package com.toconnor.pool.model;

import java.io.Serializable;
import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Talk
 */
@Entity
@Cache
public class Talk implements Serializable, Comparable<Talk>
{
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	@Index
	private Date posted;
	private String userKey;
	private String userName;
	private String title;
	private String message;

	private Talk()
	{
		//needed for persistence
	}

	public Talk(User user, String message)
	{
		this.posted = new Date();
		this.userKey = user.getKey();
		this.userName = user.fetchFullName();
		this.message = message;
	}

	public String getMessage()
	{
		return message;
	}

	public String getUserName()
	{
		return userName;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public Long getId()
	{
		return id;
	}

	public Date getPosted()
	{
		return posted;
	}

	@Override
	public int compareTo(Talk other)
	{
		final int BEFORE = -1;
		final int AFTER = 1;
		final int SAME = 0;

		if(other == null) return BEFORE;

		if(this.posted.before(other.posted)) return BEFORE;
		if(this.posted.after((other.posted))) return AFTER;

		return SAME;
	}
}
