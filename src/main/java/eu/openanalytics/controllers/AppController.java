/**
 * ShinyProxy
 *
 * Copyright (C) 2016-2017 Open Analytics
 *
 * ===========================================================================
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Apache License as published by
 * The Apache Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Apache License for more details.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/>
 */
package eu.openanalytics.controllers;


import java.security.Principal;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.openanalytics.ShinyProxyApplication;
import eu.openanalytics.services.AppService;
import eu.openanalytics.services.DockerService;
import eu.openanalytics.services.UserService;

@Controller
public class AppController extends BaseController {

	@Inject
	DockerService dockerService;
	
	@Inject
	AppService appService;

	@Inject
	UserService userService;
	
	@Inject
	Environment environment;

	@RequestMapping("/app/*")
	String app(ModelMap map, Principal principal, HttpServletRequest request)  {
		prepareMap(map, request);
		
		request.getSession().setMaxInactiveInterval(6*60);
		String userName = getUserName(request);

		String queryString = request.getQueryString();
		if (queryString == null) queryString = "";
		else queryString = "?" + queryString;
		String mapping = dockerService.getMapping(getUserName(request), getAppName(request), false);
		String contextPath = ShinyProxyApplication.getContextPath(environment);

		map.put("appTitle", getAppTitle(request));
		map.put("container", buildContainerPath(mapping, request));
		map.put("heartbeatRate", environment.getProperty("shiny.proxy.heartbeat-rate", "10000"));
		map.put("heartbeatTimeout",environment.getProperty("shiny.proxy.heartbeat-timeout", "960000"));
		map.put("adminGroups", userService.getAdminGroups());
		map.put("userName",userName);
		map.put("heartbeatPath", contextPath + "/heartbeat");
		
		return "app";
	}
	
	@RequestMapping(value="/app/*", method=RequestMethod.POST)
	@ResponseBody
	String startApp(HttpServletRequest request) {
		String mapping = dockerService.getMapping(getUserName(request), getAppName(request), true);
		return buildContainerPath(mapping, request);
	}
	
	private String buildContainerPath(String mapping, HttpServletRequest request) {
		if (mapping == null) return "";
		
		String queryString = request.getQueryString();
		queryString = (queryString == null) ? "" : "?" + queryString;
		
		String contextPath = ShinyProxyApplication.getContextPath(environment);
		String containerPath = contextPath + "/" + mapping + environment.getProperty("shiny.proxy.landing-page") + queryString;
		return containerPath;
	}
}
