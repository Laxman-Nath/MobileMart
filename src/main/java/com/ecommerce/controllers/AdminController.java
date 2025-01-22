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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ecommerce.commonutils.FileUploadUtils;
import com.ecommerce.models.Category;
import com.ecommerce.models.Customer;
import com.ecommerce.models.Order;
import com.ecommerce.models.OrderItem;
import com.ecommerce.models.Product;
import com.ecommerce.models.Sale;
import com.ecommerce.services.CategoryService;
import com.ecommerce.services.CustomerService;
import com.ecommerce.services.OrderItemService;
import com.ecommerce.services.OrderService;
import com.ecommerce.services.ProductService;
import com.ecommerce.services.SaleService;
import com.ecommerce.servicesimpl.CategoryServiceImpl;
import com.ecommerce.servicesimpl.CustomerServiceImpl;
import com.ecommerce.servicesimpl.ProductServiceImpl;
import com.ecommerce.servicesimpl.SaleServiceImpl;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import ch.qos.logback.core.joran.conditional.IfAction;
import jakarta.servlet.http.HttpSession;

@Controller
@PreAuthorize("hasRole('ADMIN')")
//@RequestMapping("/admin")
public class AdminController {
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private SaleService saleService;
	@Autowired
	private FileUploadUtils fileUpload;
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderItemService orderItemService;

	@GetMapping("/admin/")

	public String adminPanel(Model m) {

		return "admin/Admindashboard";
	}

	@ModelAttribute
	public void getLoggedInUser(Principal p, Model m) {
		m.addAttribute("totalProducts", productService.getNumberOfProducts());
		m.addAttribute("deliveredProducts", orderService.findTotalDeliveredProducts());
		m.addAttribute("NumberOfCustomers", customerService.findNumberOfCustomers());
		m.addAttribute("totalSales", orderService.findTotalOrders());
		List<Category> categories = categoryService.findByIsActiveTrue();

		m.addAttribute("categories", categories);
		String emailString = p.getName();
		Customer customer = customerService.findByEmail(emailString);
		List<Order> orders = this.orderService.findByCustomer(customer);
		m.addAttribute("orders", orders);
		m.addAttribute("loggedUser", customer);
	}

	@GetMapping("admin/addProduct")
	public String product(Model m) {
		List<Category> categories = categoryService.listAllCategory();
		m.addAttribute("categories", categories);
		return "admin/addProduct";
	}

	@PostMapping("admin/saveProduct")
	public String addProduct(@ModelAttribute("Product") Product product, @RequestParam("image") MultipartFile image,
			HttpSession session) throws IOException {

		if (productService.existByName(product.getName())) {
			session.setAttribute("error", "Product already exists");
		} else {
			if (productService.addProduct(product, image) != null) {
				session.setAttribute("success", "Product added successfully!");
			} else {
				session.setAttribute("error", "Error adding product!");
			}
		}
		return "redirect:/admin/addProduct";
	}

	@GetMapping("admin/viewProducts")
	public String viewProduct(@RequestParam(required = false, defaultValue = "") String category, Model m,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int pageNo) {
		Page<Product> pages = productService.getAllProducts(category, page, pageNo);
		List<Sale> sales = saleService.getAllProductsOnSale();

		m.addAttribute("sales", sales);

		List<Product> products = pages.getContent();
		m.addAttribute("products", products);
		m.addAttribute("productsSize", products.size());
		m.addAttribute("products", products);
		m.addAttribute("page", pages.getNumber());
		m.addAttribute("pageSize", pageNo);
		m.addAttribute("totalElements", pages.getTotalElements());
		m.addAttribute("totalPages", pages.getTotalPages());
		m.addAttribute("isFirst", pages.isFirst());
		m.addAttribute("isLast", pages.isLast());
		return "admin/viewProducts";
	}

