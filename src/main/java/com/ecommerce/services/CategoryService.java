package com.ecommerce.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.ecommerce.dao.CategoryDao;
import com.ecommerce.models.Category;

public interface CategoryService {

	 Category addCategory(Category category);

	 List<Category> listAllCategory();
	 boolean existsByName(String name);
	
	 boolean deleteCategoryById(int id);
	 
	 Category findByCategoryId(int id);
	 List<Category> findByIsActiveTrue();
	 

}
