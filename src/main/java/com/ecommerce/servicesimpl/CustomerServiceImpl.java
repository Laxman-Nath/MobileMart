package com.ecommerce.servicesimpl;

import java.util.Date;
import org.slf4j.Logger;
import java.util.List;

import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;
import org.hibernate.bytecode.internal.bytebuddy.PrivateAccessorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.dao.CustomerDao;
import com.ecommerce.models.Customer;
import com.ecommerce.services.CustomerService;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

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

	@Override
	@Transactional
	public Customer addCustomer(Customer customer) {

		customer.setEnable(true);
		customer.setAccountNonLocked(true);
		customer.setPassword(passwordEncoder.encode(customer.getPassword()));
		return this.cd.save(customer);
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
	public Customer updateCustomer(Customer customer) {
		if (customer.getRole().equals("ROLE_ADMIN")) {
			customer.setRole("ROLE_ADMIN");
		} else if (customer.getRole().equals("ROLE_USER")) {
			customer.setRole("ROLE_USER");
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
				customer.setPassword(passwordEncoder.encode(newPassword));
				customer.setCpassword(newPassword);
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

}
