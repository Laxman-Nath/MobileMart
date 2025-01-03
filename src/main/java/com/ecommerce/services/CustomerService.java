package com.ecommerce.services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.models.Customer;

import jakarta.servlet.http.HttpServletRequest;

public interface CustomerService {
	Customer addCustomer(Customer customer, MultipartFile image) throws IOException;

	Customer findByEmail(String email);

	List<Customer> findAllCustomers(String role);

	Customer findCustomerById(int id);

	boolean updateCustomerStatus(int id);

	void increaseFailedAttempt(Customer customer);

	void resetFailedAttempt(Customer customer);

	void lockAccount(Customer customer);

	boolean isLockedTimeExpired(Customer customer);

	void unlockAccount(Customer customer);

	Customer updateCustomer(Customer customer, MultipartFile image) throws IOException;

	boolean isPasswordMatches(Customer customer);

	boolean changePassword(int customerId, String password, String newPassword);

	List<Customer> getAllLockedCustomers();

	Long findNumberOfCustomers();

	Customer processForgotPassword(String email, HttpServletRequest request);
	
	Customer saveResetPassword(int id,String password,String cPassword);
	
	Customer addCustomer(Customer customer);

}
