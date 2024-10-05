package com.ecommerce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ecommerce.dao.CustomerDao;
import com.ecommerce.models.Customer;

public class CustomCustomerDetailsService implements UserDetailsService {

	@Autowired
	private CustomerDao customerDao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Customer customer=this.customerDao.findByEmail(username);
		if(customer == null) {
			throw new UsernameNotFoundException("User not found");
		}
		return new CustomCustomer(customer);
	}

}
