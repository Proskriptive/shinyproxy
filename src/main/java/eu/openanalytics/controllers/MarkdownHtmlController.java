package eu.openanalytics.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MarkdownHtmlController {
	@RequestMapping(value="/renderHtml", method = RequestMethod.GET)
	public String renderHtml(){
		return "admin";
	}
}
