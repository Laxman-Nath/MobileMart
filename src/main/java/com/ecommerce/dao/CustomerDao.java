package com.ecommerce.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.models.Customer;

@Repository
public interface CustomerDao extends JpaRepository<Customer,Integer>{
Customer findByEmail(String email);
boolean existsByEmail(String email);
List<Customer> findCustomersByRole(String role);
boolean existsByCode(String code);
Customer findByCode(String code);
boolean existsByPassword(String password);
List<Customer> findCustomerByIsAccountNonLockedIsFalse();
}
