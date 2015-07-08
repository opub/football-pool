package com.toconnor.pool.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * User
 */
@Entity
@Cache(expirationSeconds = 3600)
public class User implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	private String key;

	private String displayName;
	private String firstName;
	private String lastName;
	private String email;
	private String provider;
	private Date lastAccess;
	@Index
	private boolean active;
	private boolean paid;
	private boolean admin;
	private double winnings;
	private Date talkViewed;
	private List<RankedTeam> rankedTeams = new ArrayList<RankedTeam>();

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getProvider()
	{
		return provider;
	}

	public void setProvider(String provider)
	{
		this.provider = provider;
	}

	public Date getLastAccess()
	{
		return lastAccess;
	}

	public void setLastAccess(Date lastAccess)
	{
		this.lastAccess = lastAccess;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public boolean isPaid()
	{
		return paid;
	}

	public void setPaid(boolean paid)
	{
		this.paid = paid;
	}

	public boolean isAdmin()
	{
		return admin || "toconnor@gmail.com".equals(email);     //need this for access to admin sections when my user has just been created
	}

	public void setAdmin(boolean admin)
	{
		this.admin = admin;
	}

	public double getWinnings()
	{
		return winnings;
	}

	public void setWinnings(double winnings)
	{
		this.winnings = winnings;
	}

	public List<RankedTeam> getRankedTeams()
	{
		return rankedTeams;
	}

	public void setRankedTeams(List<RankedTeam> rankedTeams)
	{
		this.rankedTeams = rankedTeams;
	}

	public Date getTalkViewed()
	{
		return talkViewed;
	}

	public void setTalkViewed(Date talkViewed)
	{
		this.talkViewed = talkViewed;
	}

	public String fetchFullName()
	{
		return (firstName + " " + lastName).trim();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof User)) return false;

		User user = (User) o;

		if (admin != user.admin) return false;
		if (!displayName.equals(user.displayName)) return false;
		if (!email.equals(user.email)) return false;
		if (!firstName.equals(user.firstName)) return false;
		if (!key.equals(user.key)) return false;
		if (!lastName.equals(user.lastName)) return false;
		if (!provider.equals(user.provider)) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = key.hashCode();
		result = 31 * result + displayName.hashCode();
		result = 31 * result + firstName.hashCode();
		result = 31 * result + lastName.hashCode();
		result = 31 * result + email.hashCode();
		result = 31 * result + provider.hashCode();
		result = 31 * result + (admin ? 1 : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return fetchFullName();
	}
}
