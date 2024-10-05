package com.ecommerce.servicesimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.dao.CategoryDao;
import com.ecommerce.models.Category;
import com.ecommerce.services.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {
	@Autowired
private CategoryDao cd;

@Override
public Category addCategory(Category category) {
	
	return this.cd.save(category);
}

@Override
public List<Category> listAllCategory() {
	
	return this.cd.findAll();
}

@Override
public boolean existsByName(String name) {
	return this.cd.existsByName(name);
}

@Override
public boolean deleteCategoryById(int id) {
	if(this.cd.existsById(id)){
		this.cd.deleteById(id);
		return true;
	}
	return false;
}

@Override
public Category findByCategoryId(int id) {
	Optional<Category> category=this.cd.findById(id);
	
	return category.orElse(null);
}

@Override
public List<Category> findByIsActiveTrue() {
	return this.cd.findByIsActiveTrue();

}

}
