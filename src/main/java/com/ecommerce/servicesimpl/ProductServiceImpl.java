package com.ecommerce.servicesimpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.ecommerce.dao.ProductDao;
import com.ecommerce.models.Product;
import com.ecommerce.services.ProductService;

import lombok.Setter;

@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	private ProductDao pd;

	public Product addProduct(Product product) {
		return this.pd.save(product);

	}

	@Override
	public Boolean existByName(String name) {
		// TODO Auto-generated method stub
		return this.pd.existsByName(name);
	}

	@Override
	public Boolean deleteProductById(int id) {
		if (this.pd.existsById(id)) {
			this.pd.deleteById(id);
			return true;
		}
		return false;
	}

	@Override
	public List<Product> getAllProducts(String category) {
		List<Product> products = new ArrayList<>();
		if (category == null || category.equals("")) {
			products = this.pd.findAll();
		} else {
			products = this.pd.findByCategory(category);
		}
		return products;
	}

	@Override
	public Product findByProductId(int id) {

		return this.pd.findById(id).orElse(null);
	}

	@Override
	public Set<Product> searchProduct(String keyword) {
//	   System.out.println(keyword);
		String searchedKeywordString = keyword.trim();
		Set<Product> products = new HashSet<>();
		String[] keywords = searchedKeywordString.split("\\s+");
		for (String str : keywords) {
			products.addAll(this.pd.searchProducts(str));
		}

		return products;
	}

	@Override
	public List<Product> getLatestProduct() {
		List<Product> products = this.pd.getLatestProducts(PageRequest.of(0, 10));

		return products;
	}

}
