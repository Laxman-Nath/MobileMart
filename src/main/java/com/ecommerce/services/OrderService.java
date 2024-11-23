package com.ecommerce.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.ecommerce.models.Customer;
import com.ecommerce.models.Order;

public interface OrderService {
	Order placeOrder(int cartId, int userId, Order order);

	Order findByCustomerAndIsVerifiedFalse(Customer customer);

	Order saveOrder(Order order);

	List<Order> showVerifiedOrders();

	List<Order> showUnverfiedOrders();

	Order findById(int id);

	boolean deleteProductFromOrder(int orderItemId);

	boolean increaseProductFromOrder(int orderItemId);

	boolean decreaseProductFromOrder(int orderItemId);
	
	public Order updateOrder(Order oldOrder,Order newOrder);
	
	
	List<Order> findByCustomer(Customer customer);
	
	Order cancelOrder(int orderId);
	
	Long findTotalDeliveredProducts();
	
	Long findTotalOrders();
}
