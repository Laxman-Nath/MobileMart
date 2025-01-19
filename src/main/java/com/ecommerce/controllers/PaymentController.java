package com.ecommerce.controllers;

import java.util.List;

import java.security.Principal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ecommerce.commonutils.EmailUtils;
import com.ecommerce.commonutils.SignatureUtils;
import com.ecommerce.commonutils.Transaction;

import com.ecommerce.constants.BillConstant;
import com.ecommerce.constants.PaymentConstant;
import com.ecommerce.models.Category;
import com.ecommerce.models.Customer;
import com.ecommerce.models.Order;

import com.ecommerce.services.CategoryService;
import com.ecommerce.services.CustomerService;
import com.ecommerce.services.OrderService;
import com.ecommerce.services.ReportService;

import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/payment")
public class PaymentController {
	@Autowired
	private OrderService orderService;
	@Autowired
	private Transaction transaction;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private ReportService reportService;

	@ModelAttribute
	public void getLoggedInUser(Principal p, Model m) {
		List<Category> categories = categoryService.findByIsActiveTrue();

		m.addAttribute("categories", categories);
		String emailString = p.getName();
		Customer customer = customerService.findByEmail(emailString);
		if (customerService.findByEmailAndProviderNotGoogle(emailString) != null) {
			m.addAttribute("isLoggedInByGoogle", false);
		} else {
			m.addAttribute("isLoggedInByGoogle", true);
		}
		List<Order> orders = this.orderService.findByCustomer(customer);
		m.addAttribute("orders", orders);
		m.addAttribute("loggedUser", customer);
	}

	@GetMapping("/paymentform")
	public String showPaymentForm(@RequestParam int orderId, Model m) {
		log.info("Inside payment form");
		log.info("Order id {}", orderId);

		m.addAttribute("order", orderService.findById(orderId));
		return "payment/esewaform";
	}

	@PostMapping("/pay")
	public ModelAndView initiatePayment(@RequestParam("amount") Double amount,
			@RequestParam("tax_amount") Double taxAmount,

			@RequestParam String orderId, @RequestParam("shipping_cost") Double shippingCost) {

		log.info("Inside pat order id {}", orderId);
		String totalAmount = String.valueOf(amount + taxAmount + shippingCost);

		String transactionUUID = transaction.generateTransactionUUID();

		String signatureData = "total_amount=" + totalAmount + "&transaction_uuid=" + transactionUUID + "&product_code="
				+ orderId;
		String signature = SignatureUtils.generateSignature(signatureData, PaymentConstant.SECRET_KEY);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:" + PaymentConstant.ESEWA_URL);

		modelAndView.addObject("amt", amount);
		modelAndView.addObject("pid", orderId);
		modelAndView.addObject("txAmt", taxAmount);
		modelAndView.addObject("tAmt", totalAmount);
		modelAndView.addObject("scd", "EPAYTEST");
		modelAndView.addObject("transaction_uuid", transactionUUID);
		modelAndView.addObject("product_code", orderId);
		modelAndView.addObject("psc", "0");
		modelAndView.addObject("pdc", shippingCost);
		modelAndView.addObject("su", PaymentConstant.SUCCESS_URL);
		modelAndView.addObject("fu", PaymentConstant.FAILURE_URL);
		modelAndView.addObject("signed_field_names", "total_amount,transaction_uuid,product_code");
		modelAndView.addObject("signature", signature);

		return modelAndView;
	}

//	https://localhost:8080/payment/paymentsuccess?oid=45&amt=10910.0&refId=00092XR
	@GetMapping("paymentsuccess")
	public String showpaymentsuccess(@RequestParam("amt") Double amount, @RequestParam("refId") String refId,

			@RequestParam("oid") int orderId, Model model, HttpServletRequest request) throws Exception {

		log.info("Inside success: amount,orderId,referenceId {} {} {}", amount, orderId, refId);

		Order order = this.orderService.findById(orderId);
		Customer customer = this.customerService.findCustomerById(order.getCustomer().getId());
		model.addAttribute("order", order);
		this.reportService.generateBill(orderId, BillConstant.getPdfPath(orderId));
		order.setIsPaid(true);
		;

		orderService.setVerified(order, request, customer);

		model.addAttribute("date", LocalDate.now().getYear());

		return "payment/paymentsuccess";
	}

	@GetMapping("/paymentfailure")
	public String showpaymentfailure(@RequestParam("amt") Double amount, @RequestParam("refId") String refId,

			@RequestParam("oid") int orderId, Model model) {
		Order order = this.orderService.findById(orderId);

		model.addAttribute("order", order);

		model.addAttribute("date", LocalDate.now().getYear());

		return "payment/paymentfailure";
	}
}
