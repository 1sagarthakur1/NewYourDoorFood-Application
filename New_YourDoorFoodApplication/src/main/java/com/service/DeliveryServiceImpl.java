package com.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.config.JwtTokenValidatorFilter;
import com.exception.CustomerException;
import com.exception.DeliveryException;
import com.exception.LoginException;
import com.exception.OrderDetailsException;
import com.exception.TokenException;
import com.model.Customer;
import com.model.MessageDTO;
import com.model.OrderDetails;
import com.repository.CustomerRepo;
import com.repository.OrderDetailsRepo;

import io.jsonwebtoken.Claims;

@Service
public class DeliveryServiceImpl implements DeliveryService {

	@Autowired
	private OrderDetailsRepo orderDetailsRepo;

	@Autowired
	private CustomerRepo customerRepo;
	
	@Autowired
	private JwtTokenValidatorFilter jwtTokenValidatorFilter;

	@Override
	public MessageDTO getOrderDetails(String token, Integer orderId)
			throws DeliveryException, LoginException, OrderDetailsException, CustomerException, TokenException {
		
		Claims claims = jwtTokenValidatorFilter.tokenValidatingforCustomar(token);
		if (claims == null)
			throw new LoginException("Please login to get your order delivery status");
		Customer customer = customerRepo.findById(claims.get("customertId", Integer.class))
				.orElseThrow(() -> new CustomerException("Please login as Customer"));

		Optional<OrderDetails> orderDetailsOpt = orderDetailsRepo.findById(orderId);

		if (orderDetailsOpt.isEmpty())
			throw new OrderDetailsException("Order not found with this order id: " + orderId);

		OrderDetails orderDetails = orderDetailsOpt.get();

		if (claims.get("customertId", Integer.class) != orderDetails.getCustomerId())
			throw new OrderDetailsException("Order not found with this order id: " + orderId);

		LocalDateTime deliverTime = orderDetails.getOrderDate().plusMinutes(20);

		if (LocalDateTime.now().isAfter(deliverTime)) {
			return new MessageDTO(LocalDateTime.now(), "Your order was delivered on time");
		}

		String paymentStatus = orderDetails.getPaymentStatus().toString();

		String result = "Your order will be delivered at: " + deliverTime.toLocalTime()
				+ " and your payment status is: " + paymentStatus;

		return new MessageDTO(LocalDateTime.now(), result);
	}

}
