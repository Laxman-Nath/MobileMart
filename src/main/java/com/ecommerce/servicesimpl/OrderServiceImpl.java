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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ecommerce.commonutils.EmailUtils;
import com.ecommerce.constants.BillConstant;
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
import com.ecommerce.services.CartProductService;
import com.ecommerce.services.CartService;
import com.ecommerce.services.CustomerService;
import com.ecommerce.services.OrderItemService;
import com.ecommerce.services.OrderService;
import com.ecommerce.services.ProductService;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import ch.qos.logback.core.testUtil.RandomUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
//@Transactional

public class OrderServiceImpl implements OrderService {
	@Autowired
	private CustomerService customerService;
	@Autowired
	private ProductService productService;
	@Autowired
	private CartService cartService;
	@Autowired
	private CartProductService cartProductService;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private OrderItemService orderItemService;
	@Autowired
	private EmailUtils emailUtils;

	@Override
	public Order placeOrder(int cartId, int userId, Order o, String code) {
		Cart cart = cartService.getCartByCartIdAndCustomerIdAndIsCheckedFalse(cartId, userId);
		Customer customer = customerService.findCustomerById(userId);
		customer.setCode(code);
		customerService.addCustomer(customer);
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
				c.getProduct().setQuantity(c.getProduct().getQuantity() - c.getQuantity());
				totalPrice = totalPrice + orderItem.getPrice();
				orderItems.add(orderItem);

			}

			order.setCustomer(customer);
			order.setOrderItems(orderItems);
			order.setPlacedDate(LocalDateTime.now());

			order.setState(o.getState());
			order.setDistrict(o.getDistrict());
			order.setMuncipility(o.getMuncipility());
			order.setWard(o.getWard());
			order.setTole(o.getTole());
			order.setPaymentMethod(o.getPaymentMethod());
//			order.setTotalPrice(totalPrice);
			order.setStatus("Submitted");
			order.setIsPaid(o.getIsPaid());
			order.setInvoiceNumber("INV-" + UUID.randomUUID().toString());
			if (order.getState().equalsIgnoreCase("Sudurpaschim Province")) {
				double totalWeight = order.getOrderItems().stream().mapToDouble(item -> {
					Object weight = item.getProduct().getWeight();
					return weight instanceof Number ? ((Number) weight).doubleValue() : 0.0;
				}).sum();

				order.setShippingCost(totalWeight * 0.5 + 300.0);

			} else if (order.getState().equals("Karnali Province")) {
				double totalWeight = order.getOrderItems().stream().mapToDouble(item -> {
					Object weight = item.getProduct().getWeight();
					return weight instanceof Number ? ((Number) weight).doubleValue() : 0.0;
				}).sum();

				order.setShippingCost(totalWeight * 0.5 + 500.0);
			} else if (order.getState().equals("Lumbini Province")) {
				double totalWeight = order.getOrderItems().stream().mapToDouble(item -> {
					Object weight = item.getProduct().getWeight();
					return weight instanceof Number ? ((Number) weight).doubleValue() : 0.0;
				}).sum();

				order.setShippingCost(totalWeight * 0.5 + 800.0);
			} else if (order.getState().equals("Gandaki Province")) {
				double totalWeight = order.getOrderItems().stream().mapToDouble(item -> {
					Object weight = item.getProduct().getWeight();
					return weight instanceof Number ? ((Number) weight).doubleValue() : 0.0;
				}).sum();

				order.setShippingCost(totalWeight * 0.5 + 1500.0);
			} else if (order.getState().equals("Bagmati Province")) {
				double totalWeight = order.getOrderItems().stream().mapToDouble(item -> {
					Object weight = item.getProduct().getWeight();
					return weight instanceof Number ? ((Number) weight).doubleValue() : 0.0;
				}).sum();

				order.setShippingCost(totalWeight * 0.5 + 2000.0);
			} else if (order.getState().equals("Province No. 2")) {
				double totalWeight = order.getOrderItems().stream().mapToDouble(item -> {
					Object weight = item.getProduct().getWeight();
					return weight instanceof Number ? ((Number) weight).doubleValue() : 0.0;
				}).sum();

				order.setShippingCost(totalWeight * 0.5 + 2500.0);
			} else if (order.getState().equals("Province No. 1")) {
				double totalWeight = order.getOrderItems().stream().mapToDouble(item -> {
					Object weight = item.getProduct().getWeight();
					return weight instanceof Number ? ((Number) weight).doubleValue() : 0.0;
				}).sum();

				order.setShippingCost(totalWeight * 0.5 + 3000.0);
			}

