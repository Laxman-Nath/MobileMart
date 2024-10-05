package com.ecommerce.services;

import java.util.List;

import com.ecommerce.models.Customer;

public interface CustomerService {
	Customer addCustomer(Customer customer);

	Customer findByEmail(String email);

	List<Customer> findAllCustomers(String role);

	Customer findCustomerById(int id);

	boolean updateCustomerStatus(int id);

	void increaseFailedAttempt(Customer customer);

	void resetFailedAttempt(Customer customer);

	void lockAccount(Customer customer);

	boolean isLockedTimeExpired(Customer customer);

	void unlockAccount(Customer customer);

	Customer updateCustomer(Customer customer);
	
	boolean isPasswordMatches(Customer customer);

	boolean changePassword(int customerId, String password, String newPassword);
	

}
