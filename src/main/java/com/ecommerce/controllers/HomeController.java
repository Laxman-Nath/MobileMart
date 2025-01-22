package com.ecommerce.controllers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.ecommerce.constants.BillConstant;
import com.ecommerce.constants.OrderConstant;
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
import com.ecommerce.services.CategoryService;
import com.ecommerce.services.CustomerService;
import com.ecommerce.services.OrderService;
import com.ecommerce.services.ProductService;
import com.ecommerce.services.SaleService;
import com.ecommerce.servicesimpl.CategoryServiceImpl;
import com.ecommerce.servicesimpl.CustomerServiceImpl;
import com.ecommerce.servicesimpl.ProductServiceImpl;
import com.ecommerce.servicesimpl.SaleServiceImpl;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class HomeController {
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private ProductService productService;

	@Autowired
	private SaleService saleService;
	@Autowired
	private CustomerService customerService;

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
		m.addAttribute("totalProducts", productService.getNumberOfProducts());
		m.addAttribute("deliveredProducts", orderService.findTotalDeliveredProducts());
		m.addAttribute("NumberOfCustomers", customerService.findNumberOfCustomers());
		m.addAttribute("totalSales", orderService.findTotalOrders());
		System.out.println("Inside loggdin user:");
		Cart cart = null;
		boolean isEmptyCart = false;
		if (p != null) {

//			System.out.println("Inside not null");
			String emailString = p.getName();
//			System.out.println(emailString);
			Customer customer = this.customerService.findByEmail(emailString);
			List<Order> orders = this.orderService.findByCustomer(customer);

			if (customerService.findByEmailAndProviderNotGoogle(emailString) != null) {
				m.addAttribute("isLoggedInByGoogle", false);
			} else {
				m.addAttribute("isLoggedInByGoogle", true);
			}
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

		List<Product> latest = this.productService.getLatestProduct();
		m.addAttribute("latest", latest);
	}

	@GetMapping("/index")
	public String index() {
		return "index";
	}

	@GetMapping("/")

	public String home(Model m) {
		List<Product> latest = this.productService.getLatestProduct();
		m.addAttribute("latest", latest);
		latest.stream().forEach(p -> System.out.println("Product first" + p));
		List<Category> categories = this.categoryService.listAllCategory();
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
		customer.setRole("ROLE_USER");
		if (customerService.findByEmail(customer.getEmail()) == null) {

			if (customerService.addCustomer(customer, image) != null) {
				session.setAttribute("success", "You are successfully registered!");

			} else {
				session.setAttribute("error", "Error registering !");
			}
		}

		else {
			session.setAttribute("error", "Customer with this email already exists");
		}
		return "redirect:/register";
	}

	@GetMapping({ "product", "/user/product" })
	public String product(Model m, @RequestParam(required = false, defaultValue = "") String category,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size) {
//		System.out.println("Category="+category);
		List<Category> categories = this.categoryService.findByIsActiveTrue();
		Page<Product> pages = this.productService.getAllProducts(category, page, size);
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
		Product product = this.productService.findByProductId(id);
		if (product != null && !ObjectUtils.isEmpty(product)) {
			m.addAttribute("product", product);
		}
		return "viewDetail";
	}

	@GetMapping({ "/onSale", "/user/onSale" })
	public String onSale(Model m) {
		List<Sale> sales = this.saleService.getAllProductsOnSale();
		m.addAttribute("sales", sales);
		return "onSale";
	}

	@GetMapping("/forgotPassword")
	public String forgotPassword() {
		return "forgot_password_form";
	}

	@PostMapping("/processForgotPassword")
	public String processForgotPassword(HttpSession session, HttpServletRequest request, @RequestParam String email)
			throws IOException {
//		System.out.println("Email is :"+username);
		Customer customer = this.customerService.processForgotPassword(email, request);
		if (customer != null) {

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

		Customer customer = this.customerService.saveResetPassword(id, password, cpassword);
		if (customer != null) {

			session.setAttribute("success", "Your password is succesfully reseted!");

		} else {
			session.setAttribute("error", "Error reseting password!");
		}
		return "login";
	}

	@GetMapping({ "/searchProduct", "/user/searchProduct" })
	public String searchProduct(Model m, @RequestParam(required = false, defaultValue = "") String keyword,
			@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "3") int size, HttpSession session) {
//		System.out.println("Searching for: " + keyword);
//		String noSpacesKeyword = keyword.replaceAll("\\s+", "").trim();
//		System.out.println(noSpacesKeyword);
		m.addAttribute("keyword", keyword);
		Page<Product> pages = this.productService.searchProduct(keyword, page, size);
		if (pages.isEmpty()) {
			session.setAttribute("error",
					"No products found matching your search criteria. Please try different keywords.");
			return "searchproduct";
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
			m.addAttribute("keyword", keyword);
		}
		return "searchproduct";
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		request.getSession().invalidate();
		return "login?logout";
	}
}
