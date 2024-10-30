package com.ecommerce.commonutils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ecommerce.dao.CustomerDao;
import com.ecommerce.models.Customer;
@Component
public class VerifyCodeUtils {
	@Autowired
	private CustomerDao customerDao;
	public boolean verifyCode(String code) {

		if (this.customerDao.existsByCode(code)) {
			return true;
		}
		return false;
	}


	public Customer findByCode(String code) {

		return this.customerDao.findByCode(code);
	}
}
