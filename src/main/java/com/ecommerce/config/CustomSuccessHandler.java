package com.ecommerce.config;

import java.io.IOException;

import org.hibernate.annotations.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.config.CustomRepositoryImplementationDetector;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.ecommerce.dao.CustomerDao;
import com.ecommerce.models.Customer;
import com.ecommerce.servicesimpl.CustomerServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {
	private static final Logger logger = LoggerFactory.getLogger(CustomSuccessHandler.class);
	@Autowired
	private CustomerServiceImpl customerServiceImpl;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		System.out.println("Email="+userDetails.getUsername());
		Customer customer = this.customerServiceImpl.findByEmailAndProviderNotGoogle(userDetails.getUsername());
//		request.getSession().invalidate();
		if (customer != null) {
			customerServiceImpl.resetFailedAttempt(customer);
			customerServiceImpl.unlockAccount(customer);
			logger.info("User {} logged in successfully.", userDetails.getUsername());
		}
		String redirectString = "";
		if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
			redirectString = "/admin/";
		} else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
			redirectString = "/user/";
		}
		response.sendRedirect(redirectString);
//		request.getRequestDispatcher(redirectString).forward(request, response);

	}

}