			order.setTotalPrice(totalPrice + order.getShippingCost());

		}
		cart.setChecked(true);
		cartService.saveCart(cart);
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
	public Page<Order> showVerifiedOrders(int pageNo, int pageSize) {
		// TODO Auto-generated method stub
		return this.orderDao.getAllVerifiedOrders(PageRequest.of(pageNo, pageSize));
	}

	@Override
	public Page<Order> showUnverfiedOrders(int pageNo, int pageSize) {
		// TODO Auto-generated method stub
		return this.orderDao.getAllUnVerifiedOrders(PageRequest.of(pageNo, pageSize));
	}

	@Override
	public Order findById(int id) {
		System.out.println("The id is:" + id);
		return this.orderDao.findById(id).get();
	}

	@Override
	public boolean deleteProductFromOrder(int orderItemId) {
		OrderItem orderItem = this.orderItemService.findById(orderItemId);
		Order order = orderItem.getOrder();
		if (order != null) {
			order.getOrderItems().remove(orderItem);
			orderItem.setOrder(null);
			orderItem.setProduct(null);
			orderItemService.saveOrderItem(orderItem);
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
		OrderItem orderItem = this.orderItemService.findById(orderItemId);
		Order order = orderItem.getOrder();
		if (order != null) {
			orderItem.setQuantity(orderItem.getQuantity() + 1);
			orderItem.setPrice(orderItem.getProduct().getDiscountedPrice() * orderItem.getQuantity());
			order.setTotalPrice(order.getTotalPrice() + orderItem.getPrice());
			orderItemService.saveOrderItem(orderItem);
			orderDao.save(order);
			orderItemService.saveOrderItem(orderItem);
			orderDao.save(order);
			return true;
		}

		return false;
	}

	@Override
	public boolean decreaseProductFromOrder(int orderItemId) {
		OrderItem orderItem = this.orderItemService.findById(orderItemId);
		Order order = orderItem.getOrder();

		if (order != null) {
			if (orderItem.getQuantity() > 0) {
				orderItem.setQuantity(orderItem.getQuantity() - 1);
				orderItem.setPrice(orderItem.getProduct().getDiscountedPrice() * orderItem.getQuantity());
				order.setTotalPrice(order.getTotalPrice() - orderItem.getProduct().getDiscountedPrice());
				orderItemService.saveOrderItem(orderItem);
				orderDao.save(order);

			} else {
				orderItem.setProduct(null);
				order.getOrderItems().remove(orderItem);
				orderItemService.deleteById(orderItemId);
				orderItemService.saveOrderItem(orderItem);
				orderDao.save(order);
			}

			return true;
		}
		return false;
	}

	@Override
	public Order updateOrder(Order oldOrder, Order newOrder) {

		oldOrder.setDistrict(newOrder.getDistrict());
		oldOrder.setTole(newOrder.getTole());
		oldOrder.setWard(newOrder.getWard());
		oldOrder.setMuncipility(newOrder.getMuncipility());
		oldOrder.setPaymentMethod(newOrder.getPaymentMethod());

		oldOrder.setState(newOrder.getState());
		oldOrder.setPlacedDate(newOrder.getPlacedDate());
		oldOrder.setStatus(newOrder.getStatus());
		if (newOrder.getIsPaid() == null) {
			oldOrder.setIsPaid(false);
		} else {
			oldOrder.setIsPaid(newOrder.getIsPaid());
		}
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
						orderItemService.saveOrderItem(oldItem);
					} else {
						System.out.println("Inside else block");
						oldItem.setQuantity(newItem.getQuantity());
						oldItem.setPrice(newItem.getProduct().getDiscountedPrice() * newItem.getQuantity());
						System.out.println("Item Price is:" + oldItem.getPrice());
						oldOrder.setTotalPrice(oldOrder.getTotalPrice() + oldItem.getPrice());
						orderItemService.saveOrderItem(oldItem);

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
		List<OrderItem> orderItems = order.getOrderItems();
		for (OrderItem o : orderItems) {
			o.getProduct().setQuantity(o.getQuantity() + o.getProduct().getQuantity());
		}
		order.setStatus("Cancelled");
		return this.orderDao.save(order);
	}

	@Override
	public Long findTotalDeliveredProducts() {

		return this.orderDao.findTotalDeliveredProducts();
	}

	@Override
	public Long findTotalOrders() {

		return this.orderDao.count();
	}

	@Override
	public Order setVerified(Order order, HttpServletRequest request, Customer customer) {
		order.setVerified(true);

		this.emailUtils.sendEmail(BillConstant.SUBJECT, BillConstant.getURL(request), customer, BillConstant.CONTENT,
				false, true, BillConstant.getPdfPath(order.getId()));

		return orderDao.save(order);
	}

	@Override
	public Page<Order> getAllSubmittedOrders(int pageNo, int pageSize) {

		return this.orderDao.getAllSubmittedOrders(PageRequest.of(pageNo, pageSize));
	}

	@Override
	public Page<Order> getAllShippedOrders(int pageNo, int pageSize) {

		return this.orderDao.getAllShippedOrders(PageRequest.of(pageNo, pageSize));
	}

	@Override
	public Page<Order> getAllDeliveredOrders(int pageNo, int pageSize) {

		return this.orderDao.getAllDeliveredOrders(PageRequest.of(pageNo, pageSize));
	}

}
