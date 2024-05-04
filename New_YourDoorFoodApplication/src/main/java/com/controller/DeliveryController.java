package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exception.CustomerException;
import com.exception.DeliveryException;
import com.exception.LoginException;
import com.exception.OrderDetailsException;
import com.exception.TokenException;
import com.model.MessageDTO;
import com.service.DeliveryService;

@RestController
@RequestMapping("/api/YourDoorFood")
public class DeliveryController {

	@Autowired
	private DeliveryService deliveryService;

	@GetMapping("/delivery/customer/{orderId}")
	public ResponseEntity<MessageDTO> orderDeliveryHandler(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("orderId") Integer orderId)
			throws DeliveryException, LoginException, OrderDetailsException, CustomerException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		MessageDTO result = deliveryService.getOrderDetails(jwtToken, orderId);
		return new ResponseEntity<>(result, HttpStatus.FOUND);
	}

	private String extractJwtToken(String authorizationHeader) {
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7);
		}
		return null;
	}
}
