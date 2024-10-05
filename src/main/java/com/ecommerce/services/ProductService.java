package com.ecommerce.services;

import java.util.List;
import java.util.Set;

import com.ecommerce.models.Product;

public interface ProductService {
  Product addProduct(Product product);
  Boolean existByName(String name);
  Boolean deleteProductById(int id);
  List<Product> getAllProducts(String category);
  Product findByProductId(int id);
  Set<Product> searchProduct(String keyword);
  List<Product> getLatestProduct();
}
