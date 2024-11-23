package com.ecommerce.servicesimpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	public Page<Product> getAllProducts(String category,int page,int pageNo) {
		Page<Product> products ;
		Pageable pageable=PageRequest.of(page, pageNo);
		if (category == null || category.equals("")) {
			
			products= this.pd.findAll(pageable);
		} else {
			products = this.pd.findByCategory(category,pageable);
		}
		return products;
	}

	@Override
	public Product findByProductId(int id) {

		return this.pd.findById(id).orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<Product> searchProduct(String keyword,int page,int size) {
//	   System.out.println(keyword);
		String searchedKeywordString = keyword.trim();
		Set<Product> productsSet = new HashSet<>();
		String[] keywords = searchedKeywordString.split("\\s+");
		for (String str : keywords) {
			productsSet.addAll(this.pd.searchProducts(str));
		}
		
		List<Product> productsList=new ArrayList<>(productsSet);
		int totalProducts=productsList.size();
		
		int startIndex=Math.min(page*size, totalProducts);
		int endIndex=Math.min(startIndex+size,totalProducts);
		
		List<Product> paginatedList=productsList.subList(startIndex, endIndex);

		return new PageImpl(paginatedList,PageRequest.of(page, size),totalProducts);
	}

	@Override
	public List<Product> getLatestProduct() {
		List<Product> products = this.pd.getLatestProducts(PageRequest.of(0, 10));

		return products;
	}

	@Override
	public Long getNumberOfProducts() {
	
		return this.pd.count();
	}

}
