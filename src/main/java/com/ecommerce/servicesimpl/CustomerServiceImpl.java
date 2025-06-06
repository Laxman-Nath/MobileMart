package com.ecommerce.servicesimpl;

import java.io.IOException;
import java.util.Date;
import org.slf4j.Logger;
import java.util.List;
import java.util.UUID;

import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;
import org.hibernate.bytecode.internal.bytebuddy.PrivateAccessorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.commonutils.EmailUtils;
import com.ecommerce.commonutils.FileUploadUtils;
import com.ecommerce.dao.CustomerDao;
import com.ecommerce.models.Customer;
import com.ecommerce.services.CustomerService;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;

@Service
public class CustomerServiceImpl implements CustomerService {

	Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
	@Autowired
	private CustomerDao cd;
	private long lock_duration = 1 * 60 * 1000;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private FileUploadUtils fileUpload;
	@Autowired
	private EmailUtils emailUtils;

	@Override
	@Transactional
	public Customer addCustomer(Customer customer, MultipartFile image) throws IOException {

		if (customer != null && image != null) {
			customer.setFile(image.getOriginalFilename());
			if (fileUpload.uploadFile(image, "src\\main\\resources\\static\\img\\profile_img\\")) {
				customer.setEnable(true);
				customer.setAccountNonLocked(true);
				customer.setPassword(passwordEncoder.encode(customer.getPassword()));
				customer.setPassword(passwordEncoder.encode(customer.getCpassword()));
				customer.setProvider("self");

				Customer registeredCustomer = cd.save(customer);
				if (registeredCustomer != null) {
					return registeredCustomer;
				} else {
					return null;
				}
			} else {
				return null;
			}
		}

		return null;
	}

	@Override
	public Customer findByEmail(String email) {

		return this.cd.findByEmail(email);
	}

	@Override
	public List<Customer> findAllCustomers(String role) {
		return this.cd.findCustomersByRole(role);
	}

	@Override
	public Customer findCustomerById(int id) {
		// TODO Auto-generated method stub
		return this.cd.findById(id).get();
	}

	@Override
	public void increaseFailedAttempt(Customer customer) {
		Customer c = findCustomerById(customer.getId());
		if (c != null) {
			c.setFailedAttempt(c.getFailedAttempt() + 1);
			this.cd.save(c);
		}

	}

	@Override
	public void resetFailedAttempt(Customer c) {

		c.setFailedAttempt(0);
		this.cd.save(c);

	}

	@Override
	public void lockAccount(Customer c) {

		c.setAccountNonLocked(false);
		c.setLockedTime(new Date());
		this.cd.save(c);

	}

	public void unlockAccount(Customer customer) {
		customer.setAccountNonLocked(true);
		customer.setLockedTime(null);
		// Save the updated customer state
	}

	@Override
	public boolean isLockedTimeExpired(Customer customer) {
		long lockedTime = customer.getLockedTime().getTime();
		long currentTimeInMillies = System.currentTimeMillis();
		if (lockedTime + lock_duration < currentTimeInMillies) {
			unlockAccount(customer);
			resetFailedAttempt(customer);
			this.cd.save(customer);
			return true;
		}
		return false;
	}

	@Override
	public boolean updateCustomerStatus(int id) {
		System.out.println("Id is +" + id);
		Customer customer = this.cd.findById(id).get();
		if (customer != null) {
			if (customer.isEnable()) {
				customer.setEnable(false);

			} else {
				customer.setEnable(true);
			}
			this.cd.save(customer);
			return true;
		}
		return false;
	}

	@Override
	public Customer updateCustomer(Customer customer, MultipartFile image) throws IOException {

		if (image != null && !image.isEmpty()) {

			if (fileUpload.uploadFile(image, "src\\main\\resources\\static\\img\\profile_img\\")) {
				customer.setFile(image.getOriginalFilename());

			} else {
				return null;
			}

		}

		customer.setEnable(true);
		customer.setAccountNonLocked(true);
//			customer.setPassword(passwordEncoder.encode(customer.getPassword()));
//		    customer.setPassword(customer.getPassword());
		return this.cd.save(customer);
	}

	@Override
	public boolean changePassword(int customerId, String password, String newPassword) {
		Customer customer = this.cd.findById(customerId).get();
		if (customer != null) {

			if (passwordEncoder.matches(password, customer.getPassword())) {
				String hashedPasswordString = passwordEncoder.encode(newPassword);
				customer.setPassword(hashedPasswordString);
				customer.setCpassword(hashedPasswordString);
				cd.save(customer);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isPasswordMatches(Customer customer) {
		Customer c = this.cd.findByEmail(customer.getEmail());
		if (customer.getPassword().equals(c.getPassword())) {
			return true;
		}
		return false;
	}

	@Override
	public List<Customer> getAllLockedCustomers() {

		return this.cd.findCustomerByIsAccountNonLockedIsFalse();
	}

	@Override
	public Long findNumberOfCustomers() {

		return this.cd.findTotalNumberOfCustomers();
	}

	@Override
	public Customer processForgotPassword(String email, HttpServletRequest request) {

		Customer customer = this.cd.findByEmail(email);
		if (customer != null) {

			String content = "Dear [[name]],please click on the link below to reset your password."
					+ "<h3><a href=\"[[path]]\" target=\"_self\">RESET</a></h3> Thank you!";

			String codeString = UUID.randomUUID().toString();
			customer.setCode(codeString);
			this.cd.save(customer);
			String url = request.getRequestURL().toString();
			url = url.replace(request.getRequestURI(), "");
			String subject = "Reset password";
			this.emailUtils.sendEmail(subject, url, customer, content, false, false, null);
			return customer;
		}
		return customer;
	}

	@Override
	public Customer saveResetPassword(int id, String password, String cPassword) {
		Customer customer = this.cd.findById(id).get();
		if (customer != null) {
			if (password.equals(cPassword)) {
				customer.setPassword(passwordEncoder.encode(password));
				customer.setCpassword(passwordEncoder.encode(cPassword));
				return this.cd.save(customer);
			}
			return null;
		}
		return null;
	}

	@Override
	public Customer addCustomer(Customer customer) {

		// customer.setProvider("self");
		return this.cd.save(customer);
	}

	public Customer findByEmailAndProviderNotGoogle(String email) {
		return this.cd.findByEmailAndProviderNotGoogle(email);
	}

}
