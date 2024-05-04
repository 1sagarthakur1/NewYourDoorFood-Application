package com.service;

import com.exception.CustomerException;
import com.exception.DeliveryException;
import com.exception.LoginException;
import com.exception.OrderDetailsException;
import com.exception.TokenException;
import com.model.MessageDTO;

public interface DeliveryService {

	public MessageDTO getOrderDetails(String key, Integer orderId)
			throws DeliveryException, LoginException, OrderDetailsException, CustomerException, TokenException;

}
