package com.ecommerce.schedulers;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ecommerce.dao.SaleDao;
import com.ecommerce.models.Sale;



@Component
public class RemoveFromScheduler {
	@Autowired
	private SaleDao saleDao;
	
	@Scheduled(fixedRate = 86400*1000)
	public void removeProductFromSale() {
	  List<Sale> sales=saleDao.findAll();
	  for(Sale s:sales) {
		  if(LocalDate.now().isAfter(s.getSaleEndDate())) {
			  saleDao.delete(s);
			  saleDao.save(s);
		  }
	  }
	 
	}
}
