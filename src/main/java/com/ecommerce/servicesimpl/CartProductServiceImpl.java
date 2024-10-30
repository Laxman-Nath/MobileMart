package com.ecommerce.servicesimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.dao.CartProductDao;
import com.ecommerce.models.CartProduct;
import com.ecommerce.services.CartProductService;
@Service
public class CartProductServiceImpl implements CartProductService{

	@Autowired
	private CartProductDao cartProductDao;
	@Override
	public void addToCart(int cid, int pid) {
		
		
	}

	@Override
	public CartProduct findById(int pid) {
		
		return this.cartProductDao.findById(pid).get();
	}

}
