package com.ecommerce.servicesimpl;

import java.util.List;
import java.util.function.DoubleToLongFunction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.dao.CartDao;
import com.ecommerce.dao.CartProductDao;
import com.ecommerce.dao.CustomerDao;
import com.ecommerce.dao.ProductDao;
import com.ecommerce.models.Cart;
import com.ecommerce.models.CartProduct;
import com.ecommerce.models.Customer;
import com.ecommerce.models.Product;
import com.ecommerce.services.CartService;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private CartDao cartDao;
    @Autowired
    private CartProductDao cartProductDao;

    @Transactional
    @Override
    public Cart saveCart(int cid, int pid) {
        Customer customer = this.customerDao.findById(cid).get() ;
        Product product = this.productDao.findById(pid).get();
        Cart cart = cartDao.findByCustomerIdAndIsCheckedFalse(cid);

        if (cart == null) {
            cart = createNewCart(customer);
            
            cartDao.save(cart); // Ensure cart is saved before adding products
        }

        // Update CartProduct
        CartProduct cartProduct = cart.getCartProducts().stream()
                .filter(p -> p.getProduct().getId() == pid)
                .findFirst()
                .orElse(null);

        if (cartProduct == null) {
            cartProduct = new CartProduct();
            cartProduct.setCart(cart);
            cartProduct.setQuantity(1);
            cartProduct.setProduct(product);
          
            // Initialize totalPrice correctly
            cart.getCartProducts().add(cartProduct);
        } else {
            cartProduct.setQuantity(cartProduct.getQuantity() + 1);
           // Update totalPrice correctly
        }

        // Save the updated CartProduct
        cartProductDao.save(cartProduct);

        // Save the updated Cart
        return cartDao.save(cart);
    }

    @Override
    public Cart createNewCart(Customer customer) {
        Cart cart = new Cart();
        cart.setCustomer(customer);
        return cart;
    }

	@Override
	public Cart getCartByUserId(int id) {
		
		return this.cartDao.findByCustomerId(id);
	}

	@Override
	public Cart getCartByCartIdAndCustomerIdAndIsCheckedFalse(int cid,int uid) {
	
		return this.cartDao.findByIdAndCustomerIdAndIsCheckedFalse(cid, uid);
	}
	
	public double calculateTotalPrice(Cart cart) {
	   double totalPrice=0.0;
	   List< CartProduct> cartProducts=cart.getCartProducts();
	   for(CartProduct c:cartProducts) {
		   totalPrice=totalPrice+c.getQuantity()*c.getProduct().getDiscountedPrice();
	   }
	   return totalPrice;
	}
	
	public Cart removeProductFromCart(int CartProductId) {
		CartProduct cartProduct=this.cartProductDao.findById(CartProductId).get();
		Cart cart=cartProduct.getCart();
		if(cart!=null) {
			cart.getCartProducts().remove(cartProduct);
			cartProductDao.deleteById(CartProductId);
		}
		return cartDao.save(cart);
	}

	@Override
	public Cart increaseProductInCart(int productCartId) {
		CartProduct cartProduct=this.cartProductDao.findById(productCartId).get();
		Cart cart=cartProduct.getCart();
		if(cart!=null) {
			cartProduct.setQuantity(cartProduct.getQuantity()+1);
			cartProductDao.save(cartProduct);
		}
		
		return cartDao.save(cart);
	}

	@Override
	public Cart decreaseProductInCart( int productCartId) {
		CartProduct cartProduct=this.cartProductDao.findById(productCartId).get();
		Cart cart=cartProduct.getCart();
		if(cart!=null) {
			cartProduct.setQuantity(cartProduct.getQuantity()-1);
			
			if(cartProduct.getQuantity()<=0) {
				cart.getCartProducts().remove(cartProduct);
				cartProduct.setCart(null);
			}
		}
		
		cartProductDao.save(cartProduct);
		return cartDao.save(cart);
	}

	@Override
	public Cart getCartByUserIdAndIsCheckedFalse(int userId) {
		
		return this.cartDao.findByCustomerIdAndIsCheckedFalse(userId);
	}

	@Override
	public boolean isCartEmpty(int cartId) {
		Cart cart=cartDao.findById(cartId).get();
		if(cart.getCartProducts().isEmpty())
		{
			return true;
		}
		   return false;
		
	}

	@Override
	public Cart saveCart(Cart cart) {
		// TODO Auto-generated method stub
		return this.cartDao.save(cart);
	}
}
