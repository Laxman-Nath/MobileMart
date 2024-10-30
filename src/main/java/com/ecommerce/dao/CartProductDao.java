package com.ecommerce.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.models.CartProduct;

public interface CartProductDao extends JpaRepository<CartProduct, Integer> {
	CartProduct findByProductId(int pid);
}
