package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exception.BillException;
import com.exception.CustomerException;
import com.exception.FoodCartException;
import com.exception.ItemException;
import com.exception.LoginException;
import com.exception.OrderDetailsException;
import com.exception.RestaurantException;
import com.exception.TokenException;
import com.model.MessageDTO;
import com.model.OrderDetails;
import com.service.OrderService;

@RestController
@RequestMapping("/api/YourDoorFood")
@CrossOrigin(value = "*")
public class OrderController {
	@Autowired
	private OrderService orderService;

	@PostMapping("/orders/customer/COD/order_itemfrom_cart")
	public ResponseEntity<List<OrderDetails>> placeOrderHandlerByCOD(@RequestHeader("Authorization") String authorizationHeader)
			throws OrderDetailsException, LoginException, CustomerException, FoodCartException, ItemException,
			BillException, RestaurantException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		List<OrderDetails> orders = orderService.orderItem(jwtToken, "CASH_ON_DELIVERY");
		return new ResponseEntity<>(orders, HttpStatus.ACCEPTED);
	}

	@PostMapping("/orders/customer/prepaid/order_itemfrom_cart")
	public ResponseEntity<List<OrderDetails>> placeOrderHandlerByPrepaid(@RequestHeader("Authorization") String authorizationHeader)
			throws OrderDetailsException, LoginException, CustomerException, FoodCartException, ItemException,
			BillException, RestaurantException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		List<OrderDetails> orders = orderService.orderItem(jwtToken, "PAYMENT_SUCCESS");
		return new ResponseEntity<>(orders, HttpStatus.ACCEPTED);
	}

	@DeleteMapping("/orders/customer/cancel_order/{orderId}")
	public ResponseEntity<MessageDTO> cancelOrderHandler(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("orderId") Integer orderId) throws OrderDetailsException, LoginException, CustomerException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		MessageDTO messageDTO = orderService.cancelOrder(jwtToken,orderId);
		return new ResponseEntity<MessageDTO>(messageDTO, HttpStatus.OK);
	}

	@GetMapping("/orders/customer/view_order/{orderId}")
	public ResponseEntity<OrderDetails> viewOrderByIdByCustomerHandler(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("orderId") Integer orderId) throws OrderDetailsException, CustomerException, LoginException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		OrderDetails orderDetails = orderService.viewOrderByIdByCustomer(jwtToken, orderId);
		return new ResponseEntity<OrderDetails>(orderDetails, HttpStatus.OK);
	}

	@GetMapping("/orders/restaurant/view_order/{orderId}")
	public ResponseEntity<OrderDetails> viewOrderByIdByRestaurantHandler(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("orderId") Integer orderId)
			throws OrderDetailsException, LoginException, RestaurantException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		OrderDetails orderDetails = orderService.viewOrderByIdByRestaurant(jwtToken, orderId);
		return new ResponseEntity<OrderDetails>(orderDetails, HttpStatus.OK);
	}

	@GetMapping("/orders/customer/view_all_orders")
	public ResponseEntity<List<OrderDetails>> viewAllOrderByCustomerHandler(@RequestHeader("Authorization") String authorizationHeader)
			throws OrderDetailsException, CustomerException, LoginException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		List<OrderDetails> orders = orderService.viewAllOrdersByCustomer(jwtToken);
		return new ResponseEntity<List<OrderDetails>>(orders, HttpStatus.ACCEPTED);
	}


	@GetMapping("/orders/restaurant/view_all_orders")
	public ResponseEntity<List<OrderDetails>> viewAllOrderByRestaurantHandler(@RequestHeader("Authorization") String authorizationHeader)
			throws OrderDetailsException, LoginException, RestaurantException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		List<OrderDetails> orders = orderService.viewAllOrdersByRestaurant(jwtToken);
		return new ResponseEntity<List<OrderDetails>>(orders, HttpStatus.ACCEPTED);
	}

	@GetMapping("/orders/restaurant/view_all_orders_of_customer_from_our_restaurant/{customerId}")
	public ResponseEntity<List<OrderDetails>> viewAllOrderByRestaurantByCustomerIdHandler(
			@RequestHeader("Authorization") String authorizationHeader, @PathVariable("customerId") Integer customerId)
			throws OrderDetailsException, LoginException, RestaurantException, CustomerException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		List<OrderDetails> orders = orderService.viewAllOrdersByRestaurantByCustomerId(jwtToken, customerId);
		return new ResponseEntity<List<OrderDetails>>(orders, HttpStatus.ACCEPTED);
	}

	private String extractJwtToken(String authorizationHeader) {
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7);
		}
		return null;
	}
}
