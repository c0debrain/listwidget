/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.turbomanage.listwidget.server.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.turbomanage.listwidget.domain.AppUser;
import com.turbomanage.listwidget.server.service.AppUserDao;
import com.turbomanage.listwidget.server.service.LoginService;
import com.turbomanage.listwidget.shared.TooManyResultsException;

/**
 * A servlet filter that handles basic GAE user authentication.
 * Based on http://code.google.com/p/google-web-toolkit/source/browse/trunk/samples/expenses/src/main/java/com/google/gwt/sample/gaerequest/server/GaeAuthFilter.java
 */
public class AuthFilter implements Filter
{

	public void destroy()
	{
	}

	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException
	{
		UserService userService = UserServiceFactory.getUserService();
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		if (!userService.isUserLoggedIn())
		{
			// User is not logged in to App Engine so redirect to login page
			response.setHeader("login",
					userService.createLoginURL(request.getRequestURI()));
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		LoginService.login(request, response);
		
		filterChain.doFilter(request, response);
	}

	public void init(FilterConfig config)
	{
	}
}
