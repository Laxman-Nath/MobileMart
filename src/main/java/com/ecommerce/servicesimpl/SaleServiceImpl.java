package com.ecommerce.servicesimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.dao.ProductDao;
import com.ecommerce.dao.SaleDao;
import com.ecommerce.models.Product;
import com.ecommerce.models.Sale;
import com.ecommerce.services.ProductService;
import com.ecommerce.services.SaleService;
import java.util.Iterator;

@Service
public class SaleServiceImpl implements SaleService {

    @Autowired
    private SaleDao saleDao;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductDao productDao;

    @Override
    public Sale addProductToSale(Sale sale,int productId) {
    	Product product=this.productService.findByProductId(productId);
    	if(!(product.getDiscountedPrice()==sale.getDiscountedPrice() && product.getDiscountPercent()==sale.getDiscountPercent())){
    		double discountedPrice = sale.getPrice() - ((sale.getPrice() * sale.getDiscountPercent()) / 100);
    		sale.setDiscountedPrice(discountedPrice);
    	}
    	sale.setProduct(product);
    
//    	productDao.save(product);
        return saleDao.save(sale);
    }

    @Override
    public boolean getProductOnSaleByProductId(int id) {
        return saleDao.existsByProductId(id);
    }

    @Override
    public List<Sale> getAllProductsOnSale() {
        return saleDao.findAll();
    }

    @Override
    public Sale getByProductId(int id) {
        return saleDao.findByProductId(id);
    }

    @Override
    @Transactional
    public void deleteByProductId(int id) {
        if (saleDao.existsByProductId(id)) {
          saleDao.deleteByProductId(id);
         
           
        }
    }
}
