package com.ecommerce.services;

import com.ecommerce.models.Cart;
import com.ecommerce.models.Customer;
import com.ecommerce.models.Product;

public interface CartService {
	Cart saveCart(int cid,int pid);
	Cart createNewCart(Customer customer);
	Cart getCartByUserId(int id);
	Cart getCartByCartIdAndCustomerIdAndIsCheckedFalse(int cid,int uid);
	 double calculateTotalPrice(Cart cart);
	Cart removeProductFromCart(int CartProductId) ;
	Cart increaseProductInCart(int productCartId);
	Cart decreaseProductInCart(int productCartId);
	Cart getCartByUserIdAndIsCheckedFalse(int cartId);
	boolean isCartEmpty(int customerId);
	Cart saveCart(Cart cart);
}
