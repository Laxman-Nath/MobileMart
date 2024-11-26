package com.ecommerce.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.commonutils.EmailUtils;
import com.ecommerce.commonutils.FileUploadUtils;
import com.ecommerce.models.Category;
import com.ecommerce.models.Customer;
import com.ecommerce.models.Order;
import com.ecommerce.models.OrderItem;
import com.ecommerce.models.Product;
import com.ecommerce.services.CategoryService;
import com.ecommerce.services.OrderItemService;
import com.ecommerce.services.OrderService;
import com.ecommerce.services.ProductService;
import com.ecommerce.servicesimpl.CustomerServiceImpl;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private CustomerServiceImpl customerServiceImpl;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private ProductService productService;
	@Autowired
	private FileUploadUtils fileUpload;
	@Autowired
	private OrderItemService orderItemService;

	@GetMapping("/")
	public String home() {
		return "home";
	}

	@ModelAttribute
	public void getLoggedInUser(Principal p, Model m) {
		m.addAttribute("totalProducts",productService.getNumberOfProducts());
		m.addAttribute("deliveredProducts",orderService.findTotalDeliveredProducts());
		m.addAttribute("NumberOfCustomers",customerServiceImpl.findNumberOfCustomers());
		m.addAttribute("totalSales",orderService.findTotalOrders());
		List<Category> categories = this.categoryService.findByIsActiveTrue();
		m.addAttribute("categories", categories);
		String emailString = p.getName();
//	System.out.println(emailString);
		Customer customer = this.customerServiceImpl.findByEmail(emailString);
		List<Order> orders = this.orderService.findByCustomer(customer);
		List<Product> latest = this.productService.getLatestProduct();
		m.addAttribute("latest", latest);
		m.addAttribute("orders", orders);
		System.out.println(orders.isEmpty());
		orders.forEach(o -> o.getCustomer().getName());
//	System.out.println(customer.getName());
		m.addAttribute("loggedUser", customer);
	}

	@GetMapping("/viewProfile/{userId}")
	public String viewProfile(@PathVariable Integer userId, Model m) {
//		System.out.println("The id is:" + userId);s
		Customer customer = this.customerServiceImpl.findCustomerById(userId);
		m.addAttribute("url", customer.getFile());
		m.addAttribute("customer", customer);
		return "user/UserDetails";
	}

	@GetMapping("/EditProfile/{userId}")
	public String editProfile(@PathVariable Integer userId, Model m) {
//		System.out.println("The id is:" + userId);
		Customer customer = this.customerServiceImpl.findCustomerById(userId);
		m.addAttribute("customer", customer);
		return "user/EditProfile";
	}

	@PostMapping("/saveUpdatedProfile")
	public String saveUpdatedProfile(@ModelAttribute("customer") Customer customer, HttpSession session,
			@RequestParam(required = false) MultipartFile image) throws IOException {
		System.out.println("Password:" + customer.getPassword());
		if (!image.isEmpty() && image != null) {
			customer.setFile(image.getOriginalFilename());
			if (!fileUpload.uploadFile(image, "src\\main\\resources\\static\\img\\profile_img\\")) {
				session.setAttribute("error", "Error updating your profile!");
				return "redirect:/user/viewProfile/" + customer.getId();
			}

		}

//		customer.setFile(image.getOriginalFilename());
		customer.setRole("ROLE_USER");
		Customer c = this.customerServiceImpl.updateCustomer(customer);
		if (c != null) {
			session.setAttribute("success", "Your profile is successfully updated!");
		} else {
			session.setAttribute("error", "Error updating your profile!");
		}
		return "redirect:/user/viewProfile/" + customer.getId();
	}

	@GetMapping("/changePassword/{userId}")
	public String changePassword(@PathVariable int userId, Model m) {
		Customer customer = this.customerServiceImpl.findCustomerById(userId);
		System.out.println("Name is:" + customer.getName());
		m.addAttribute("customer", customer);
		return "/user/changePassword";
	}

	@PostMapping("/saveChangedPassword")
	public String saveChangedPassword(@RequestParam int id, @RequestParam String password,
			@RequestParam String newpassword, HttpSession session) {
		if (customerServiceImpl.changePassword(id, password, newpassword)) {
			session.setAttribute("success", "Your password is changed successfully!");
		} else {
			session.setAttribute("error", "Error changing password!");
		}
		return "redirect:/user/changePassword/" + id;
	}

	@GetMapping("viewOrders/{customerId}")
	public String viewOrders(@PathVariable int customerId, Model m) {
		List<Order> orders = this.orderService.findByCustomer(customerServiceImpl.findCustomerById(customerId));
		m.addAttribute("orders", orders);
		return "/user/orderlist";

	}

