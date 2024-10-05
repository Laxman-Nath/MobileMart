package com.ecommerce.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.models.Sale;
@Repository
public interface SaleDao extends JpaRepository<Sale,Integer> {
  boolean existsByProductId(int productId);
  void deleteByProductId(int productId);
  Sale findByProductId(int productId);
}
