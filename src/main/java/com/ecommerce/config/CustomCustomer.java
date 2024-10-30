package com.ecommerce.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.ecommerce.models.Customer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomCustomer implements UserDetails {

    private Customer customer;

    @Override
    public boolean isAccountNonLocked() {
        return this.customer.isAccountNonLocked();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(customer.getRole());
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
        return this.customer.isEnable(); // Ensure you have a proper method in Customer
    }

  
}
