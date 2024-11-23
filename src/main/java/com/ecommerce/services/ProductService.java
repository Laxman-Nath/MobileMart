package com.ecommerce.services;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.ecommerce.models.Product;

public interface ProductService {
  Product addProduct(Product product);
  Boolean existByName(String name);
  Boolean deleteProductById(int id);
  Page<Product> getAllProducts(String category,int page,int pageNo);
  Product findByProductId(int id);
  Page<Product> searchProduct(String keyword,int page,int size);
  List<Product> getLatestProduct();
  Long getNumberOfProducts();
}
