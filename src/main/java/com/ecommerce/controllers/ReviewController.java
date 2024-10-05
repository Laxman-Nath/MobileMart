package com.ecommerce.controllers;

import java.security.Principal;
import java.util.List;

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
import com.ecommerce.services.OrderService;
import com.ecommerce.services.ReviewService;
import com.ecommerce.servicesimpl.CustomerServiceImpl;
import com.ecommerce.servicesimpl.ProductServiceImpl;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

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
//	@ModelAttribute
//	public void getLoggedInUser(Principal p, Model m) {
//		String emailString = p.getName();
////System.out.println(emailString);
//		Customer customer = this.customerServiceImpl.findByEmail(emailString);
////System.out.println(customer.getName());
//		m.addAttribute("customer", customer);
//	}

	@ModelAttribute
	public void getLoggedInUser(Principal p, Model m) {
		List<Category> categories = this.categoryService.findByIsActiveTrue();
		m.addAttribute("categories", categories);
		String emailString = p.getName();
//	System.out.println(emailString);
		Customer customer = this.customerServiceImpl.findByEmail(emailString);
		List<Order> orders = this.orderService.findByCustomer(customer);
		List<Product> latest = this.psi.getLatestProduct();
		m.addAttribute("latest", latest);
		m.addAttribute("orders", orders);
		System.out.println(orders.isEmpty());
		orders.forEach(o -> o.getCustomer().getName());
//	System.out.println(customer.getName());
		m.addAttribute("loggedUser", customer);
	}

	@GetMapping("/reviews/{id}")
	public String showReviews(@PathVariable int id, Model model) {
		Product product = psi.findByProductId(id);
		List<Review> reviews = product.getReviews();
		model.addAttribute("product", product);
		model.addAttribute("reviews", reviews);
		model.addAttribute("newReview", new Review());
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
	@ResponseBody
	public ResponseEntity<String> saveReview(@PathVariable int id, @ModelAttribute("newReview") Review review) {
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
			reviewService.saveReview(id, review, email);
			System.out.println(review.getComment());
			System.out.println(review.getRating());
			String htmlResponse = "<html>" + "<head><title>Review Added</title></head>" + "<body>"
					+ "<h1 style='color: green;'>Review added successfully!</h1>"
					+ "<p>Thank you for your feedback.</p>" + "</body>" + "</html>";

			return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(htmlResponse);
		} catch (Exception e) {
			String errorHtmlResponse = "<html>" + "<head><title>Error</title></head>" + "<body>"
					+ "<h1 style='color: red;'>Error adding review</h1>"
					+ "<p>There was a problem processing your request.</p>" + "</body>" + "</html>";

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_HTML)
					.body(errorHtmlResponse);
		}
	}

	@GetMapping("/admin/viewReviews")
	public String reviews(Model m) {
		List<Review> reviews = this.reviewService.getAllReviews();
		m.addAttribute("reviews", reviews);
		return "admin/showreviews";
	}

	@GetMapping("admin/deleteReview/{reviewId}")
	@ResponseBody
	public String deleteReview(@PathVariable int reviewId, Model m) {
		if (this.reviewService.deleteReview(reviewId)) {
			m.addAttribute("success", "Review is successfully deleted");

		} else {
			m.addAttribute("error", "Error deleting reviews");
		}
		return "redirect:/admin/viewReviews";
	}

}
