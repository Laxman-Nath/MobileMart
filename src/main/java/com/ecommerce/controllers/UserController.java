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
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.ecommerce.commonutils.VerifyCodeUtils;
import com.ecommerce.constants.BillConstant;
import com.ecommerce.constants.OrderConstant;
import com.ecommerce.models.Cart;
import com.ecommerce.models.CartProduct;
import com.ecommerce.models.Category;
import com.ecommerce.models.Customer;
import com.ecommerce.models.Order;
import com.ecommerce.models.OrderItem;
import com.ecommerce.models.Product;
import com.ecommerce.services.CartProductService;
import com.ecommerce.services.CartService;
import com.ecommerce.services.CategoryService;
import com.ecommerce.services.CustomerService;
import com.ecommerce.services.OrderItemService;
import com.ecommerce.services.OrderService;
import com.ecommerce.services.ProductService;
import com.ecommerce.servicesimpl.CustomerServiceImpl;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@PreAuthorize("hasRole('USER')")
@RequestMapping("/user")
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private CustomerService customerService;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private ProductService productService;
	@Autowired
	private FileUploadUtils fileUpload;
	@Autowired
	private EmailUtils emailUtils;
	@Autowired
	private OrderItemService orderItemService;
	@Autowired
	private CartService cartService;
	@Autowired
	private VerifyCodeUtils verifyCode;
	
	
	@Autowired
	private CartProductService cartProductService;

	@GetMapping("/")
	public String home() {
		return "home";
	}

	@ModelAttribute
	public void getLoggedInUser(Principal p, Model m) {
		m.addAttribute("totalProducts", productService.getNumberOfProducts());
		m.addAttribute("deliveredProducts", orderService.findTotalDeliveredProducts());
		m.addAttribute("NumberOfCustomers", customerService.findNumberOfCustomers());
		m.addAttribute("totalSales", orderService.findTotalOrders());
		List<Category> categories = this.categoryService.findByIsActiveTrue();
		m.addAttribute("categories", categories);
		String emailString = p.getName();
//	System.out.println(emailString);
		Customer customer = this.customerService.findByEmail(emailString);
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
		Customer customer = this.customerService.findCustomerById(userId);
		m.addAttribute("url", customer.getFile());
		m.addAttribute("customer", customer);
		return "user/UserDetails";
	}

	@GetMapping("/EditProfile/{userId}")
	public String editProfile(@PathVariable Integer userId, Model m) {
//		System.out.println("The id is:" + userId);
		Customer customer = this.customerService.findCustomerById(userId);
		m.addAttribute("customer", customer);
		return "user/EditProfile";
	}

	@PostMapping("/saveUpdatedProfile")
	public String saveUpdatedProfile(@ModelAttribute("customer") Customer customer, HttpSession session,
			@RequestParam(required = false) MultipartFile image) throws IOException {

		customer.setRole("ROLE_USER");
		Customer c = this.customerService.updateCustomer(customer, image);
		if (c != null) {
			session.setAttribute("success", "Your profile is successfully updated!");
		} else {
			session.setAttribute("error", "Error updating your profile!");
		}
		return "redirect:/user/viewProfile/" + customer.getId();
	}

	@GetMapping("/changePassword/{userId}")
	public String changePassword(@PathVariable int userId, Model m) {
		Customer customer = this.customerService.findCustomerById(userId);
		System.out.println("Name is:" + customer.getName());
		m.addAttribute("customer", customer);
		return "/user/changePassword";
	}

	@PostMapping("/saveChangedPassword")
	public String saveChangedPassword(@RequestParam int id, @RequestParam String password,
			@RequestParam String newpassword, HttpSession session) {
		if (customerService.changePassword(id, password, newpassword)) {
			session.setAttribute("success", "Your password is changed successfully!");
		} else {
			session.setAttribute("error", "Error changing password!");
		}
		return "redirect:/user/changePassword/" + id;
	}

	@GetMapping("viewOrders/{customerId}")
	public String viewOrders(@PathVariable int customerId, Model m) {
		List<Order> orders = this.orderService.findByCustomer(customerService.findCustomerById(customerId));
		m.addAttribute("orders", orders);
		m.addAttribute("customerId",customerId);
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

	@GetMapping("/editorder/{orderId}/{customerId}")
	public String editOrder(@PathVariable int orderId,@PathVariable int customerId, Model m) {
		Order order = orderService.findById(orderId);
		List<OrderItem> items = order.getOrderItems();
		m.addAttribute("order", order);
		m.addAttribute("items", items);
		m.addAttribute("customerId",customerId);
		return "user/orderedit";
	}

	@GetMapping("/cancelOrder/{orderId}/{customerId}")
	public String deleteProductFromOrder(@PathVariable int orderId,@PathVariable int customerId,HttpSession session) {
		
			if (orderService.cancelOrder(orderId) != null) {
				session.setAttribute("success", "Order is cancelled successfully!");
				
			} else {
				session.setAttribute("error","Error cancelling order!");
			}
			return "redirect:/user/viewOrders/"+customerId;
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

	@PostMapping("/updateOrder/{orderId}/{customerId}")
	public String updateOrder(@PathVariable int orderId,@PathVariable int customerId, @ModelAttribute("order") Order order, HttpSession session) {
		Order oldOrder = orderService.findById(orderId);

//        for(OrderItem item:order.getOrderItems()) {
//        	System.out.println(item.getQuantity());
//        	System.out.println(item.getPrice());
//        }

		if (orderService.updateOrder(oldOrder, order) != null) {
			session.setAttribute("success", "Order updated successfully!");
		} else {
			session.setAttribute("error", "Error updating order!");
		}

		return "redirect:/user/viewOrders/"+customerId;
	}

	@GetMapping("/addtocart")
	public String addToCart(@RequestParam int pid, @RequestParam int uid, HttpSession session) {
		System.out.println(pid);
		System.out.println(uid);
//		System.out.println("We are inside add to cart");
		Cart cart = cartService.saveCart(uid, pid);
		if (cart != null) {
			session.setAttribute("success", "Product is successfully addded to cart!");
		} else {
			session.setAttribute("error", "Error adding product to cart!");
		}

		return "redirect:/viewDetail/" + pid;
	}

	@GetMapping("/viewcart")
	public String viewCart(@RequestParam(required = false, defaultValue = "0") int cid,
			@RequestParam(required = false, defaultValue = "0") int uid, Model m) {
		Cart cart = this.cartService.getCartByCartIdAndCustomerIdAndIsCheckedFalse(cid, uid);
		if (cart != null) {
			List<CartProduct> cartProducts = cart.getCartProducts();
			m.addAttribute("cartproducts", cartProducts);
			m.addAttribute("totalprice", cartService.calculateTotalPrice(cart));

			m.addAttribute("uid", uid);
			m.addAttribute("cid", cid);
		}
//		List<Product> products=new ArrayList<>();
//		for(CartProduct c:cartProducts) {
//			products.add(c.getProduct());
//		}
//		
//		for(Product p:products) {
//			System.out.println(p.getName());
//		}
//		System.out.println("Total price="+cart.getTotalPrice());

		return "/user/viewcart";
	}

	@GetMapping("/deleteProductFromCart")
	public String deleteProductFromCart(@RequestParam(required = false, defaultValue = "0") int cid,
			@RequestParam(required = false, defaultValue = "0") int uid, @RequestParam int pid, HttpSession session) {
		Cart cart = this.cartService.removeProductFromCart(pid);
		if (cart != null) {
			session.setAttribute("success", "Products are successfully removed from your cart!");
		} else {
			session.setAttribute("error", "Error removing products from your cart!");
		}
		return "redirect:/user/viewcart?cid=" + cid + "&uid=" + uid;
	}

	@GetMapping("/increaseProduct")
	public String increaseProductInCart(@RequestParam(required = false, defaultValue = "0") int cid,
			@RequestParam(required = false, defaultValue = "0") int uid, @RequestParam int pid, HttpSession session) {

		CartProduct cartProduct = this.cartProductService.findById(pid);
		Product product = cartProduct.getProduct();
		if (product.getQuantity() <= cartProduct.getQuantity()) {
			session.setAttribute("error", "Insufficent item available in our inventory!");

		} else {
			Cart cart = this.cartService.increaseProductInCart(pid);

			if (cart != null) {
				session.setAttribute("success", "Product is successfully increased in your cart!");
			} else {
				session.setAttribute("error", "Error increasing products in your cart!");
			}
		}
		return "redirect:/user/viewcart?cid=" + cid + "&uid=" + uid;
	}

	@GetMapping("/decreaseProduct")
	public String decreasProductInCart(@RequestParam(required = false, defaultValue = "0") int cid,
			@RequestParam(required = false, defaultValue = "0") int uid, @RequestParam int pid, HttpSession session) {
		Cart cart = this.cartService.decreaseProductInCart(pid);
		if (cart != null) {
			session.setAttribute("success", "Product is successfully deccreased in your cart!");
		} else {
			session.setAttribute("error", "Error decreasing products from your cart!");
		}
		return "redirect:/user/viewcart?cid=" + cid + "&uid=" + uid;
	}

	@GetMapping("/checkout/{customerId}/{cartId}")
	public String checkout(@PathVariable int customerId, @PathVariable int cartId, Model m) {

		Customer customer = this.customerService.findCustomerById(customerId);
		Cart cart = this.cartService.getCartByCartIdAndCustomerIdAndIsCheckedFalse(cartId, customerId);
		System.out.println(customer.getTole());
//		System.out.println("name="+customer.getName());
//		System.out.println("Cart "+cart.getCustomer().getName());
		m.addAttribute("customer", customer);
		m.addAttribute("cart", cart);
		return "/user/checkout";
	}

	@PostMapping("/placeOrder/{customerId}/{cartId}")
	public String placeOrder(@ModelAttribute Order order, @PathVariable int customerId, @PathVariable int cartId,
			HttpSession session, HttpServletRequest request, Model m) {
//		System.out.println(order.getShippingAddress());
//		System.out.println("customer id="+customerId);
//		System.out.println("cart id="+cartId);
//		System.out.println(order.getPaymentMethod());

		Customer customer = this.customerService.findCustomerById(customerId);

		Order o = this.orderService.placeOrder(cartId, customerId, order,
				OrderConstant.generateOrderVerificationCode());
		if (order.getPaymentMethod().equalsIgnoreCase("esewa")) {

			return "redirect:/payment/paymentform?orderId=" + o.getId();
		}
		this.emailUtils.sendEmail(OrderConstant.SUBJECT, BillConstant.getURL(request), customer, OrderConstant.CONTENT,
				true, false, null);
		if (o != null) {
			session.setAttribute("success",
					"Your order is successfully placed!Please verify your order through the mail that we have send to you.");
		} else {
			session.setAttribute("error", "Error placing order!");
		}
		return "redirect:/user/userhelp/" + customerId + "/" + cartId;
	}

	@GetMapping("/userhelp/{customerId}/{cartId}")
	public String help(@PathVariable int customerId, @PathVariable int cartId, Model m) {
		m.addAttribute("customerId", customerId);
		m.addAttribute("cartId", cartId);
		return "/user/userhelp";
	}

	@GetMapping("/verifyOrder")
	public String verifyOrder(@Param("code") String code, HttpSession session, Model m) {
		if (this.verifyCode.verifyCode(code)) {
			Order order = this.orderService.findByCustomerAndIsVerifiedFalse(verifyCode.findByCode(code));
			if (order != null) {
				order.setVerified(true);
				Order o = orderService.saveOrder(order);
				List<OrderItem> orderItems = o.getOrderItems();
				if (o != null) {
					m.addAttribute("order", o);
					m.addAttribute("items", orderItems);
					return "/user/ordersuccess";
				}
			}
		}
		return "/user/verifyerror";
	}
}
