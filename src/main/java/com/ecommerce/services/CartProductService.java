package com.ecommerce.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecommerce.models.Cart;
import com.ecommerce.models.CartProduct;

public interface CartProductService {
 void addToCart(int cid,int pid);
 CartProduct findById(int pid);
}
