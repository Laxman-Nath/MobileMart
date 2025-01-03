package com.ecommerce.services;

import com.ecommerce.models.OrderItem;

public interface OrderItemService {
OrderItem findById(int id);
 OrderItem saveOrderItem(OrderItem orderItem);
 void deleteById(int id);
}
