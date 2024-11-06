package com.ecommerce.services;

import java.util.List;

import com.ecommerce.models.Sale;

public interface SaleService {
    Sale addProductToSale(Sale sale,int productId);
    List<Sale> getAllProductsOnSale();
    boolean  getProductOnSaleByProductId(int id);
    Sale getByProductId(int id);
    void deleteByProductId(int id);
}
