package com.ecommerce.services;


import com.ecommerce.models.CartProduct;

public interface CartProductService {
 void addToCart(int cid,int pid);
 CartProduct findById(int pid);
}