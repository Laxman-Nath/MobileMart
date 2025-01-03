package com.ecommerce.servicesimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.dao.OrderItemDao;
import com.ecommerce.models.OrderItem;
import com.ecommerce.services.OrderItemService;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

@Service
public class OrderItemServiceImpl implements OrderItemService {
	@Autowired
	private OrderItemDao orderItemDao;

	@Override
	public OrderItem findById(int id) {

		return this.orderItemDao.findById(id).get();
	}

	@Override
	public OrderItem saveOrderItem(OrderItem orderItem) {
		return this.orderItemDao.save(orderItem);
	}

	@Override
	public void deleteById(int id) {
		this.orderItemDao.deleteById(id);
	}

}
