package com.service;

import java.util.List;

import com.exception.CustomerException;
import com.exception.ItemException;
import com.exception.LoginException;
import com.exception.RestaurantException;
import com.exception.TokenException;
import com.model.MessageDTO;
import com.model.ResetPasswordDTO;
import com.model.Restaurant;
import com.model.Suggestion;

public interface RestaurantService {

	public Restaurant addRestaurant(Integer verificationId, Restaurant restaurant)throws RestaurantException;
	
	public Restaurant updateRestaurant(String token, Restaurant res)throws RestaurantException, LoginException, TokenException;
	
	public Restaurant viewRestaurant(Integer restaurantId)throws RestaurantException;
	
	public List<Restaurant> viewNearByRestaurant(String cityName, String pincode)throws RestaurantException;
	
	public List<Restaurant> viewRestaurantByItemName(String itemname, String pincode)throws RestaurantException;
	
	public MessageDTO restaurantStatus(Integer restaurantId) throws RestaurantException;
	
	public MessageDTO giveSuggestionAboutItem(String token, Suggestion suggestion, String pincode) throws CustomerException, LoginException, RestaurantException, TokenException;
	
	public List<Suggestion> viewSuggestions(String token) throws LoginException, RestaurantException, TokenException;

	public MessageDTO  updatepassword(String token, ResetPasswordDTO resetPasswordDTO) throws RestaurantException, LoginException, TokenException;
	
	public Restaurant getRestaurantByItemId(Integer itemId) throws RestaurantException, ItemException;
	
	public List<Restaurant> getAllRestuList()throws RestaurantException;
}
