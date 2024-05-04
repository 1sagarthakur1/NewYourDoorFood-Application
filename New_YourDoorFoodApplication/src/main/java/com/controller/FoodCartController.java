package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exception.CustomerException;
import com.exception.FoodCartException;
import com.exception.ItemException;
import com.exception.LoginException;
import com.exception.RestaurantException;
import com.exception.TokenException;
import com.model.FoodCart;
import com.model.ItemQuantityDTO;
import com.service.FoodCartService;

@RestController
@RequestMapping("/api/YourDoorFood")
public class FoodCartController {

	@Autowired
	private FoodCartService foodCartService;

	@PostMapping("/foodcarts/customer/addItem/{itemName}/{restaurantId}")
	public ResponseEntity<FoodCart> addItemToCartHandler(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("itemName") String itemName, @PathVariable("restaurantId") Integer restaurantId)
			throws FoodCartException, LoginException, ItemException, RestaurantException, CustomerException,
			TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		FoodCart cart = foodCartService.addItemToCart(jwtToken, itemName, restaurantId);
		return new ResponseEntity<FoodCart>(cart, HttpStatus.OK);
	}

	@PutMapping("/foodcarts/customer/increase_quantity/{itemName}/{quantity}")
	public ResponseEntity<FoodCart> increaseQuantityInCartHandler(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("itemName") String itemName, @PathVariable("quantity") Integer quantity)
			throws FoodCartException, LoginException, ItemException, CustomerException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		FoodCart cart = foodCartService.increaseQuantity(jwtToken, itemName, quantity);
		return new ResponseEntity<FoodCart>(cart, HttpStatus.OK);
	}

	@PutMapping("/foodcarts/customer/reduce_quantity/{itemName}/{quantity}")
	public ResponseEntity<FoodCart> reduceQuantityInCartHandler(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("itemName") String itemName, @PathVariable("quantity") Integer quantity)
			throws FoodCartException, LoginException, ItemException, CustomerException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		FoodCart cart = foodCartService.reduceQuantity(jwtToken, itemName, quantity);
		return new ResponseEntity<FoodCart>(cart, HttpStatus.OK);
	}

	@DeleteMapping("/foodcarts/customer/remove_item/{itemName}")
	public ResponseEntity<FoodCart> removeItemInCartHandler(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("itemName") String itemName) throws FoodCartException, CustomerException, LoginException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		FoodCart cart = foodCartService.removeItem(jwtToken, itemName);
		return new ResponseEntity<FoodCart>(cart, HttpStatus.ACCEPTED);
	}

	@DeleteMapping("/foodcarts/customer/clear_cart")
	public ResponseEntity<FoodCart> clearCartHandler(@RequestHeader("Authorization") String authorizationHeader)
			throws FoodCartException, CustomerException, LoginException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		FoodCart cart = foodCartService.clearCart(jwtToken);
		return new ResponseEntity<FoodCart>(cart, HttpStatus.ACCEPTED);
	}

	@GetMapping("/foodcarts/customer/view_cart")
	public ResponseEntity<List<ItemQuantityDTO>> viewCartHandler(@RequestHeader("Authorization") String authorizationHeader)
			throws LoginException, CustomerException, FoodCartException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		return new ResponseEntity<>(foodCartService.viewCart(jwtToken), HttpStatus.FOUND);
	}

	private String extractJwtToken(String authorizationHeader) {
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7);
		}
		return null;
	}
}
