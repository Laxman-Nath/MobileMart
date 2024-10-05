package com.ecommerce.config;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ecommerce.models.Customer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomCustomer implements UserDetails {

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return this.customer.isAccountNonLocked();
	}

	private Customer customer;
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		SimpleGrantedAuthority authority=new SimpleGrantedAuthority(customer.getRole());
		return Arrays.asList(authority);
	}

	@Override
	public String getPassword() {
		
		return customer.getPassword();
	}

	@Override
	public String getUsername() {
		
		return customer.getEmail();
	}

	@Override
	public boolean isEnabled() {
	   return this.customer.isEnable();
		
	}
	

}
