package com.ecommerce.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecommerce.models.Product;

@Repository
public interface ProductDao extends JpaRepository<Product, Integer> {
	Boolean existsByName(String name);

	Page<Product> findByCategory(String category,Pageable pageable);

	@Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.dimension) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.weight) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.build) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.sim) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.color) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.type) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.size) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.resolution) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.os) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.chipSet) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.cpu) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.gpu) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.cardSlot) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.internal) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.bFeatures) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.bVideo) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.fFeatures) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.fVideo) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.loudSpeaker) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.sFeatures) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.bType) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.charging) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.cFeatures) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.security) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(p.category) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
	Set<Product> searchProducts(@Param("searchTerm") String keyword);

	@Query("SELECT p FROM Product p ORDER BY  p.id DESC")
	List<Product> getLatestProducts(Pageable pageable);
	
	

}
