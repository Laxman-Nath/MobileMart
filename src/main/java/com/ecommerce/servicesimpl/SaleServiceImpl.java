package com.ecommerce.servicesimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.dao.SaleDao;
import com.ecommerce.models.Sale;
import com.ecommerce.services.SaleService;

@Service
public class SaleServiceImpl implements SaleService {

    @Autowired
    private SaleDao saleDao;

    @Override
    public Sale addProductToSale(Sale sale) {
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
