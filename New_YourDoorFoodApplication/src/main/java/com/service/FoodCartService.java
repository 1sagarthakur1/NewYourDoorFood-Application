package com.service;

import java.util.List;

import com.exception.CustomerException;
import com.exception.FoodCartException;
import com.exception.ItemException;
import com.exception.LoginException;
import com.exception.RestaurantException;
import com.exception.TokenException;
import com.model.FoodCart;
import com.model.ItemQuantityDTO;

public interface FoodCartService {
	public FoodCart addItemToCart(String token, String itemName, Integer restaurantId) throws FoodCartException, LoginException, ItemException, RestaurantException, CustomerException, TokenException;
	
	public FoodCart increaseQuantity(String token, String itemName, 	int quantity) throws FoodCartException, LoginException, ItemException, CustomerException, TokenException;
	
	public FoodCart reduceQuantity(String token, String itemName, int quantity) throws FoodCartException, LoginException, ItemException, CustomerException, TokenException;
	
	public FoodCart removeItem(String token, String itemName) throws FoodCartException, CustomerException, LoginException, TokenException;
	
	public FoodCart clearCart(String token) throws FoodCartException, CustomerException, LoginException, TokenException;
	
	public List<ItemQuantityDTO> viewCart(String token) throws LoginException, CustomerException, FoodCartException, TokenException; 
}
