package com.ecommerce.services;

import java.io.ByteArrayOutputStream;

import com.ecommerce.models.Customer;

public interface CommonService {
  void removeSessionAttribute();
  void sendEmail(String subject,String url,Customer customer,String content,boolean isOrder,boolean isBill,
		  String filePath);
  boolean verifyCode(String code);
  Customer findByCode(String code);
}
