package com.ecommerce.controllers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecommerce.commonutils.EmailUtils;
import com.ecommerce.constants.BillConstant;
import com.ecommerce.models.Category;
import com.ecommerce.models.Customer;
import com.ecommerce.models.Order;

import com.ecommerce.services.CustomerService;
import com.ecommerce.services.OrderService;
import com.ecommerce.services.ProductService;
import com.ecommerce.services.ReportService;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.itextpdf.text.pdf.qrcode.ByteArray;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class ReportController {
	@Autowired
	private ReportService reportService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private EmailUtils emailUtils;
	@Autowired
	private ProductService productService;

	@ModelAttribute
	public void getLoggedInUser(Principal p, Model m) {
		m.addAttribute("totalProducts", productService.getNumberOfProducts());
		m.addAttribute("deliveredProducts", orderService.findTotalDeliveredProducts());
		m.addAttribute("NumberOfCustomers", customerService.findNumberOfCustomers());
		m.addAttribute("totalSales", orderService.findTotalOrders());
		String emailString = p.getName();
		Customer customer = customerService.findByEmail(emailString);
		m.addAttribute("loggedUser", customer);
	}

	@GetMapping("/orderReport")
	public String chooseOrderDateForReport() {

		return "admin/orderdate";
	}

	@PostMapping("/generateOrderReport")
	public void generateOrdersToCsv(@RequestParam String startdatetime, @RequestParam String enddatetime,
			HttpServletResponse response) throws IOException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
		LocalDateTime start = LocalDateTime.parse(startdatetime, formatter);
		LocalDateTime end = LocalDateTime.parse(enddatetime, formatter);
		reportService.generateOrdersToCsv(start, end, response);

	}

	@GetMapping("/generateBill")
	public String generateBill(Model m) {
		List<Order> orders = this.orderService.showVerifiedOrders();
		m.addAttribute("orders", orders);
		m.addAttribute("forWhich", "admin");
		return "/admin/generatebill";
	}

//	@PostMapping("/downloadBill/{orderId}")
//	public String downloadBill(@RequestParam String invoice, @PathVariable int orderId, HttpServletResponse response)
//			throws Exception {
//		this.reportService.generateBill(orderId, response, invoice);
//		return "redirect:/admin/generateBill";
//	}

	@GetMapping("sendBill")
	public String sendBill(@RequestParam int orderId, @RequestParam int customerId, HttpServletRequest request,
			HttpSession session, HttpServletResponse response) throws Exception {

		Customer customer = this.customerService.findCustomerById(customerId);

		this.reportService.generateBill(orderId, BillConstant.getPdfPath(orderId));

		this.emailUtils.sendEmail(BillConstant.SUBJECT, BillConstant.getURL(request), customer, BillConstant.CONTENT,
				false, true, BillConstant.getPdfPath(orderId));
		session.setAttribute("success", "Bill is sent to the customer mail!");
		return "redirect:/admin/vieworderdetails/" + orderId;
	}
}
