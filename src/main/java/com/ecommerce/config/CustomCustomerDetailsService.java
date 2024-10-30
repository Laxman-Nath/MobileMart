package com.ecommerce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ecommerce.dao.CustomerDao;
import com.ecommerce.models.Customer;

import lombok.extern.slf4j.Slf4j;

@Service
public class CustomCustomerDetailsService implements UserDetailsService {

    @Autowired
    private CustomerDao customerDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = this.customerDao.findByEmail(username);
        
        if (customer == null) {
            System.out.println("User not found for email: " + username);
            throw new UsernameNotFoundException("User not found");
        }

        System.out.println("User found: " + customer.getEmail());
        return new CustomCustomer(customer);
    }
}
