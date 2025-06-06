package com.ecommerce.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import com.ecommerce.commonutils.UserRoleUtils;

@Controller
public class CustomErrorController implements ErrorController {

	@Autowired
	private UserRoleUtils userRoleUtils;

	@GetMapping("/error")

	public String handleError(Model m) {
		String role = userRoleUtils.getAuthenticateUserRole();
		if (role != null) {
			m.addAttribute("role", role);
		}
		return "error/error";
	}

}
