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
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.openanalytics.services.AppService.ShinyApp;
import eu.openanalytics.services.DockerService;

@Controller
public class IndexController extends BaseController {
	
	@Inject
	Environment environment;
    
	@Inject
    DockerService dockerService;
	
	@RequestMapping("/")
    String index(ModelMap map, Principal principal, HttpServletRequest request) {
		prepareMap(map, request);

        HttpSession session = request.getSession();
		if(dockerService != null){
			dockerService.shutdown();
		}
    	String userName = getUserName(request);
        session.setAttribute("userName", userName);

		List<ShinyApp> apps = userService.getAccessibleApps(SecurityContextHolder.getContext().getAuthentication());
		map.put("apps", apps.toArray());

		boolean displayAppLogos = false;
		for (ShinyApp app: apps) {
			if (app.getLogoUrl() != null) displayAppLogos = true;
		}
		
		map.put("title", environment.getProperty("shiny.proxy.title"));
		map.put("logo", environment.getProperty("shiny.proxy.logo-url"));
		map.put("apps", apps.toArray());
		map.put("displayAppLogos", displayAppLogos);
		map.put("adminGroups", userService.getAdminGroups());
		map.put("userName", userName);
		map.put("displayAppLogos", displayAppLogos);

		return "index";
    }
}