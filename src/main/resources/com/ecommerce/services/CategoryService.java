package com.ecommerce.services;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.dao.CategoryDao;
import com.ecommerce.models.Category;

public interface CategoryService {

	Category addCategory(Category category, MultipartFile image, Boolean status) throws IOException;

	List<Category> listAllCategory();

	boolean existsByName(String name);

	boolean deleteCategoryById(int id);

	Category findByCategoryId(int id);

	List<Category> findByIsActiveTrue();

	Category updateCategory(Category category, MultipartFile file, Boolean status) throws IOException;

}
