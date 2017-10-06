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

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.NestedServletException;

import eu.openanalytics.services.UserService;

@Controller
public class ErrorController extends BaseController implements org.springframework.boot.autoconfigure.web.ErrorController {
	
	@Inject
	UserService userService;
	
	@Inject
	Environment environment;
	
	@RequestMapping("/error")
    String handleError(ModelMap map, HttpServletRequest request, HttpServletResponse response) {
		prepareMap(map, request);

        HttpSession session =  request.getSession();
        String userName = (String) session.getAttribute("userName");
		map.put("title", environment.getProperty("shiny.proxy.title"));
		map.put("logo", environment.getProperty("shiny.proxy.logo-url"));
		map.put("status", response.getStatus());
		map.put("adminGroups", userService.getAdminGroups());
		map.put("userName",userName);

		String message = "";
		String stackTrace = "";
		Throwable exception = (Throwable) request.getAttribute("javax.servlet.error.exception");
		if (exception instanceof NestedServletException && exception.getCause() instanceof Exception) {
			exception = (Exception) exception.getCause();
		}
		if (exception != null) {
			if (exception.getMessage() != null) message = exception.getMessage();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try (PrintWriter writer = new PrintWriter(bos)) {
				exception.printStackTrace(writer);
			}
			stackTrace = bos.toString();
			stackTrace = stackTrace.replace(System.getProperty("line.separator"), "<br/>");
		}
		map.put("message", message);
		map.put("stackTrace", stackTrace);
		map.put("status", response.getStatus());
		
		return "error";
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}

}
