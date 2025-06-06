package com.ecommerce.commonutils;

import org.springframework.stereotype.Component;

import com.ecommerce.models.Product;
@Component
public class DiscountUtils {
	public Double calculateDiscountedPrice(Product product) {

		double discountedPrice = product.getPrice() - ((product.getPrice() * product.getDiscountPercent()) / 100);

		return discountedPrice;
	}
}
