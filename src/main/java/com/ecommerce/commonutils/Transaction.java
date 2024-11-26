package com.ecommerce.commonutils;

import java.util.UUID;

import org.springframework.stereotype.Service;


@Service
public class Transaction {
         
	public String generateTransactionUUID() {
		return UUID.randomUUID().toString();
	}
}
