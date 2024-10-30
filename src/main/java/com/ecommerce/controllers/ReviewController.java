package com.ecommerce.controllers;

import java.security.Principal;
import java.util.List;

import org.hibernate.bytecode.internal.bytebuddy.PrivateAccessorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ecommerce.models.Category;
import com.ecommerce.models.Customer;
import com.ecommerce.models.Order;
import com.ecommerce.models.Product;
import com.ecommerce.models.Review;
import com.ecommerce.services.CategoryService;
import com.ecommerce.services.CustomerService;
import com.ecommerce.services.OrderService;
import com.ecommerce.services.ReviewService;
import com.ecommerce.servicesimpl.CustomerServiceImpl;
import com.ecommerce.servicesimpl.ProductServiceImpl;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import jakarta.servlet.http.HttpSession;

@Controller
public class ReviewController {
	@Autowired
	private ProductServiceImpl psi;
	@Autowired
	private CustomerServiceImpl customerServiceImpl;
	@Autowired
	private ReviewService reviewService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private CustomerService customerService;

	@ModelAttribute
	public void getLoggedInUser(Principal p, Model m) {
		List<Category> categories = this.categoryService.findByIsActiveTrue();
		m.addAttribute("categories", categories);
		if (p != null) {
			String emailString = p.getName();

			Customer customer = this.customerServiceImpl.findByEmail(emailString);
			List<Order> orders = this.orderService.findByCustomer(customer);

			m.addAttribute("orders", orders);

			m.addAttribute("loggedUser", customer);
		} else {
			System.out.println("Inside null");
			m.addAttribute("loggedUser", null);
		}
		List<Product> latest = this.psi.getLatestProduct();
		m.addAttribute("latest", latest);
	}

	@GetMapping("/reviews/{id}")
	public String showReviews(@PathVariable int id, Model model) {
		Product product = psi.findByProductId(id);
		List<Review> reviews = product.getReviews();
		model.addAttribute("product", product);
		model.addAttribute("reviews", reviews);

		return "review/reviews";
	}

	@GetMapping("/showreviewform/{id}")
	public String reviewForm(@PathVariable int id, Model model, Principal p) {
//		String email = p.getName();
//		Customer customer = this.customerServiceImpl.findByEmail(email);
		if (p == null) {
			return "redirect:/login";
		}
		Product product = psi.findByProductId(id);
		List<Review> reviews = product.getReviews();
		model.addAttribute("product", product);
		model.addAttribute("reviews", reviews);
		model.addAttribute("newReview", new Review());
		return "/review/reviewForm";
	}

	@PostMapping("/saveReview/{id}")
	public String saveReview(@PathVariable int id, @ModelAttribute("newReview") Review review, HttpSession session) {
		Review savedReview = null;
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		System.out.println(userDetails.getUsername());
//		String email=userDetails.getUsername();
//		  review.setCustomer(customerServiceImpl.findByEmail(email));
//		 System.out.println(email);
			String email;
			if (authentication.getPrincipal() instanceof UserDetails) {
				UserDetails userDetails = (UserDetails) authentication.getPrincipal();
				email = userDetails.getUsername();
			} else {
				email = (String) authentication.getPrincipal();
			}
			System.out.println("Email is:" + email);
			savedReview = reviewService.saveReview(id, review, customerService.findByEmail(email).getId());
			System.out.println(review.getComment());
			System.out.println(review.getRating());

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (savedReview == null) {
			session.setAttribute("error", "Error adding review!");
		} else {
			session.setAttribute("success", "Your review was added successfully! Thank you for your feedback");
		}

		return "redirect:/showreviewform/" + id;
	}

	@GetMapping("/admin/viewReviews")
	public String reviews(Model m) {
		List<Review> reviews = this.reviewService.getAllReviews();
		m.addAttribute("reviews", reviews);
		return "admin/showreviews";
	}

	@GetMapping("admin/deleteReview/{reviewId}")

	public String deleteReview(@PathVariable int reviewId, HttpSession session) {
		if (this.reviewService.deleteReview(reviewId)) {
			session.setAttribute("success", "Review is successfully deleted");

		} else {
			session.setAttribute("error", "Error deleting reviews");
		}
		return "redirect:/admin/viewReviews";
	}

}