	@GetMapping("admin/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		if (productService.deleteProductById(id)) {
			session.setAttribute("success", "Product deleted Successfully");
		} else {
			session.setAttribute("error", "Error deleting product");
		}
		return "redirect:/admin/viewProducts";
	}

	@GetMapping("admin/editProductForm/{id}")
	public String editProductForm(@PathVariable int id, HttpSession session, Model m) {
		Product product = productService.findByProductId(id);
		List<Category> categories = categoryService.listAllCategory();
		if (product != null) {
			m.addAttribute("product", product);
			m.addAttribute("categories", categories);
		}
		return "admin/editProductForm";
	}

	@PostMapping("admin/editProduct")
	public String editProduct(@ModelAttribute Product product, @RequestParam String oldfile,
			@RequestParam MultipartFile newfile, HttpSession session) throws IOException {
		Product oldProduct = productService.findByProductId(product.getId());
		if (productService.editProduct(oldProduct, product, newfile) != null) {
			session.setAttribute("success", "Product is successfully edited!");
		} else {
			session.setAttribute("error", "Error editing products!");
		}
		return "redirect:/admin/viewProducts";
	}

	@GetMapping("admin/category")
	public ModelAndView category() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("categories", categoryService.listAllCategory());
		mv.setViewName("admin/category");
		return mv;
	}

	@PostMapping("admin/addCategory")
	public String addCategory(@ModelAttribute Category category, @RequestParam("image") MultipartFile image,
			HttpSession session, @RequestParam("status") Boolean status) throws IOException {

		if (this.categoryService.existsByName(category.getName())) {
			session.setAttribute("error", "Category already exists in database");
		} else if (image != null) {
			if (categoryService.addCategory(category, image, status) != null) {
				session.setAttribute("success", "Successfully added category!");
			} else {
				session.setAttribute("error", "Error adding  category!");
			}
		}

		return "redirect:/admin/category";
	}

	@GetMapping("admin/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id, HttpSession session) {
		if (categoryService.deleteCategoryById(id)) {
			session.setAttribute("success", "Category is successfully deleted");
		} else {
			session.setAttribute("error", "Error deleting category");
		}
		return "redirect:/admin/category";
	}

	@GetMapping("admin/editCategoryForm/{id}")
	public ModelAndView editCategoryForm(@PathVariable int id) {
		ModelAndView mv = new ModelAndView();
		Category category = categoryService.findByCategoryId(id);
		if (category != null) {
			mv.addObject("category", category);
		}
		mv.setViewName("admin/editCategoryForm");
		return mv;
	}

	@PostMapping("admin/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam String oldfile,
			@RequestParam MultipartFile newfile, @RequestParam Boolean status, HttpSession session) throws IOException {

		if (this.categoryService.updateCategory(category, newfile, status) != null) {
			session.setAttribute("success", "Category is successfully updated!");
		} else {
			session.setAttribute("error", "Error updating category!");
		}

		return "redirect:/admin/category";

	}

	@GetMapping("admin/addToSale/{id}")
	public String addToSale(@PathVariable int id, Model m, HttpSession session) {
		if (saleService.getProductOnSaleByProductId(id)) {
			session.setAttribute("error", "Product was already added to sale");
			return "redirect:/admin/viewProducts";
		}
		Product product = productService.findByProductId(id);
		m.addAttribute("product", product);
		System.out.println("Products price.................." + product.getPrice());
		System.out.println("Products Discount percent.................." + product.getDiscountPercent());
//		m.addAttribute("sale",new Sale());
		return "admin/addToSale";
	}

	@GetMapping("admin/removeFromSale/{id}")
	public String removeFromSale(@PathVariable int id, HttpSession session) {
		Sale sale = saleService.getByProductId(id);
		if (sale != null) {
			saleService.deleteByProductId(id);
			session.setAttribute("success", "Product is successfully deleted from sale");
		} else {
			session.setAttribute("error", "Error deleting product from sale");
		}
		return "redirect:/admin/viewProducts";
	}

	@PostMapping("admin/saveProductToSale")
	public String saveProductToSale(@ModelAttribute Sale sale, @RequestParam int productId, HttpSession session) {

//		sale.setDiscountedPrice(psi.findByProductId(productId).getDiscountedPrice());
		Sale s = saleService.addProductToSale(sale, productId);
		if (s != null) {
			session.setAttribute("success", "Product was added to sale");
		} else {
			session.setAttribute("error", "Error adding product to sale");
		}
		return "redirect:/admin/viewProducts";
	}

	@GetMapping("admin/viewCustomers")
	public String viewUsers(Model m) {
		List<Customer> customers = customerService.findAllCustomers("ROLE_USER");
		m.addAttribute("customers", customers);
		return "admin/ViewUsers";
	}

	@GetMapping("admin/updateCustomerStatus/{id}")
	public String updateCustomerStatus(@PathVariable int id, HttpSession session) {
		if (customerService.updateCustomerStatus(id)) {
			session.setAttribute("success", "Successfully updated!");
		} else {
			session.setAttribute("error", "Error updating!");
		}
		return "redirect:/admin/viewCustomers";
	}

	@GetMapping("admin/showorders")
	public String showOrders(HttpSession session, @RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(defaultValue = "0") int pageNo, Model m) {
		Page<Order> orders = orderService.showVerifiedOrders(pageNo, pageSize);
		m.addAttribute("orders", orders.getContent());
		m.addAttribute("orderssize", orders.getTotalElements());
		m.addAttribute("page", orders.getNumber());
		m.addAttribute("pageSize", orders.getSize());
		m.addAttribute("totalElements", orders.getTotalElements());
		m.addAttribute("totalPages", orders.getTotalPages());
		m.addAttribute("isFirst", orders.isFirst());
		m.addAttribute("isLast", orders.isLast());

		return "admin/showverifiedorders";
	}

	@GetMapping("admin/showunverifiedorders")
	public String showUnverifiedOrders(HttpSession session, @RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(defaultValue = "0") int pageNo, Model m) {
		Page<Order> orders = orderService.showVerifiedOrders(pageNo, pageSize);
		m.addAttribute("orders", orders.getContent());
		m.addAttribute("orderssize", orders.getTotalElements());
		m.addAttribute("page", orders.getNumber());
		m.addAttribute("pageSize", orders.getSize());
		m.addAttribute("totalElements", orders.getTotalElements());
		m.addAttribute("totalPages", orders.getTotalPages());
		m.addAttribute("isFirst", orders.isFirst());
		m.addAttribute("isLast", orders.isLast());

		return "admin/unverifiedorders";
	}

	@GetMapping("admin/vieworderdetails/{id}")
	public String showProductDetails(@PathVariable int id, Model m) {
		Order order = orderService.findById(id);
		List<OrderItem> items = order.getOrderItems();
		m.addAttribute("items", items);
		m.addAttribute("order", order);
		return "admin/orderdetails";
	}

	@GetMapping("admin/editorder/{id}")
	public String editOrder(@PathVariable int id, Model m) {
		System.out.println("The id is :" + id);
		Order order = orderService.findById(id);
		List<OrderItem> items = order.getOrderItems();
		m.addAttribute("order", order);
		m.addAttribute("items", items);
		return "admin/editorderform";
	}

	@DeleteMapping("admin/deleteProductFromOrder/{itemId}")
	public ResponseEntity<String> deleteProductFromOrder(@PathVariable int itemId, @RequestParam int orderId) {
		try {
			if (orderService.deleteProductFromOrder(itemId)) {

				return ResponseEntity.ok("Product deleted successfully.");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
			}
		} catch (Exception e) {
			logger.error("Failed to delete product from order", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to delete product. Please try again.");
		}
	}

	@PatchMapping("admin/increaseProductFromOrder/{itemId}")
	public ResponseEntity<String> increaseProductFromOrder(@PathVariable int itemId, @RequestParam int orderId) {
		OrderItem orderItem = this.orderItemService.findById(itemId);
		Product product = orderItem.getProduct();

		try {
			if (product.getQuantity() <= orderItem.getQuantity()) {
				System.out.println("******************Inside if************************** ");
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body("Cannot increase product quantity. Available: " + product.getQuantity());
			} else {
				if (orderService.increaseProductFromOrder(itemId)) {
					String message = "Product is increased successfully";
					Map<String, Object> response = new HashMap<>();
					response.put("message", message);
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

	@PatchMapping("admin/decreaseProductFromOrder/{itemId}")
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

	@PostMapping("admin/updateOrder/{id}")
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

		return "redirect:/admin/vieworderdetails/" + id;
	}

	@GetMapping("admin/viewProfile/{userId}")
	public String viewProfile(@PathVariable Integer userId, Model m) {
//		System.out.println("The id is:" + userId);s
		Customer customer = this.customerService.findCustomerById(userId);
		m.addAttribute("customer", customer);
		return "admin/AdminDetails";
	}

	@GetMapping("admin/EditProfile/{userId}")
	public String editProfile(@PathVariable Integer userId, Model m) {
//		System.out.println("The id is:" + userId);
		Customer customer = this.customerService.findCustomerById(userId);
		m.addAttribute("customer", customer);
		return "admin/EditProfile";
	}

	@PostMapping("admin/saveUpdatedProfile")
	public String saveUpdatedProfile(@ModelAttribute("customer") Customer customer, HttpSession session,
			@RequestParam(required = false) MultipartFile image) throws IOException {
		logger.info("Name :{}", customer.getName());
		logger.info("Role :{}", customer.getRole());
//	
		customer.setRole("ROLE_ADMIN");
		Customer c = this.customerService.updateCustomer(customer, image);
		if (c != null) {
			logger.info("updated customer");
			session.setAttribute("success", "Your profile is successfully updated!");
		} else {
			session.setAttribute("error", "Error updating your profile!");
		}
		return "redirect:/admin/viewProfile/" + customer.getId();
	}

	@GetMapping("admin/changePassword/{userId}")
	public String changePassword(@PathVariable int userId, Model m) {
		Customer customer = this.customerService.findCustomerById(userId);
		System.out.println("Name is:" + customer.getName());
		m.addAttribute("customer", customer);
		return "/admin/changePassword";
	}

	@PostMapping("admin/saveChangedPassword")
	public String saveChangedPassword(@RequestParam int id, @RequestParam String password,
			@RequestParam String newpassword, @RequestParam String rnewpassword, HttpSession session) {
		if (!newpassword.equals(rnewpassword)) {
			session.setAttribute("success", "Confirm password does not match the new password.!");
		} else if (customerService.changePassword(id, password, newpassword)) {
			session.setAttribute("success", "Your password is changed successfully!");
		} else {
			session.setAttribute("error", "Error changing password!");
		}
		return "redirect:/admin/changePassword/" + id;
	}

	@GetMapping("admin/addAdmin")
	public String addAdmin() {

		return "/admin/addAdmin";
	}

	@PostMapping("admin/saveAdmin")
	public String registerUser(@ModelAttribute Customer customer, @RequestParam MultipartFile image,
			HttpSession session) throws IOException {

		customer.setRole("ROLE_ADMIN");

		if (customerService.addCustomer(customer, image) != null) {
			session.setAttribute("Success", "Admin is successfully registered!");

		} else {
			session.setAttribute("Error", "Error registering admin!");
		}
		return "redirect:/admin/addAdmin";
	}

	@GetMapping("admin/getAllSubmittedOrders")
	public String getAllSubmittedOrders(@RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(defaultValue = "0") int pageNo, Model m) {
		Page<Order> submittedOrders = orderService.getAllSubmittedOrders(pageNo, pageSize);
		m.addAttribute("orders", submittedOrders.getContent());
		m.addAttribute("orderssize", submittedOrders.getTotalElements());
		m.addAttribute("page", submittedOrders.getNumber());
		m.addAttribute("pageSize", submittedOrders.getSize());
		m.addAttribute("totalElements", submittedOrders.getTotalElements());
		m.addAttribute("totalPages", submittedOrders.getTotalPages());
		m.addAttribute("isFirst", submittedOrders.isFirst());
		m.addAttribute("isLast", submittedOrders.isLast());
		return "/admin/submittedorders";
	}

	@GetMapping("admin/getAllShippedOrders")
	public String getAllShippedOrders(@RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(defaultValue = "0") int pageNo, Model m) {
		Page<Order> orders = orderService.getAllShippedOrders(pageNo, pageSize);
		m.addAttribute("orders", orders.getContent());
		m.addAttribute("orderssize", orders.getTotalElements());
		m.addAttribute("page", orders.getNumber());
		m.addAttribute("pageSize", orders.getSize());
		m.addAttribute("totalElements", orders.getTotalElements());
		m.addAttribute("totalPages", orders.getTotalPages());
		m.addAttribute("isFirst", orders.isFirst());
		m.addAttribute("isLast", orders.isLast());
		return "/admin/shippedorders";
	}

	@GetMapping("admin/getAllDeliveredOrders")
	public String getAllDeliveredOrders(@RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(defaultValue = "0") int pageNo, Model m) {
		Page<Order> orders = orderService.getAllDeliveredOrders(pageNo, pageSize);
		m.addAttribute("orders", orders.getContent());
		m.addAttribute("orderssize", orders.getTotalElements());
		m.addAttribute("page", orders.getNumber());
		m.addAttribute("pageSize", orders.getSize());
		m.addAttribute("totalElements", orders.getTotalElements());
		m.addAttribute("totalPages", orders.getTotalPages());
		m.addAttribute("isFirst", orders.isFirst());
		m.addAttribute("isLast", orders.isLast());
		return "/admin/deliveredorders";
	}
}
