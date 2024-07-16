package com.service;

import java.util.List;
import java.util.Map;

import com.exception.CustomerException;
import com.exception.ItemException;
import com.exception.LoginException;
import com.exception.RestaurantException;
import com.exception.TokenException;
import com.model.Item;

public interface ItemService {
	
	public List<Item> viewAllItems() throws ItemException;

	public Item addItem(String token,Item item)throws ItemException, LoginException, RestaurantException, TokenException;
	
	public Item updateItem(String token,Item item) throws ItemException, LoginException, RestaurantException, TokenException;
	
	public List<Item> viewAllItemsByRestaurant(Integer restaurantId) throws ItemException ,RestaurantException;
		
	public Item viewItem(String itemName, Integer restaurantId) throws ItemException, RestaurantException;
	
	public Map<String, Item> viewItemsOnMyAddress(String token, String itemName) throws ItemException, RestaurantException, LoginException, CustomerException, TokenException;
	
	
}
