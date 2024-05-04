package com.service;

import java.util.List;

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

public interface OrderService {
	public List<OrderDetails> orderItem(String token, String paymentType) throws OrderDetailsException, LoginException,
			CustomerException, FoodCartException, ItemException, BillException, RestaurantException, TokenException;

	public MessageDTO cancelOrder(String token, Integer orderId)
			throws OrderDetailsException, LoginException, CustomerException, TokenException;

	public OrderDetails viewOrderByIdByCustomer(String token, Integer orderId)
			throws OrderDetailsException, CustomerException, LoginException, TokenException;

	public OrderDetails viewOrderByIdByRestaurant(String token, Integer orderId)
			throws OrderDetailsException, LoginException, RestaurantException, TokenException;

	public List<OrderDetails> viewAllOrdersByRestaurant(String token)
			throws OrderDetailsException, LoginException, RestaurantException, TokenException;

	public List<OrderDetails> viewAllOrdersByCustomer(String token)
			throws OrderDetailsException, CustomerException, LoginException, TokenException;

	public List<OrderDetails> viewAllOrdersByRestaurantByCustomerId(String token, Integer customerId)
			throws OrderDetailsException, LoginException, RestaurantException, CustomerException, TokenException;

}
