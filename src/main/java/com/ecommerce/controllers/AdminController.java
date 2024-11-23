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
import com.ecommerce.services.OrderItemService;
import com.ecommerce.services.OrderService;
import com.ecommerce.servicesimpl.CategoryServiceImpl;
import com.ecommerce.servicesimpl.CustomerServiceImpl;
import com.ecommerce.servicesimpl.ProductServiceImpl;
import com.ecommerce.servicesimpl.SaleServiceImpl;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	private CategoryServiceImpl csi;

	@Autowired
	private ProductServiceImpl psi;

	@Autowired
	private CustomerServiceImpl customerServiceImpl;

	@Autowired
	private SaleServiceImpl ssi;
	@Autowired
	private FileUploadUtils fileUpload;
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderItemService orderItemService;

	@GetMapping("/")
	public String adminPanel(Model m) {
		m.addAttribute("totalProducts",psi.getNumberOfProducts());
		m.addAttribute("deliveredProducts",orderService.findTotalDeliveredProducts());
		m.addAttribute("NumberOfCustomers",customerServiceImpl.findNumberOfCustomers());
		m.addAttribute("totalSales",orderService.findTotalOrders());
		return "admin/Admindashboard";
	}

	@ModelAttribute
	public void getLoggedInUser(Principal p, Model m) {
		List<Category> categories = csi.findByIsActiveTrue();
	
		m.addAttribute("categories", categories);
		String emailString = p.getName();
		Customer customer = customerServiceImpl.findByEmail(emailString);
		List<Order> orders = this.orderService.findByCustomer(customer);
		m.addAttribute("orders", orders);
		m.addAttribute("loggedUser", customer);
	}

	@GetMapping("/addProduct")
	public String product(Model m) {
		List<Category> categories = csi.listAllCategory();
		m.addAttribute("categories", categories);
		return "admin/addProduct";
	}

	@PostMapping("/saveProduct")
	public String addProduct(@ModelAttribute("Product") Product product, @RequestParam("image") MultipartFile image,
			HttpSession session) throws IOException {

		if (psi.existByName(product.getName())) {
			session.setAttribute("addProductError", "Product already exists");
		} else {
			product.setFile(image.getOriginalFilename());
			double discountedPrice = product.getPrice() - ((product.getPrice() * product.getDiscountPercent()) / 100);
			product.setDiscountedPrice(discountedPrice);
			Product p = psi.addProduct(product);
			if (p != null) {
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
						+ image.getOriginalFilename());
				Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				session.setAttribute("addProductSuccess", "Product successfully added");
			} else {
				session.setAttribute("addProductError", "Something went wrong");
			}
		}
		return "redirect:/admin/addProduct";
	}

	@GetMapping("/viewProducts")
	public String viewProduct(@RequestParam(required = false, defaultValue = "") String category, Model m,@RequestParam (defaultValue = "0") int page,@RequestParam(defaultValue = "5") int pageNo) {
		Page<Product> products = psi.getAllProducts(category,page,pageNo);
		List<Sale> sales = ssi.getAllProductsOnSale();
		m.addAttribute("products", products);
		m.addAttribute("sales", sales);
		return "admin/viewProducts";
	}

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		if (psi.deleteProductById(id)) {
			session.setAttribute("deleteProductSuccess", "Product deleted Successfully");
		} else {
			session.setAttribute("deleteProductError", "Error deleting product");
		}
		return "redirect:/admin/viewProducts";
	}

	@GetMapping("/editProductForm/{id}")
	public String editProductForm(@PathVariable int id, HttpSession session, Model m) {
		Product product = psi.findByProductId(id);
		List<Category> categories = csi.listAllCategory();
		if (product != null) {
			m.addAttribute("product", product);
			m.addAttribute("categories", categories);
		}
		return "admin/editProductForm";
	}

	@PostMapping("/editProduct")
	public String editProduct(@ModelAttribute Product product, @RequestParam String oldfile,
			@RequestParam MultipartFile newfile, HttpSession session) throws IOException {
		Product oldProduct = psi.findByProductId(product.getId());
		oldProduct.setName(product.getName());
		oldProduct.setDimension(product.getDimension());
		oldProduct.setWeight(product.getWeight());
		oldProduct.setBuild(product.getBuild());
		oldProduct.setSim(product.getSim());
		oldProduct.setColor(product.getColor());
		oldProduct.setType(product.getType());
		oldProduct.setSize(product.getSize());
		oldProduct.setResolution(product.getResolution());
		oldProduct.setOs(product.getOs());
		oldProduct.setChipSet(product.getChipSet());
		oldProduct.setCpu(product.getCpu());
		oldProduct.setGpu(product.getGpu());
		oldProduct.setCardSlot(product.getCardSlot());
		oldProduct.setInternal(product.getInternal());
		oldProduct.setBFeatures(product.getBFeatures());
		oldProduct.setBVideo(product.getBVideo());
		oldProduct.setFFeatures(product.getFFeatures());
		oldProduct.setFVideo(product.getFVideo());
		oldProduct.setLoudSpeaker(product.getLoudSpeaker());
		oldProduct.setSFeatures(product.getSFeatures());
		oldProduct.setBType(product.getBType());
		oldProduct.setCharging(product.getCharging());
		oldProduct.setCFeatures(product.getCFeatures());
		oldProduct.setSecurity(product.getSecurity());

		oldProduct.setStatus(product.getStatus());
		oldProduct.setCategory(product.getCategory());
		oldProduct.setPolicy(product.getPolicy());
		oldProduct.setPrice(product.getPrice());
		oldProduct.setQuantity(product.getQuantity());

		if (oldProduct.getDiscountPercent() != product.getDiscountPercent()) {
			oldProduct.setDiscountPercent(product.getDiscountPercent());
			double discountedPrice = product.getPrice() - ((product.getPrice() * product.getDiscountPercent()) / 100);
			oldProduct.setDiscountedPrice(discountedPrice);
		}

		if (csi.existsByName(oldProduct.getName())) {
			session.setAttribute("ProductEditError", "Product with similar name already exists!!");
		} else {
			if (!ObjectUtils.isEmpty(newfile) && !newfile.isEmpty()) {
				oldProduct.setFile(newfile.getOriginalFilename());
				File file = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(file.getAbsolutePath() + File.separator + "product_img" + File.separator
						+ newfile.getOriginalFilename());
				Files.copy(newfile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			Product p = psi.addProduct(oldProduct);
			if (p == null) {
				session.setAttribute("ProductEditError", "Error editing Product");
			} else {
				session.setAttribute("ProductEditSuccess", "Successfully Edited Product");
			}
		}
		return "redirect:/admin/viewProducts";
	}

	@GetMapping("/category")
	public ModelAndView category() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("categories", csi.listAllCategory());
		mv.setViewName("admin/category");
		return mv;
	}

	@PostMapping("/addCategory")
	public String addCategory(@ModelAttribute Category category, @RequestParam("image") MultipartFile image,
			HttpSession session, @RequestParam("status") Boolean status) throws IOException {

//		if (csi.existsByName(category.getName())) {
//			session.setAttribute("error", "Category already exists in database");
//		} else {
//			category.setFile(image.getOriginalFilename());
//			category.setActive(status);
//			Category cat = csi.addCategory(category);
//			if (cat != null) {
//				File saveFile = new ClassPathResource("static/img").getFile();
//				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
//						+ image.getOriginalFilename());
//				Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//				session.setAttribute("success", "Successfully added category");
//			} else {
//				session.setAttribute("error", "Something went wrong");
//			}
//		}

		if (this.csi.existsByName(category.getName())) {
			session.setAttribute("error", "Category already exists in database");
		} else if (image != null) {
			category.setFile(image.getOriginalFilename());
			if (fileUpload.uploadFile(image, "src\\main\\resources\\static\\img\\category_img\\")) {
				Category registeredCategory = this.csi.addCategory(category);
				if (registeredCategory != null) {
					session.setAttribute("success", "Successfully added category");
				} else {
					session.setAttribute("error", "Something went wrong");
				}
			} else {
				session.setAttribute("error", "Something went wrong");
			}
		}

		return "redirect:/admin/category";
	}

	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id, HttpSession session) {
		if (csi.deleteCategoryById(id)) {
			session.setAttribute("Deletesuccess", "Category is successfully deleted");
		} else {
			session.setAttribute("Deleteerror", "Error deleting category");
		}
		return "redirect:/admin/category";
	}

	@GetMapping("/editCategoryForm/{id}")
	public ModelAndView editCategoryForm(@PathVariable int id) {
		ModelAndView mv = new ModelAndView();
		Category category = csi.findByCategoryId(id);
		if (category != null) {
			mv.addObject("category", category);
		}
		mv.setViewName("admin/editCategoryForm");
		return mv;
	}

	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam String oldfile,
			@RequestParam MultipartFile newfile, @RequestParam Boolean status, HttpSession session) throws IOException {
		Category oldcategory = csi.findByCategoryId(category.getId());
		oldcategory.setName(category.getName());
		oldcategory.setActive(status);

		
			if (!ObjectUtils.isEmpty(newfile) && !newfile.isEmpty()) {
				oldcategory.setFile(newfile.getOriginalFilename());
				if (fileUpload.uploadFile(newfile, "src\\main\\resources\\static\\img\\category_img\\")) {
					Category cat = csi.addCategory(oldcategory);
					if (cat == null) {
						session.setAttribute("EditError", "Error editing category");
					} else {
						session.setAttribute("EditSuccess", "Successfully Edited category");
					}
				}
			}

		
		return "redirect:/admin/category";

	}

	@GetMapping("addToSale/{id}")
	public String addToSale(@PathVariable int id, Model m, HttpSession session) {
		if (ssi.getProductOnSaleByProductId(id)) {
			session.setAttribute("saleError", "Product was already added to sale");
			return "redirect:/admin/viewProducts";
		}
		Product product = psi.findByProductId(id);
		m.addAttribute("product", product);
		System.out.println("Products price.................."+product.getPrice());
		System.out.println("Products Discount percent.................."+product.getDiscountPercent());
//		m.addAttribute("sale",new Sale());
		return "admin/addToSale";
	}

	@GetMapping("removeFromSale/{id}")
	public String removeFromSale(@PathVariable int id, HttpSession session) {
		Sale sale = ssi.getByProductId(id);
		if (sale != null) {
			ssi.deleteByProductId(id);
			session.setAttribute("saleSuccess", "Product is successfully deleted from sale");
		} else {
			session.setAttribute("saleError", "Error deleting product from sale");
		}
		return "redirect:/admin/viewProducts";
	}

	@PostMapping("saveProductToSale")
	public String saveProductToSale(@ModelAttribute Sale sale, @RequestParam int productId, HttpSession session) {
		
//		sale.setDiscountedPrice(psi.findByProductId(productId).getDiscountedPrice());
		Sale s = ssi.addProductToSale(sale,productId);
		if (s != null) {
			session.setAttribute("saleSuccess", "Product was added to sale");
		} else {
			session.setAttribute("saleError", "Error adding product to sale");
		}
		return "redirect:/admin/viewProducts";
	}

	@GetMapping("/viewCustomers")
	public String viewUsers(Model m) {
		List<Customer> customers = customerServiceImpl.findAllCustomers("ROLE_USER");
		m.addAttribute("customers", customers);
		return "admin/ViewUsers";
	}

	@GetMapping("updateCustomerStatus/{id}")
	public String updateCustomerStatus(@PathVariable int id, HttpSession session) {
		if (customerServiceImpl.updateCustomerStatus(id)) {
			session.setAttribute("success", "Successfully updated!");
		} else {
			session.setAttribute("success", "Error updating!");
		}
		return "redirect:/admin/viewCustomers";
	}

	@GetMapping("/showorders")
	public String showOrders(HttpSession session, Model m) {
		List<Order> orders = orderService.showVerifiedOrders();
		if (orders.isEmpty()) {
			session.setAttribute("error", "There are no verified orders!");
		}
		m.addAttribute("orders", orders);
		return "admin/showverifiedorders";
	}

	@GetMapping("/showunverifiedorders")
	public String showUnverifiedOrders(HttpSession session, Model m) {
		List<Order> orders = orderService.showUnverfiedOrders();
		if (orders.isEmpty()) {
			session.setAttribute("error", "There are no unverified orders!");
		}
		m.addAttribute("orders", orders);
		return "admin/unverifiedorders";
	}

	@GetMapping("/vieworderdetails/{id}")
	public String showProductDetails(@PathVariable int id, Model m) {
		Order order = orderService.findById(id);
		List<OrderItem> items = order.getOrderItems();
		m.addAttribute("items", items);
		m.addAttribute("order", order);
		return "admin/orderdetails";
	}

	@GetMapping("/editorder/{id}")
	public String editOrder(@PathVariable int id, Model m) {
		System.out.println("The id is :" + id);
		Order order = orderService.findById(id);
		List<OrderItem> items = order.getOrderItems();
		m.addAttribute("order", order);
		m.addAttribute("items", items);
		return "admin/editorderform";
	}

	@DeleteMapping("/deleteProductFromOrder/{itemId}")
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

	@PatchMapping("/increaseProductFromOrder/{itemId}")
	public ResponseEntity<String> increaseProductFromOrder(@PathVariable int itemId, @RequestParam int orderId) {
		  OrderItem orderItem = this.orderItemService.findById(itemId);
		    Product product = orderItem.getProduct();

		    try {
		        if (product.getQuantity() <= orderItem.getQuantity()) {
		        	System.out.println("******************Inside if************************** ");
		            return ResponseEntity.status(HttpStatus.CONFLICT)
		                    .body("Cannot increase product quantity. Available: " + product.getQuantity());
		        }
		        else {
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

		return "redirect:/admin/vieworderdetails/" + id;
	}

	@GetMapping("/viewProfile/{userId}")
	public String viewProfile(@PathVariable Integer userId, Model m) {
//		System.out.println("The id is:" + userId);s
		Customer customer = this.customerServiceImpl.findCustomerById(userId);
		m.addAttribute("customer", customer);
		return "admin/AdminDetails";
	}

	@GetMapping("/EditProfile/{userId}")
	public String editProfile(@PathVariable Integer userId, Model m) {
//		System.out.println("The id is:" + userId);
		Customer customer = this.customerServiceImpl.findCustomerById(userId);
		m.addAttribute("customer", customer);
		return "admin/EditProfile";
	}

	@PostMapping("/saveUpdatedProfile")
	public String saveUpdatedProfile(@ModelAttribute("customer") Customer customer, HttpSession session,
			@RequestParam(required = false) MultipartFile image) throws IOException {
		System.out.println("Password:" + customer.getPassword());
		if (!image.isEmpty() && image != null) {
			if (!fileUpload.uploadFile(image, "src\\main\\resources\\static\\img\\profile_img\\")) {
				session.setAttribute("error", "Error updating your profile!");
				return "redirect:/admin/viewProfile/" + customer.getId();
			}
		}
//		customer.setFile(image.getOriginalFilename());
		customer.setRole("ROLE_ADMIN");
		Customer c = this.customerServiceImpl.updateCustomer(customer);
		if (c != null) {
			session.setAttribute("success", "Your profile is successfully updated!");
		} else {
			session.setAttribute("error", "Error updating your profile!");
		}
		return "redirect:/admin/viewProfile/" + customer.getId();
	}

	@GetMapping("/changePassword/{userId}")
	public String changePassword(@PathVariable int userId, Model m) {
		Customer customer = this.customerServiceImpl.findCustomerById(userId);
		System.out.println("Name is:" + customer.getName());
		m.addAttribute("customer", customer);
		return "/admin/changePassword";
	}

	@PostMapping("/saveChangedPassword")
	public String saveChangedPassword(@RequestParam int id, @RequestParam String password,
			@RequestParam String newpassword, HttpSession session) {
		if (customerServiceImpl.changePassword(id, password, newpassword)) {
			session.setAttribute("success", "Your password is changed successfully!");
		} else {
			session.setAttribute("error", "Error changing password!");
		}
		return "redirect:/admin/changePassword/" + id;
	}

	@GetMapping("/addAdmin")
	public String addAdmin() {

		return "/admin/addAdmin";
	}

	@PostMapping("/saveAdmin")
	public String registerUser(@ModelAttribute Customer customer, @RequestParam MultipartFile image,
			HttpSession session) throws IOException {

		if (customer != null && image != null) {
			customer.setFile(image.getOriginalFilename());
			if (fileUpload.uploadFile(image, "src\\main\\resources\\static\\img\\profile_img\\")) {
				customer.setRole("ROLE_ADMIN");
				Customer registeredCustomer = customerServiceImpl.addCustomer(customer);
				if (registeredCustomer != null) {
					session.setAttribute("Success", "Admin is registered successfully!");
				} else {
					session.setAttribute("Error", "Error registering admin!");
				}
			} else {
				session.setAttribute("Error", "Error registering admin!");
			}

		} else {
			session.setAttribute("Error", "Error registering admin!");
		}
		return "redirect:/admin/addAdmin";
	}
}
