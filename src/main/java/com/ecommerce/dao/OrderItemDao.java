package com.ecommerce.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.models.OrderItem;

@Repository
public interface OrderItemDao extends JpaRepository<OrderItem,Integer> {

}
