package com.ecommerce.config;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import com.ecommerce.commonutils.DownloadImageUtils;
import com.ecommerce.dao.CustomerDao;
import com.ecommerce.models.Customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

	private static final Logger log = LoggerFactory.getLogger(OAuthSuccessHandler.class);

	@Autowired
	private CustomerDao customerDao;
	@Autowired
	private DownloadImageUtils downloadImageUtils;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		var oauth2User = (DefaultOAuth2User) authentication.getPrincipal();
		String email = oauth2User.getAttribute("email").toString();
		log.info("OAuth authentication successful for email: {}", email);

		Customer customer = customerDao.findByEmail(email);
		UsernamePasswordAuthenticationToken token;

		if (customer == null) {
			customer = createNewCustomer(oauth2User);
			token = new UsernamePasswordAuthenticationToken(customer.getEmail(), null,
					Arrays.asList(new SimpleGrantedAuthority(customer.getRole())));
		} else {
			token = new UsernamePasswordAuthenticationToken(customer.getEmail(), null,
					Arrays.asList(new SimpleGrantedAuthority(customer.getRole())));
		}

		SecurityContextHolder.getContext().setAuthentication(token);
		new DefaultRedirectStrategy().sendRedirect(request, response, "/user/");
	}

	private Customer createNewCustomer(DefaultOAuth2User oauth2User) throws IOException {
		Customer customer = new Customer();
		customer.setName(oauth2User.getAttribute("name").toString());
		customer.setEmail(oauth2User.getAttribute("email").toString());

		customer.setRole("ROLE_USER");
		customer.setAccountNonLocked(true);
		customer.setEnable(true);
		customer.setProvider("Google");
		String fileName = downloadImageUtils.downloadImage(oauth2User.getAttribute("picture").toString());
		customer.setFile(fileName);
		return customerDao.save(customer);
	}
}
