package com.ecommerce.services;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.models.Product;

public interface ProductService {
	Product addProduct(Product product,MultipartFile file) throws IOException;

	Boolean existByName(String name);

	Boolean deleteProductById(int id);

	Page<Product> getAllProducts(String category, int page, int pageNo);

	Product findByProductId(int id);

	Page<Product> searchProduct(String keyword, int page, int size);

	List<Product> getLatestProduct();

	Long getNumberOfProducts();

	Product editProduct(Product oldProduct, Product product, MultipartFile newfile) throws IOException;
}