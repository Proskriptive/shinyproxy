package eu.openanalytics.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Torkild U. Resheim, Itema AS
 */
@Controller
public class HospitalController {

@RequestMapping(value = "/hospital", method = RequestMethod.GET)
public String hospital(){
	return "hospital";
}
}
