package com.ecommerce.servicesimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.dao.OrderItemDao;
import com.ecommerce.models.OrderItem;
import com.ecommerce.services.OrderItemService;
@Service
public class OrderItemServiceImpl implements OrderItemService {
	@Autowired
	private OrderItemDao orderItemDao;

	@Override
	public OrderItem findById(int id) {
		
		return this.orderItemDao.findById(id).get();
	}

}
