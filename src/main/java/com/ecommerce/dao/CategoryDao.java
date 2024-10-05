package com.ecommerce.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.models.Category;

@Repository
public interface CategoryDao extends JpaRepository<Category,Integer>{
   boolean existsByName(String name);
   List<Category> findByIsActiveTrue();
   
}
