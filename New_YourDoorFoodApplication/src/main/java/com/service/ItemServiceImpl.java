package com.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.config.JwtTokenValidatorFilter;
import com.exception.CustomerException;
import com.exception.ItemException;
import com.exception.LoginException;
import com.exception.RestaurantException;
import com.exception.TokenException;
import com.model.Category;
import com.model.Customer;
import com.model.Item;
import com.model.Restaurant;
import com.repository.CategoryRepo;
import com.repository.CustomerRepo;
import com.repository.ItemRepo;
import com.repository.RestaurantRepo;

import io.jsonwebtoken.Claims;

@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private RestaurantRepo restaurantRepo;

	@Autowired
	private ItemRepo itemRepo;

	@Autowired
	private CategoryRepo categoryRepo;

	@Autowired
	private CustomerRepo customerRepo;

	@Autowired
	private JwtTokenValidatorFilter jwtTokenValidatorFilter;

	@Override
	public Item addItem(String token, Item item)
			throws ItemException, LoginException, RestaurantException, TokenException {
		Claims claims = jwtTokenValidatorFilter.tokenValidatingforRestaurant(token);
		if (claims == null)
			throw new RestaurantException("Token is not valid");
		Restaurant restaurant = restaurantRepo.findById(claims.get("restaurantId", Integer.class))
				.orElseThrow(() -> new RestaurantException("Please login as Restaurant"));

		if (item.getRestaurant() != null && restaurant.getRestaurantId() != item.getRestaurant().getRestaurantId())
			throw new RestaurantException("Item can not be added");

		List<Item> items = restaurant.getItems();
		for (Item i : items) {
			if (i.getItemName().equalsIgnoreCase(item.getItemName())) {
				throw new ItemException("Item already exist");
			}
		}

		String categoryName = item.getCategory().getCategoryName();

		List<Category> categories = categoryRepo.findAll();

		Category category = null;

		for (Category c : categories) {
			if (c.getCategoryName().equalsIgnoreCase(categoryName)) {
				category = c;
				break;
			}
		}

		if (category == null) {
			category = new Category();
			category.setCategoryName(categoryName);
		}

		category.getItems().add(item);
		item.setCategory(category);
		item.setRestaurant(restaurant);
		restaurant.getItems().add(item);

		return itemRepo.save(item);

	}

	@Override
	public Item updateItem(String token, Item item) throws ItemException, LoginException, RestaurantException, TokenException {

		Claims claims = jwtTokenValidatorFilter.tokenValidatingforRestaurant(token);
		if (claims == null)
			throw new RestaurantException("Token is not valid");
		Restaurant restaurant = restaurantRepo.findById(claims.get("restaurantId", Integer.class))
				.orElseThrow(() -> new RestaurantException("Please login as Restaurant"));


		if (item.getRestaurant() != null && restaurant.getRestaurantId() != item.getRestaurant().getRestaurantId())
			throw new RestaurantException("Item can not be added");

		List<Item> items = restaurant.getItems();

		Item verifiedItem = null;
		for (Item i : items) {
			if (i.getItemName().equalsIgnoreCase(item.getItemName())) {
				verifiedItem = i;
				break;
			}
		}
		

		if (verifiedItem == null) {
			throw new ItemException("This Item not present");
		}

		if (item.getCost() != null)
			verifiedItem.setCost(item.getCost());
		if (item.getQuantity() != null)
			verifiedItem.setQuantity(item.getQuantity());

		return itemRepo.save(verifiedItem);

	}

	@Override
	public Item viewItem(String itemName, Integer restaurantId) throws ItemException, RestaurantException {

		Restaurant restaurant = restaurantRepo.findById(restaurantId)
				.orElseThrow(() -> new RestaurantException("Restaurant not found"));

		List<Item> items = restaurant.getItems();

		for (Item i : items) {
			if (i.getItemName().equalsIgnoreCase(itemName) && i.getQuantity() > 0) {
				return i;
			}
		}

		throw new ItemException(itemName + " not found in the restaurant or currently out of stock");

	}


	@Override
	public List<Item> viewAllItemsByRestaurant(Integer restaurantId) throws ItemException, RestaurantException {

		Restaurant restaurant = restaurantRepo.findById(restaurantId)
				.orElseThrow(() -> new RestaurantException("Restaurant not found with id: " + restaurantId));

		List<Item> items = restaurant.getItems();

		if (items.isEmpty())
			throw new ItemException("Item(s) not found in this restaurant");

		return items;

	}

	@Override
	public Map<String, Item> viewItemsOnMyAddress(String token, String itemName)
			throws ItemException, RestaurantException, LoginException, CustomerException, TokenException {

		Claims claims = jwtTokenValidatorFilter.tokenValidatingforCustomar(token);
		if(claims == null)
			throw new LoginException("Please login to view item on address");
		Customer customer = customerRepo.findById(claims.get("customertId",Integer.class)).orElseThrow(()-> new CustomerException("Please login as Customer"));

		String pincode = customer.getAddress().getPincode();

		List<Restaurant> restaurants = restaurantRepo.findAll();

		if (restaurants.isEmpty())
			throw new RestaurantException("Restaurant(s) not found");

		List<Restaurant> filteredRestaurants = new ArrayList<>();

		for (Restaurant r : restaurants) {

			if (r.getAddress().getPincode().equals(pincode))
				filteredRestaurants.add(r);

		}
		if (filteredRestaurants.isEmpty())
			throw new RestaurantException("Restaurant(s) not found in your area");

		List<Item> items = new ArrayList<>();

		for (Restaurant r : filteredRestaurants) {
			List<Item> temp = r.getItems();
			if (temp == null)
				continue;

			for (Item i : temp) {
				if (i.getItemName().equalsIgnoreCase(itemName))
					items.add(i);
			}

		}

		if (items.isEmpty())
			throw new ItemException("Restaurant(s) not Found in your area with " + itemName);

		Map<String, Item> itemsMap = new HashMap<>();
		for (Item i : items)
			itemsMap.put(i.getRestaurant().getRestaurantName(), i);

		return itemsMap;

	}
}
