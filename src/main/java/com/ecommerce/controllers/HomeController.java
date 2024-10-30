package com.ecommerce.controllers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.eclipse.angus.mail.handlers.message_rfc822;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.commonutils.EmailUtils;
import com.ecommerce.commonutils.FileUploadUtils;
import com.ecommerce.commonutils.VerifyCodeUtils;
import com.ecommerce.dao.CartDao;
import com.ecommerce.models.Cart;
import com.ecommerce.models.CartProduct;
import com.ecommerce.models.Category;
import com.ecommerce.models.Customer;
import com.ecommerce.models.Order;
import com.ecommerce.models.OrderItem;
import com.ecommerce.models.Product;

import com.ecommerce.models.Sale;
import com.ecommerce.services.CartProductService;
import com.ecommerce.services.CartService;

import com.ecommerce.services.OrderService;
import com.ecommerce.servicesimpl.CategoryServiceImpl;
import com.ecommerce.servicesimpl.CustomerServiceImpl;
import com.ecommerce.servicesimpl.ProductServiceImpl;
import com.ecommerce.servicesimpl.SaleServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	@Autowired
	private CategoryServiceImpl csi;
	@Autowired
	private ProductServiceImpl psi;

	@Autowired
	private SaleServiceImpl ssi;
	@Autowired
	private CustomerServiceImpl customerServiceImpl;
	@Autowired
	private FileUploadUtils fileUpload;
	@Autowired
	private EmailUtils emailUtils;
	@Autowired
	private VerifyCodeUtils verifyCode;
	@Autowired
	private CartService cartService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private CartProductService cartProductService;

	@ModelAttribute
	public void getLoggedInUser(Principal p, Model m) {
		System.out.println("Inside loggdin user:");
		Cart cart = null;
		boolean isEmptyCart = false;
		if (p != null) {

//			System.out.println("Inside not null");
			String emailString = p.getName();
//			System.out.println(emailString);
			Customer customer = this.customerServiceImpl.findByEmail(emailString);
			List<Order> orders = this.orderService.findByCustomer(customer);

			m.addAttribute("orders", orders);

			if (customer != null) {
				cart = this.cartService.getCartByUserIdAndIsCheckedFalse(customer.getId());
				if (cart != null) {
					isEmptyCart = this.cartService.isCartEmpty(cart.getId());
				}
			}
//			System.out.println(customer.getName());
			m.addAttribute("loggedUser", customer);
			m.addAttribute("isEmpty", isEmptyCart);
			m.addAttribute("cart", cart);
		} else {
			System.out.println("Inside null");
			m.addAttribute("loggedUser", null);
		}

		List<Product> latest = this.psi.getLatestProduct();
		m.addAttribute("latest", latest);
	}

	@GetMapping("/index")
	public String index() {
		return "index";
	}

	@GetMapping("/")
	public String home(Model m) {
		List<Product> latest = this.psi.getLatestProduct();
		m.addAttribute("latest", latest);
		latest.stream().forEach(p -> System.out.println("Product first" + p));
		List<Category> categories = this.csi.listAllCategory();
		m.addAttribute("categories", categories);
		return "home";
	}

	@GetMapping("login")
	public String login() {
		System.out.println("login is calling");
		return "login";
	}

	@GetMapping("register")
	public String register() {
		return "register";
	}

	@PostMapping("/registerUser")
	public String registerUser(@ModelAttribute Customer customer, @RequestParam MultipartFile image,
			HttpSession session) throws Exception {
//		System.out.println("I m inside register user");
//		System.out.println(customer.getCpassword());
//		System.out.println("Name="+customer.getName());
//		if (customer != null) {
//			if (!image.isEmpty() && image != null) {
//				customer.setFile(image.getOriginalFilename());
//				File file = new ClassPathResource("static/img").getFile();
//				Path path = Paths.get(file.getAbsolutePath() + File.separator + "profile_img" + File.separator
//						+ image.getOriginalFilename());
//				Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//				customer.setRole("ROLE_USER");
//				Customer c = this.customerServiceImpl.addCustomer(customer);
//				if (!ObjectUtils.isEmpty(c)) {
//					session.setAttribute("registerSuccess", "User registered successfully");
//				} else {
//					session.setAttribute("registerError", "User registered failed");
//				}
//			}
//		} else {
//			System.out.println("Customer is null");
//		}

		if (customer != null && image != null) {
			customer.setFile(image.getOriginalFilename());
			if (fileUpload.uploadFile(image, "src\\main\\resources\\static\\img\\profile_img\\")) {
				customer.setRole("ROLE_USER");
				Customer registeredCustomer = customerServiceImpl.addCustomer(customer);
				if (registeredCustomer != null) {
					session.setAttribute("Success", "User is registered successfully!");
				} else {
					session.setAttribute("Error", "Error registering user!");
				}
			} else {
				session.setAttribute("Error", "Error registering user!");
			}

		} else {
			session.setAttribute("Error", "Error registering user!");
		}
		return "redirect:/register";
	}

	@GetMapping("product")
	public String product(Model m, @RequestParam(required = false, defaultValue = "") String category,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "1") int size) {
//		System.out.println("Category="+category);
		List<Category> categories = this.csi.findByIsActiveTrue();
		Page<Product> pages = this.psi.getAllProducts(category, page, size);
		m.addAttribute("selectedCategory", category);
		m.addAttribute("categories", categories);
//		for(Category c:categories) {
//		        System.out.println(c.getName());
//		}

//		for(Product p:products) {
//			System.out.println(p.getName());
//		}
//        m.addAttribute("products",products);

		List<Product> products = pages.getContent();
		m.addAttribute("products", products);
		m.addAttribute("productsSize", products.size());

		m.addAttribute("page", pages.getNumber());
		m.addAttribute("pageSize", size);
		m.addAttribute("totalElements", pages.getTotalElements());
		m.addAttribute("totalPages", pages.getTotalPages());
		m.addAttribute("isFirst", pages.isFirst());
		m.addAttribute("isLast", pages.isLast());
		return "product";
	}

	@GetMapping("/viewDetail/{id}")
	public String viewDetail(Model m, @PathVariable int id) {
//		System.out.println("Inside viewDetail"+id);
		Product product = this.psi.findByProductId(id);
		if (product != null && !ObjectUtils.isEmpty(product)) {
			m.addAttribute("product", product);
		}
		return "viewDetail";
	}

	@GetMapping("/onSale")
	public String onSale(Model m) {
		List<Sale> sales = this.ssi.getAllProductsOnSale();
		m.addAttribute("sales", sales);
		return "onSale";
	}

	@GetMapping("/forgotPassword")
	public String forgotPassword() {
		return "forgot_password_form";
	}

	@PostMapping("/processForgotPassword")
	public String processForgotPassword(HttpSession session, HttpServletRequest request,
			@RequestParam String username) {
//		System.out.println("Email is :"+username);
		Customer customer = this.customerServiceImpl.findByEmail(username);
		if (customer != null) {

			String content = "Dear [[name]],please click on the link below to reset your password."
					+ "<h3><a href=\"[[path]]\" target=\"_self\">RESET</a></h3> Thank you!";

			String codeString = UUID.randomUUID().toString();
			customer.setCode(codeString);
			this.customerServiceImpl.addCustomer(customer);
			String url = request.getRequestURL().toString();
			url = url.replace(request.getRequestURI(), "");
			String subject = "Reset password";
			this.emailUtils.sendEmail(subject, url, customer, content, false, false, null);
			session.setAttribute("success", "Password reset link is send to your email!");
		} else {
			session.setAttribute("error", "Invalid email");
		}
//		System.out.println("Url is "+url);

		return "forgot_password_form";
	}

	@GetMapping("/resetPassword")
	public String resetPassword(@Param("code") String code, HttpSession session, Model model) {
		System.out.println(code);
		if (!this.verifyCode.verifyCode(code)) {
			session.setAttribute("error", "Your code is invalid!");
			return "forgot_password_form";
		}
		model.addAttribute("id", this.verifyCode.findByCode(code).getId());
		return "reset_password_form";
	}

	@PostMapping("/saveResetPassword")
	public String saveResetPassword(@RequestParam int id, @RequestParam String password, HttpSession session,
			@RequestParam String cpassword) {

		Customer customer = this.customerServiceImpl.findCustomerById(id);
		if (customer != null) {

			customer.setPassword(password);
			customer.setCpassword(cpassword);
			customerServiceImpl.addCustomer(customer);
			session.setAttribute("success", "Your password is succesfully reseted!");

		} else {
			session.setAttribute("error", "Error reseting password!");
		}
		return "login";
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

		return "redirect:viewDetail/" + pid;
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

		return "viewcart";
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
		return "redirect:/viewcart?cid=" + cid + "&uid=" + uid;
	}

	@GetMapping("/increaseProduct")
	public String increaseProductInCart(@RequestParam(required = false, defaultValue = "0") int cid,
			@RequestParam(required = false, defaultValue = "0") int uid, @RequestParam int pid, HttpSession session) {
		
		CartProduct cartProduct = this.cartProductService.findById(pid);
         Product product=cartProduct.getProduct();
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
			return "redirect:/viewcart?cid=" + cid + "&uid=" + uid;
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
		return "redirect:/viewcart?cid=" + cid + "&uid=" + uid;
	}

	@GetMapping("/checkout/{customerId}/{cartId}")
	public String checkout(@PathVariable int customerId, @PathVariable int cartId, Model m) {

		Customer customer = this.customerServiceImpl.findCustomerById(customerId);
		Cart cart = this.cartService.getCartByCartIdAndCustomerIdAndIsCheckedFalse(cartId, customerId);
		System.out.println(customer.getTole());
//		System.out.println("name="+customer.getName());
//		System.out.println("Cart "+cart.getCustomer().getName());
		m.addAttribute("customer", customer);
		m.addAttribute("cart", cart);
		return "checkout";
	}

	@PostMapping("/placeOrder/{customerId}/{cartId}")
	public String placeOrder(@ModelAttribute Order order, @PathVariable int customerId, @PathVariable int cartId,
			HttpSession session, HttpServletRequest request) {
//		System.out.println(order.getShippingAddress());
//		System.out.println("customer id="+customerId);
//		System.out.println("cart id="+cartId);
//		System.out.println(order.getPaymentMethod());
		String code = UUID.randomUUID().toString();

		String content = "Dear [[name]],please click on the link below to verify your order."
				+ "<h3><a href=\"[[path]]\" target=\"_self\">VERIFY</a></h3> Thank you!";
		Customer customer = this.customerServiceImpl.findCustomerById(customerId);
		customer.setCode(code);
		String url = request.getRequestURL().toString();
		url = url.replace(request.getRequestURI(), "");
		String subject = "Verify order";
		this.emailUtils.sendEmail(subject, url, customer, content, true, false, null);
		Order o = this.orderService.placeOrder(cartId, customerId, order);
		if (o != null) {
			session.setAttribute("success",
					"Your order is successfully placed!Please verify your order through the mail that we have send to you.");
		} else {
			session.setAttribute("error", "Error placing order!");
		}
		return "redirect:/userhelp/" + customerId + "/" + cartId;
	}

	@GetMapping("/userhelp/{customerId}/{cartId}")
	public String help(@PathVariable int customerId, @PathVariable int cartId, Model m) {
		m.addAttribute("customerId", customerId);
		m.addAttribute("cartId", cartId);
		return "/userhelp";
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
					return "ordersuccess";
				}
			}
		}
		return "verifyerror";
	}

	@GetMapping("/searchProduct")
	public String searchProduct(Model m, @RequestParam String keyword,
			@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "1") int size, HttpSession session) {
//		System.out.println("Searching for: " + keyword);
//		String noSpacesKeyword = keyword.replaceAll("\\s+", "").trim();
//		System.out.println(noSpacesKeyword);

		Page<Product> pages = this.psi.searchProduct(keyword, page, size);
		if (pages.isEmpty()) {
			session.setAttribute("error",
					"No products found matching your search criteria. Please try different keywords.");
		}

		else {
			List<Product> products = pages.getContent();
			m.addAttribute("products", products);
			m.addAttribute("productsSize", products.size());

			m.addAttribute("page", pages.getNumber());
			m.addAttribute("pageSize", size);
			m.addAttribute("totalElements", pages.getTotalElements());
			m.addAttribute("totalPages", pages.getTotalPages());
			m.addAttribute("isFirst", pages.isFirst());
			m.addAttribute("isLast", pages.isLast());
		}
		return "searchproduct";
	}

	@GetMapping("/logout")
	public String logout() {
		return "login?logout";
	}
}
