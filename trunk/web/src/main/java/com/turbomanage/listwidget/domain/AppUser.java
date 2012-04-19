package com.turbomanage.listwidget.domain;

import javax.persistence.Entity;

/**
 * An application user, named with a prefix to avoid confusion with GAE User type
 */
@Entity
public class AppUser extends DatastoreObject
{
	private String email;

	public AppUser()
	{
		// No-arg constructor required by Objectify
	}
	
	public AppUser(String userEmail)
	{
		this.email = userEmail;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}
}
