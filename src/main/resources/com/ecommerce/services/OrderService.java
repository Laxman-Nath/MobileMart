package com.ecommerce.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;

import com.ecommerce.models.Customer;
import com.ecommerce.models.Order;

import jakarta.servlet.http.HttpServletRequest;

public interface OrderService {
	Order placeOrder(int cartId, int userId, Order order, String code);

	Order findByCustomerAndIsVerifiedFalse(Customer customer);

	Order saveOrder(Order order);

	Page<Order> showVerifiedOrders(int pageNo,int pageSize);

	Page<Order> showUnverfiedOrders(int pageNo,int pageSize);

	Order findById(int id);

	boolean deleteProductFromOrder(int orderItemId);

	boolean increaseProductFromOrder(int orderItemId);

	boolean decreaseProductFromOrder(int orderItemId);

	public Order updateOrder(Order oldOrder, Order newOrder);

	List<Order> findByCustomer(Customer customer);

	Order cancelOrder(int orderId);

	Long findTotalDeliveredProducts();

	Long findTotalOrders();

	Order setVerified(Order order, HttpServletRequest request, Customer customer);

	Page<Order> getAllSubmittedOrders(int pageNo,int pageSize);
	Page<Order> getAllShippedOrders(int pageNo,int pageSize);
	Page<Order> getAllDeliveredOrders(int pageNo,int pageSize);
}
