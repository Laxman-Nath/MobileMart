package com.ecommerce.config;

import java.io.IOException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ecommerce.dao.CustomerDao;
import com.ecommerce.models.Customer;

import com.ecommerce.servicesimpl.CustomerServiceImpl;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	Logger logger = LoggerFactory.getLogger(CustomFailureHandler.class);
	@Autowired
	private CustomerServiceImpl customerServiceImpl;
	
	@Autowired
	private CustomerDao customerDao;
	@Autowired
	private CustomSuccessHandler customSuccessHandler;



	public Authentication createAuthentication(CustomCustomer customer) {
		return new UsernamePasswordAuthenticationToken(customer, customer.getPassword(), customer.getAuthorities());
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		Customer customer = customerServiceImpl.findByEmail(request.getParameter("username"));
		System.out.println(request.getParameter("username"));

		if (customer != null) {
			if (customer.isEnable()) {
				if (!customer.isAccountNonLocked() && customerServiceImpl.isLockedTimeExpired(customer)) {
					customerServiceImpl.unlockAccount(customer);
					customerServiceImpl.resetFailedAttempt(customer);
					exception = new LockedException("Your account has been unlocked!" + "please try logging in");

//					if (customerServiceImpl.isPasswordMatches(customer)) {
//						logger.info("Password matched!");
//
//						CustomCustomer customCustomer = new CustomCustomer(customer);
//						Authentication authentication = createAuthentication(customCustomer);
//						SecurityContextHolder.getContext().setAuthentication(authentication);
//						String redirect = getRedirect(authentication);
//						response.sendRedirect(redirect);

//	             else {
//						customerServiceImpl.increaseFailedAttempt(customer);
//						System.out.println("Failed attempt is :" + customer.getFailedAttempt());
//						exception = new LockedException(
//								"You have only " + (2 - customer.getFailedAttempt() + 1) + " attempts left!");
//						return;
//					}
				} else if (customer.isAccountNonLocked()) {
					if (customer.getFailedAttempt() < 3) {
						customerServiceImpl.increaseFailedAttempt(customer);
						System.out.println("Failed attempt is :" + customer.getFailedAttempt());
						exception = new LockedException(
								"You have only " + (2 - customer.getFailedAttempt() + 1) + " attempts left!");
					} else {
						System.out.println("Inside");
						customerServiceImpl.lockAccount(customer);

						exception = new LockedException("Your account has been locked.Try again after one minute !");
					}
				} else {
					exception = new LockedException("Your account has been locked already!");
				}

			} else {
				exception = new LockedException("Your account has been locked.Try again after one minute!");
			}
		} else {
			exception = new LockedException("Customer with this email doesn't exist!");
		}

		super.setDefaultFailureUrl("/login?error");
		super.onAuthenticationFailure(request, response, exception);
	}

//	public String getRedirect(Authentication authentication) {
//		if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
//			return "/user/";
//		} else {
//			return "/admin/";
//		}
//
//	}

}
