package com.ecommerce.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.models.Product;
import com.ecommerce.models.Review;

@Repository
public interface ReviewDao extends JpaRepository<Review,Integer> {
  List<Review> findByProduct(Product product);
}
