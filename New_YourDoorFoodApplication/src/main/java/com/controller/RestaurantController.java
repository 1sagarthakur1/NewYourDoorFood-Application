package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exception.CustomerException;
import com.exception.ItemException;
import com.exception.LoginException;
import com.exception.RestaurantException;
import com.exception.TokenException;
import com.model.MessageDTO;
import com.model.ResetPasswordDTO;
import com.model.Restaurant;
import com.model.Suggestion;
import com.service.RestaurantService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/YourDoorFood")
@CrossOrigin(value = "*")
public class RestaurantController {

	@Autowired
	private RestaurantService resService;

	@PostMapping("/restaurants/register/{verficationId}")
	public ResponseEntity<Restaurant> addRestuarants(@PathVariable("verficationId") Integer verificationId,
			@Valid @RequestBody Restaurant restaurant) throws RestaurantException {
		return new ResponseEntity<>(resService.addRestaurant(verificationId, restaurant), HttpStatus.CREATED);

	}

	@PutMapping("/restaurants/update_basic_details")
	public ResponseEntity<Restaurant> updateRestuarants(@RequestHeader("Authorization") String authorizationHeader,
			@Valid @RequestBody Restaurant restaurant) throws RestaurantException, LoginException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		return new ResponseEntity<>(resService.updateRestaurant(jwtToken, restaurant), HttpStatus.ACCEPTED);

	}

	@GetMapping("/restaurants/{resId}")
	public ResponseEntity<Restaurant> viewRestuarant(@PathVariable("resId") Integer resId) throws RestaurantException {
		return new ResponseEntity<>(resService.viewRestaurant(resId), HttpStatus.FOUND);
	}

	@GetMapping("/restaurants/location/{city}/{pincode}")
	public ResponseEntity<List<Restaurant>> getRestaurantsByLocation(@PathVariable("city") String cityName,
			@PathVariable("pincode") String pincode) throws RestaurantException {
		return new ResponseEntity<>(resService.viewNearByRestaurant(cityName, pincode), HttpStatus.FOUND);

	}

	@GetMapping("/restaurants/{itemName}/{pincode}")
	public ResponseEntity<List<Restaurant>> getRestaurantsByItemName(@PathVariable("itemName") String itemName,
			@PathVariable("pincode") String pincode) throws RestaurantException {
		return new ResponseEntity<>(resService.viewRestaurantByItemName(itemName, pincode), HttpStatus.FOUND);
	}
	
	@GetMapping("/restaurants/item/{itemId}")
	public ResponseEntity<Restaurant> getRestaurantsByItemId(@PathVariable("itemId") Integer itemId) throws RestaurantException, ItemException {
		return new ResponseEntity<>(resService.getRestaurantByItemId(itemId), HttpStatus.FOUND);
	}

	@GetMapping("/restaurants/status/{resId}")
	public ResponseEntity<MessageDTO> restaurantStatus(@PathVariable("resId") Integer restaurantId)
			throws RestaurantException {
		return new ResponseEntity<>(resService.restaurantStatus(restaurantId), HttpStatus.FOUND);

	}

	@PostMapping("/restaurants/customer/suggest_item/{pincode}")
	public ResponseEntity<MessageDTO> giveSuggestionsAboutItem(@RequestHeader("Authorization") String authorizationHeader,
			@Valid @RequestBody Suggestion suggestion, @PathVariable("pincode") String pincode)
			throws CustomerException, LoginException, RestaurantException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		return new ResponseEntity<>(resService.giveSuggestionAboutItem(jwtToken, suggestion, pincode),
				HttpStatus.ACCEPTED);

	}

	@GetMapping("/restaurants/suggestions")
	public ResponseEntity<List<Suggestion>> viewSuggestions(@RequestHeader("Authorization") String authorizationHeader)
			throws LoginException, RestaurantException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		return new ResponseEntity<>(resService.viewSuggestions(jwtToken), HttpStatus.FOUND);
	}

	@PutMapping("/restaurants/update_password")
	public ResponseEntity<MessageDTO> updateRestaurantPassword(@RequestHeader("Authorization") String authorizationHeader,
			@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) throws LoginException, RestaurantException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		ResponseEntity<MessageDTO> restaurantResponseEntity = new ResponseEntity<>(
				resService.updatepassword(jwtToken, resetPasswordDTO), HttpStatus.ACCEPTED);
		return restaurantResponseEntity;
	}
	
	@GetMapping("/restaurants/getAllRestaurant")
	public ResponseEntity<List<Restaurant>> getAllRestaurant() throws RestaurantException {
		ResponseEntity<List<Restaurant>> restaurantResponseEntity = new ResponseEntity<>(
				resService.getAllRestuList(), HttpStatus.ACCEPTED);
		return restaurantResponseEntity;
	}

	private String extractJwtToken(String authorizationHeader) {
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7);
		}
		return null;
	}
}
