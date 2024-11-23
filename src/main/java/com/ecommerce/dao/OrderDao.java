package com.ecommerce.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ecommerce.models.Customer;
import com.ecommerce.models.Order;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface OrderDao extends JpaRepository<Order, Integer> {
	@Query("SELECT o FROM Order o  WHERE o.customer=?1 AND o.isVerified=false ORDER BY o.placedDate desc ")
	List<Order> findByCustomerAndIsVerifiedFalse(Customer customer);

	List<Order> findByIsVerifiedTrue();

	List<Order> findByIsVerifiedFalse();

	List<Order> findByPlacedDateBetween(LocalDateTime startDateTime, LocalDateTime enDateTime);

	@Query("SELECT o FROM Order o  WHERE o.customer=?1 AND (o.status='Submitted')")
	List<Order> findByCustomerAndStatus(Customer customer);
	
	@Query("SELECT COUNT(*)  FROM Order o where o.status='Delivered'")
	Long findTotalDeliveredProducts();
}
