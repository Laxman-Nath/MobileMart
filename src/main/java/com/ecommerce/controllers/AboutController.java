package com.ecommerce.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.ecommerce.models.Category;
import com.ecommerce.models.Customer;
import com.ecommerce.models.Order;
import com.ecommerce.services.CategoryService;
import com.ecommerce.services.CustomerService;
import com.ecommerce.services.OrderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AboutController {
	private final CategoryService categoryService;
	private final OrderService orderService;
	private final CustomerService customerService;

	@ModelAttribute
	public void getLoggedInUser(Principal p, Model m) {
		List<Category> categories = categoryService.findByIsActiveTrue();

		m.addAttribute("categories", categories);
		if (p != null) {
			String emailString = p.getName();
			Customer customer = customerService.findByEmail(emailString);
			List<Order> orders = this.orderService.findByCustomer(customer);
			m.addAttribute("orders", orders);
			m.addAttribute("loggedUser", customer);
		} else {
			System.out.println("Inside null");
			m.addAttribute("loggedUser", null);
		}
	}

	@GetMapping("/about")
	public String getAbout() {
		return "aboutus";
	}
}
