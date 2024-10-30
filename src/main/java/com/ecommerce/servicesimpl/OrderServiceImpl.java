package com.ecommerce.servicesimpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.DoubleToLongFunction;

import org.hibernate.grammars.hql.HqlParser.NthSideClauseContext;
import org.springframework.aot.nativex.NativeConfigurationWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.dao.CartDao;
import com.ecommerce.dao.CartProductDao;
import com.ecommerce.dao.CustomerDao;
import com.ecommerce.dao.OrderDao;
import com.ecommerce.dao.OrderItemDao;
import com.ecommerce.dao.ProductDao;
import com.ecommerce.models.Cart;
import com.ecommerce.models.CartProduct;
import com.ecommerce.models.Customer;
import com.ecommerce.models.Order;
import com.ecommerce.models.OrderItem;
import com.ecommerce.services.OrderService;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import ch.qos.logback.core.testUtil.RandomUtil;
import jakarta.transaction.Transactional;

@Service
//@Transactional
public class OrderServiceImpl implements OrderService {
	@Autowired
	private CustomerDao customerDao;
	@Autowired
	private ProductDao productDao;
	@Autowired
	private CartDao cartDao;
	@Autowired
	private CartProductDao cartProductDao;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private OrderItemDao orderItemDao;

	@Override
	public Order placeOrder(int cartId, int userId, Order o) {
		Cart cart = cartDao.findByIdAndCustomerIdAndIsCheckedFalse(cartId, userId);
		Customer customer = customerDao.findById(userId).get();
		Order order = new Order();
		double totalPrice = 0.0;
//	   CartProduct cartProduct=
		if (cart != null) {

			List<CartProduct> cartProducts = cart.getCartProducts();
			List<OrderItem> orderItems = new ArrayList<>();
			for (CartProduct c : cartProducts) {
				OrderItem orderItem = new OrderItem();
				orderItem.setOrder(order);
				orderItem.setProduct(c.getProduct());
				orderItem.setQuantity(c.getQuantity());
				orderItem.setPrice(c.getProduct().getDiscountedPrice() * c.getQuantity());
				orderItem.getProduct().setCustomer(customer);
				customer.getProducts().add(orderItem.getProduct());
				totalPrice = totalPrice + orderItem.getPrice();
				orderItems.add(orderItem);

			}

			order.setCustomer(customer);
			order.setOrderItems(orderItems);
			order.setPlacedDate(LocalDateTime.now());
			order.setShippingCountry(o.getShippingCountry());
			order.setShippingState(o.getShippingState());
			order.setShippingDistrict(o.getShippingDistrict());
			order.setShippingMuncipility(o.getShippingMuncipility());
			order.setShippingWard(o.getShippingWard());
			order.setShippingTole(o.getShippingTole());
			order.setPaymentMethod(o.getPaymentMethod());
//			order.setTotalPrice(totalPrice);
			order.setStatus("Submitted");
			order.setInvoiceNumber("INV-" + UUID.randomUUID().toString());
			if(order.getShippingState().equals("sudurpaschim")) {
				order.setShippingCost(50.0);
			}
			else if(order.getShippingState().equals("karnali")) {
				order.setShippingCost(100.0);
			}
			else if(order.getShippingState().equals("province5")) {
				order.setShippingCost(250.0);
			}
			else if(order.getShippingState().equals("gandaki")) {
				order.setShippingCost(200.0);
			}
			else if(order.getShippingState().equals("bagmati")) {
				order.setShippingCost(500.0);
			}
			else if(order.getShippingState().equals("province1")) {
				order.setShippingCost(600.0);
			}
			else if(order.getShippingState().equals("province2")) {
				order.setShippingCost(700.0);
			}
			
			order.setTotalPrice(totalPrice+order.getShippingCost());

		}
		cart.setChecked(true);
		cartDao.save(cart);
		return orderDao.save(order);
	}

	@Override
	public Order findByCustomerAndIsVerifiedFalse(Customer customer) {

		List<Order> orders = this.orderDao.findByCustomerAndIsVerifiedFalse(customer);
		return orders.get(0);
	}

	@Override
	public Order saveOrder(Order order) {

		return this.orderDao.save(order);
	}

	@Override
	public List<Order> showVerifiedOrders() {
		// TODO Auto-generated method stub
		return this.orderDao.findByIsVerifiedTrue();
	}

