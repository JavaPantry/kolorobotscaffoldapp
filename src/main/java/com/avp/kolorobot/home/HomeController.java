package com.avp.kolorobot.home;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.SpringVersion;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class HomeController {

	@Autowired
	RuntimeProfile runtimeProfile;

	@ModelAttribute("module")
	String module() {
		return "home";
	}

	@GetMapping("/")
	String index(Principal principal, Model model) {
		model.addAttribute("springVersion", SpringVersion.getVersion());

		if(runtimeProfile != null){
			System.out.println("HomeController: runtimeProfile set in production? " + runtimeProfile.isProduction());
		} else {
			System.out.println("HomeController: runtimeProfile is NOT set");
		}

		return principal != null ? "home/homeSignedIn" : "home/homeNotSignedIn";
	}
}
