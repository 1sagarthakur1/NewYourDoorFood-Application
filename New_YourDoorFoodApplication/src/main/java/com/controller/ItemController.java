package com.controller;

import java.util.List;
import java.util.Map;

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
import com.model.Item;
import com.service.ItemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/YourDoorFood")
@CrossOrigin(value = "*")
public class ItemController {

	@Autowired
	private ItemService iItemService;

	@PostMapping("/items/restaurant/addItem")
	public ResponseEntity<Item> addItemsHandler(@RequestHeader("Authorization") String authorizationHeader,
			@Valid @RequestBody Item item) throws ItemException, LoginException, RestaurantException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		Item addeditem = iItemService.addItem(jwtToken, item);

		return new ResponseEntity<>(addeditem, HttpStatus.ACCEPTED);
	}
    
	@PutMapping("/items/restaurant/update_item")
	public ResponseEntity<Item> updateItemsHandler(@RequestHeader("Authorization") String authorizationHeader,
			@Valid @RequestBody Item item) throws ItemException, LoginException, RestaurantException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		Item updateditem = iItemService.updateItem(jwtToken, item);
		return new ResponseEntity<>(updateditem, HttpStatus.OK);
	}

	@GetMapping("/items/restaurant/viewAll_Items/{restaurantId}")
	public ResponseEntity<List<Item>> viewAllItemsByRestaurantHandler(
			@PathVariable("restaurantId") Integer restaurantId) throws ItemException, RestaurantException {
		List<Item> items = iItemService.viewAllItemsByRestaurant(restaurantId);
		return new ResponseEntity<>(items, HttpStatus.OK);
	}

	@GetMapping("/items/get_item/{itemName}/{restaurantId}")
	public ResponseEntity<Item> viewItemHandler(@PathVariable("itemName") String itemName,
			@PathVariable("restaurantId") Integer restaurantId) throws ItemException, RestaurantException {
		Item item = iItemService.viewItem(itemName, restaurantId);
		return new ResponseEntity<>(item, HttpStatus.OK);
	}
	
	@GetMapping("/items/get_item")
	public ResponseEntity<List<Item>> viewAllItems() throws ItemException, RestaurantException {
		List<Item> item = iItemService.viewAllItems();
		return new ResponseEntity<>(item, HttpStatus.OK);
	}
	
	@GetMapping("/items/get_itemById/{itemId}")
	public ResponseEntity<Item> get_itemById(@PathVariable("itemId") Integer itemId) throws ItemException, RestaurantException {
		Item item = iItemService.getItemById(itemId);
		return new ResponseEntity<>(item, HttpStatus.OK);
	}

	@GetMapping("/items/customer/search_item_from_nearby_restaurants/{itemName}")
	public ResponseEntity<Map<String, Item>> viewItemsOnMyAddressHandler(
			@RequestHeader("Authorization") String authorizationHeader, @PathVariable("itemName") String itemName)
			throws ItemException, RestaurantException, LoginException, CustomerException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		Map<String, Item> items = iItemService.viewItemsOnMyAddress(jwtToken, itemName);

		return new ResponseEntity<>(items, HttpStatus.OK);
	}
	
	

	private String extractJwtToken(String authorizationHeader) {
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7);
		}
		return null;
	}
}