//	@GetMapping("editorder/{orderId}")
//	public String editOrder(@PathVariable int orderId,Model m) {
//		Order order = orderService.findById(orderId);
//		List<OrderItem> items = order.getOrderItems();
//		m.addAttribute("order", order);
//		m.addAttribute("items", items);
//		return "user/orderedit";
//	}

	@GetMapping("/editorder/{id}")
	public String editOrder(@PathVariable int id, Model m) {
		Order order = orderService.findById(id);
		List<OrderItem> items = order.getOrderItems();
		m.addAttribute("order", order);
		m.addAttribute("items", items);
		return "user/orderedit";
	}

	@GetMapping("/cancelOrder/{orderId}")
	public ResponseEntity<String> deleteProductFromOrder(@PathVariable int orderId) {
		try {
			if (orderService.cancelOrder(orderId) != null) {
				String message = "Order updated successfully";
				Map<String, Object> response = new HashMap<>();
				response.put("message", message);
				return ResponseEntity.ok("<h2 style='color:red;'>Order Cancelled successfully.</h2>");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
			}
		} catch (Exception e) {
			logger.error("Failed to delete product from order", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to delete product. Please try again.");
		}
	}

	@PatchMapping("/increaseProductFromOrder/{itemId}")
	public ResponseEntity<String> increaseProductFromOrder(@PathVariable int itemId, @RequestParam int orderId) {
	    OrderItem orderItem = this.orderItemService.findById(itemId);
	    Product product = orderItem.getProduct();

	    try {
	        if (product.getQuantity() <= orderItem.getQuantity()) {
	        	System.out.println("******************Inside if************************** ");
	            return ResponseEntity.status(HttpStatus.CONFLICT)
	                    .body("Cannot increase product quantity. Available: " + product.getQuantity());
	        } else {
	        	System.out.println("*****************inside else**********");
	            if (orderService.increaseProductFromOrder(itemId)) {
	                logger.info("Product quantity increased successfully for itemId: {}", itemId);
	                return ResponseEntity.ok("Product quantity increased successfully.");
	            } else {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
	            }
	        }
	    } catch (Exception e) {
	        logger.error("Failed to increase product quantity", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Failed to increase product quantity. Please try again.");
	    }
	}


	@PatchMapping("/decreaseProductFromOrder/{itemId}")
	public ResponseEntity<String> decreaseProductFromOrder(@PathVariable int itemId, @RequestParam int orderId) {
		try {
			if (orderService.decreaseProductFromOrder(itemId)) {
				String message = "Product is decreased successfully";
				Map<String, Object> response = new HashMap<>();
				response.put("message", message);
				return ResponseEntity.ok("Product quantity decreased successfully.");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
			}
		} catch (Exception e) {
			logger.error("Failed to decrease product quantity", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to decrease product quantity. Please try again.");
		}
	}

	@PostMapping("/updateOrder/{id}")
	public String updateOrder(@PathVariable int id, @ModelAttribute("order") Order order, HttpSession session) {
		Order oldOrder = orderService.findById(id);

//        for(OrderItem item:order.getOrderItems()) {
//        	System.out.println(item.getQuantity());
//        	System.out.println(item.getPrice());
//        }

		if (orderService.updateOrder(oldOrder, order) != null) {
			session.setAttribute("success", "Order updated successfully!");
		} else {
			session.setAttribute("error", "Error updating order!");
		}

		return "redirect:/user/editorder/" + id;
	}
}
