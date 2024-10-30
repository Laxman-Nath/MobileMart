package com.ecommerce.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.models.Order;
import com.ecommerce.models.OrderItem;
import com.ecommerce.models.Product;

@Repository
public interface OrderItemDao extends JpaRepository<OrderItem, Integer> {
//  OrderItemDao findByProductId(int productId);
}
