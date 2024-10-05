package com.ecommerce.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecommerce.models.Cart;

public interface CartDao extends JpaRepository<Cart,Integer>{
Cart findByCustomerId(int customerId);
Cart findByIdAndCustomerIdAndIsCheckedFalse(int cartId,int customerId);
Cart findByCustomerIdAndIsCheckedFalse(int customerId);

}