	@Override
	public List<Order> showUnverfiedOrders() {
		// TODO Auto-generated method stub
		return this.orderDao.findByIsVerifiedFalse();
	}

	@Override
	public Order findById(int id) {
		System.out.println("The id is:" + id);
		return this.orderDao.findById(id).get();
	}

	@Override
	public boolean deleteProductFromOrder(int orderItemId) {
		OrderItem orderItem = this.orderItemDao.findById(orderItemId).get();
		Order order = orderItem.getOrder();
		if (order != null) {
			order.getOrderItems().remove(orderItem);
			orderItem.setOrder(null);
			orderItem.setProduct(null);
			orderItemDao.save(orderItem);
			orderDao.save(order);
			if (order.getOrderItems().isEmpty()) {
				orderDao.deleteById(order.getId());
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean increaseProductFromOrder(int orderItemId) {
		OrderItem orderItem = this.orderItemDao.findById(orderItemId).get();
		Order order = orderItem.getOrder();
		if (order != null) {
			orderItem.setQuantity(orderItem.getQuantity() + 1);
			orderItem.setPrice(orderItem.getProduct().getDiscountedPrice() * orderItem.getQuantity());
			order.setTotalPrice(order.getTotalPrice() + orderItem.getPrice());
			orderItemDao.save(orderItem);
			orderDao.save(order);
			orderItemDao.save(orderItem);
			orderDao.save(order);
			return true;
		}

		return false;
	}

	@Override
	public boolean decreaseProductFromOrder(int orderItemId) {
		OrderItem orderItem = this.orderItemDao.findById(orderItemId).get();
		Order order = orderItem.getOrder();

		if (order != null) {
			if (orderItem.getQuantity() > 0) {
				orderItem.setQuantity(orderItem.getQuantity() - 1);
				orderItem.setPrice(orderItem.getProduct().getDiscountedPrice() * orderItem.getQuantity());
				order.setTotalPrice(order.getTotalPrice() - orderItem.getProduct().getDiscountedPrice());
				orderItemDao.save(orderItem);
				orderDao.save(order);

			} else {
				orderItem.setProduct(null);
				order.getOrderItems().remove(orderItem);
				orderItemDao.deleteById(orderItemId);
				orderItemDao.save(orderItem);
				orderDao.save(order);
			}

			return true;
		}
		return false;
	}

	@Override
	public Order updateOrder(Order oldOrder, Order newOrder) {
		oldOrder.getCustomer().setName(newOrder.getCustomer().getName());
		;
		oldOrder.setPaymentMethod(newOrder.getPaymentMethod());
		oldOrder.setShippingCountry(newOrder.getShippingCountry());
		oldOrder.setShippingState(newOrder.getShippingTole());
		oldOrder.setPlacedDate(newOrder.getPlacedDate());
		oldOrder.setStatus(newOrder.getStatus());
		oldOrder.setTotalPrice(0);
		for (OrderItem newItem : newOrder.getOrderItems()) {

			for (OrderItem oldItem : oldOrder.getOrderItems()) {
				System.out.println("Old item id:" + oldItem.getId());
				System.out.println("New item id:" + newItem.getId());

				if (oldItem.getId() == newItem.getId()) {

					if (newItem.getQuantity() == 0) {
						System.out.println("Inside if block");
						oldItem.setOrder(null);
						oldOrder.getOrderItems().remove(oldItem);
						orderItemDao.save(oldItem);
					} else {
						System.out.println("Inside else block");
						oldItem.setQuantity(newItem.getQuantity());
						oldItem.setPrice(newItem.getProduct().getDiscountedPrice() * newItem.getQuantity());
						System.out.println("Item Price is:" + oldItem.getPrice());
						oldOrder.setTotalPrice(oldOrder.getTotalPrice() + oldItem.getPrice());
						orderItemDao.save(oldItem);

					}
					break;
				}
			}
		}

		System.out.println("Order total Price is:" + oldOrder.getTotalPrice());
		return this.orderDao.save(oldOrder);
	}

	@Override
	public List<Order> findByCustomer(Customer customer) {

		return this.orderDao.findByCustomerAndStatus(customer);
	}

	@Override
	public Order cancelOrder(int orderId) {
		Order order = this.orderDao.findById(orderId).get();
		order.setStatus("Cancelled");
		return this.orderDao.save(order);
	}

}
