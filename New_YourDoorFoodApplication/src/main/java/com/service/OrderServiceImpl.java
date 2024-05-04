package com.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.config.JwtTokenValidatorFilter;
import com.exception.BillException;
import com.exception.CustomerException;
import com.exception.FoodCartException;
import com.exception.ItemException;
import com.exception.LoginException;
import com.exception.OrderDetailsException;
import com.exception.RestaurantException;
import com.exception.TokenException;
import com.model.Bill;
import com.model.Customer;
import com.model.FoodCart;
import com.model.Item;
import com.model.ItemQuantityDTO;
import com.model.MessageDTO;
import com.model.OrderDetails;
import com.model.Restaurant;
import com.model.Status;
import com.repository.CustomerRepo;
import com.repository.FoodCartRepo;
import com.repository.ItemRepo;
import com.repository.OrderDetailsRepo;
import com.repository.RestaurantRepo;

import io.jsonwebtoken.Claims;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderDetailsRepo orderDetailsRepo;

	@Autowired
	private CustomerRepo customerRepo;

	@Autowired
	private ItemRepo itemRepo;

	@Autowired
	private RestaurantRepo restaurantRepo;

	@Autowired
	private FoodCartRepo foodCartRepo;

	@Autowired
	private BillService billService;

	@Autowired
	private RestaurantService restaurantService;

	@Autowired
	private JwtTokenValidatorFilter jwtTokenValidatorFilter;

	@Override
	public List<OrderDetails> orderItem(String token, String paymentType) throws OrderDetailsException, LoginException,
			CustomerException, FoodCartException, ItemException, BillException, RestaurantException, TokenException {

		Claims claims = jwtTokenValidatorFilter.tokenValidatingforCustomar(token);
		if (claims == null)
			throw new LoginException("Please login to order Item");
		Customer customer = customerRepo.findById(claims.get("customertId", Integer.class))
				.orElseThrow(() -> new CustomerException("Please login as Customer"));

		FoodCart foodCart = customer.getFoodCart();

		Map<Integer, Integer> itemsMap = foodCart.getItems();
		if (itemsMap.isEmpty())
			throw new FoodCartException("Item(s) not found in your cart");

		for (Map.Entry<Integer, Integer> entry : itemsMap.entrySet()) {

			Item itemInCart = itemRepo.findById(entry.getKey()).get();

			if (itemInCart.getQuantity() < entry.getValue()) {
				throw new ItemException("Insufficient item quantity in the restaurant");
			}

			if (restaurantService.restaurantStatus(itemInCart.getRestaurant().getRestaurantId()).equals("Closed")) {
				throw new RestaurantException(itemInCart.getRestaurant().getRestaurantName() + " is closed");
			}

			if (!itemInCart.getRestaurant().getAddress().getPincode().equals(customer.getAddress().getPincode())) {
				throw new CustomerException("This item is not deliverable in your area");
			}
		}

		Map<Integer, OrderDetails> restaurantOrderMap = new HashMap<>();

		for (Map.Entry<Integer, Integer> entry : itemsMap.entrySet()) {

			Item item = itemRepo.findById(entry.getKey()).get();

			Integer restaurantId = item.getRestaurant().getRestaurantId();

			if (restaurantOrderMap.containsKey(restaurantId)) {

				ItemQuantityDTO dto = new ItemQuantityDTO(item.getItemId(), item.getItemName(), entry.getValue(),
						item.getCategory().getCategoryName(), item.getCost());

				OrderDetails orderDetails = restaurantOrderMap.get(restaurantId);
				orderDetails.setTotalAmount(orderDetails.getTotalAmount() + item.getCost() * entry.getValue());
				orderDetails.getItems().add(dto);

				restaurantOrderMap.put(restaurantId, orderDetails);

			} else {

				ItemQuantityDTO dto = new ItemQuantityDTO(item.getItemId(), item.getItemName(), entry.getValue(),
						item.getCategory().getCategoryName(), item.getCost());

				OrderDetails orderDetails = new OrderDetails();
				orderDetails.setOrderDate(LocalDateTime.now());
				orderDetails.setCustomerId(customer.getCustomerID());
				orderDetails.setRestaurantId(restaurantId);
				orderDetails.setPaymentStatus(Status.valueOf(paymentType));
				orderDetails.setTotalAmount(item.getCost() * entry.getValue());
				orderDetails.getItems().add(dto);

				restaurantOrderMap.put(restaurantId, orderDetails);
			}

			item.setQuantity(item.getQuantity() - entry.getValue());
			itemRepo.save(item);
		}

		List<OrderDetails> orderDetailsList = new ArrayList<>();

		for (Map.Entry<Integer, OrderDetails> restaurantOrder : restaurantOrderMap.entrySet()) {

			OrderDetails orderDetails = restaurantOrder.getValue();

			Bill bill = billService.genrateBill(orderDetails);

			orderDetails.setBill(bill);

			orderDetails = orderDetailsRepo.save(orderDetails);

			orderDetailsList.add(orderDetails);
		}

		foodCart.setItems(new HashMap<Integer, Integer>());

		foodCartRepo.save(foodCart);

		return orderDetailsList;

	}

	@Override
	public MessageDTO cancelOrder(String token, Integer orderId)
			throws OrderDetailsException, LoginException, CustomerException, TokenException {

		Claims claims = jwtTokenValidatorFilter.tokenValidatingforCustomar(token);
		if (claims == null)
			throw new LoginException("Please login to cancel order");
		Customer customer = customerRepo.findById(claims.get("customertId", Integer.class))
				.orElseThrow(() -> new CustomerException("Please login as Customer"));

		OrderDetails orderDetails = orderDetailsRepo.findById(orderId)
				.orElseThrow(() -> new OrderDetailsException("Please pass valid order Id"));

		if (orderDetails.getCustomerId() != customer.getCustomerID())
			throw new CustomerException("Invalid order Id: " + orderId);

		LocalDateTime deliverTime = orderDetails.getOrderDate().plusMinutes(20);

		if (LocalDateTime.now().isAfter(deliverTime.minusMinutes(10))) {
			throw new OrderDetailsException("Order can not be cancelled, time limit exceeded for cancellation");
		}

		List<ItemQuantityDTO> itemsDto = orderDetails.getItems();

		String result = "Order cancelled successfully";

		if (orderDetails.getPaymentStatus().toString().equals("PAYMENT_SUCCESS")) {
			result += " and your payment transferred back to your account.";
		}

		orderDetailsRepo.delete(orderDetails);

		for (ItemQuantityDTO i : itemsDto) {

			Item item = itemRepo.findById(i.getItemId()).get();
			item.setQuantity(item.getQuantity() + i.getOrderedQuantity());
			itemRepo.save(item);
		}

		return new MessageDTO(LocalDateTime.now(), result);

	}

	@Override
	public OrderDetails viewOrderByIdByCustomer(String token, Integer orderId)
			throws OrderDetailsException, CustomerException, LoginException, TokenException {

		Claims claims = jwtTokenValidatorFilter.tokenValidatingforCustomar(token);
		if (claims == null)
			throw new LoginException("Please login to view orders of customer");
		Customer customer = customerRepo.findById(claims.get("customertId", Integer.class))
				.orElseThrow(() -> new CustomerException("Please login as Customer"));

		OrderDetails orderDetails = orderDetailsRepo.findById(orderId)
				.orElseThrow(() -> new OrderDetailsException("Invalid order Id: " + orderId));

		if (orderDetails.getCustomerId() != customer.getCustomerID())
			throw new OrderDetailsException("Invalid order Id: " + orderId);

		return orderDetails;

	}

	@Override
	public OrderDetails viewOrderByIdByRestaurant(String token, Integer orderId)
			throws OrderDetailsException, RestaurantException, LoginException, TokenException {
		
		Claims claims = jwtTokenValidatorFilter.tokenValidatingforRestaurant(token);
		if (claims == null) 
			throw new LoginException("Please login to view order details");
		Restaurant restaurant = restaurantRepo.findById(claims.get("restaurantId",Integer.class))
				.orElseThrow(() -> new RestaurantException("Please login as Restaurant"));

		OrderDetails orderDetails = orderDetailsRepo.findById(orderId)
				.orElseThrow(() -> new OrderDetailsException("Invalid order Id: " + orderId));

		if (orderDetails.getRestaurantId().equals(restaurant.getRestaurantId())) {
			return orderDetails;
		}

		throw new OrderDetailsException("Invalid order Id: " + orderId);

	}

	@Override
	public List<OrderDetails> viewAllOrdersByRestaurant(String token )
			throws OrderDetailsException, LoginException, RestaurantException, TokenException {

		Claims claims = jwtTokenValidatorFilter.tokenValidatingforRestaurant(token);
		if (claims == null) 
			throw new LoginException("Please login to view order(s) details");
		Restaurant restaurant = restaurantRepo.findById(claims.get("restaurantId",Integer.class))
				.orElseThrow(() -> new RestaurantException("Please login as Restaurant"));

		List<OrderDetails> orders = orderDetailsRepo.findByRestaurantId(restaurant.getRestaurantId());

		if (orders.isEmpty())
			throw new OrderDetailsException("Orders not found");

		return orders;
	}

	@Override
	public List<OrderDetails> viewAllOrdersByCustomer(String token)
			throws OrderDetailsException, CustomerException, LoginException, TokenException {

		Claims claims = jwtTokenValidatorFilter.tokenValidatingforCustomar(token);
		if (claims == null)
			throw new LoginException("Please login view all orders");
		Customer customer = customerRepo.findById(claims.get("customertId", Integer.class))
				.orElseThrow(() -> new CustomerException("Please login as Customer"));

		List<OrderDetails> orders = orderDetailsRepo.findByCustomerId(customer.getCustomerID());

		if (orders.isEmpty())
			throw new OrderDetailsException("Order(s) not found");

		return orders;

	}

	@Override
	public List<OrderDetails> viewAllOrdersByRestaurantByCustomerId(String token, Integer customerId)
			throws OrderDetailsException, LoginException, RestaurantException, CustomerException, TokenException {
		
		Claims claims = jwtTokenValidatorFilter.tokenValidatingforRestaurant(token);
		if (claims == null) 
			throw new LoginException("Please login to view all orders restaurant of customer");
		Restaurant restaurant = restaurantRepo.findById(claims.get("restaurantId",Integer.class))
				.orElseThrow(() -> new RestaurantException("Please login as Restaurant"));

		List<OrderDetails> orders = orderDetailsRepo.findByRestaurantIdAndCustomerId(restaurant.getRestaurantId(),
				customerId);

		if (orders.isEmpty())
			throw new OrderDetailsException("Orders not found");

		return orders;

	}

}