package com.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.config.JwtTokenValidatorFilter;
import com.exception.CustomerException;
import com.exception.FoodCartException;
import com.exception.ItemException;
import com.exception.LoginException;
import com.exception.RestaurantException;
import com.exception.TokenException;
import com.model.Customer;
import com.model.FoodCart;
import com.model.Item;
import com.model.ItemQuantityDTO;
import com.model.Restaurant;
import com.repository.CustomerRepo;
import com.repository.FoodCartRepo;
import com.repository.ItemRepo;
import com.repository.RestaurantRepo;

import io.jsonwebtoken.Claims;

@Service
public class FoodCartServiceImpl implements FoodCartService {
	@Autowired
	private FoodCartRepo cartRepo;

	@Autowired
	private CustomerRepo customerRepo;

	@Autowired
	private RestaurantRepo restaurantRepo;

	@Autowired
	private ItemRepo itemRepo;
	
	@Autowired
	private JwtTokenValidatorFilter jwtTokenValidatorFilter;

	@Override
	public FoodCart addItemToCart(String token, String itemName, Integer restaurantId)
			throws FoodCartException, LoginException, ItemException, RestaurantException, CustomerException, TokenException {

		Restaurant restaurant = restaurantRepo.findById(restaurantId)
				.orElseThrow(() -> new RestaurantException("Restaurant not found"));
		List<Item> items = restaurant.getItems();
		Item item = null;
		for (Item i : items) {
			if (i.getItemName().equalsIgnoreCase(itemName)) {
				item = i;
				break;
			}
		}
		if (item == null || item.getQuantity() <= 0)
			throw new ItemException("Item not found in this restaurant or item is out of stock");

		Claims claims = jwtTokenValidatorFilter.tokenValidatingforCustomar(token);
		if (claims == null)
			throw new LoginException("Please login to add item to cart");
		Customer customer = customerRepo.findById(claims.get("customertId", Integer.class))
				.orElseThrow(() -> new CustomerException("Please login as Customer"));

		FoodCart foodCart = customer.getFoodCart();

		Map<Integer, Integer> itemsMap = foodCart.getItems();

		Integer itemId = item.getItemId();

		if (itemsMap.containsKey(itemId)) {
			itemsMap.put(itemId, itemsMap.get(itemId) + 1);
		} else {
			itemsMap.put(itemId, 1);
		}

		if (customer.getAddress() == null)
			throw new CustomerException("Please add address first");

		if (!restaurant.getAddress().getPincode().equals(customer.getAddress().getPincode())) {
			throw new CustomerException("This item is not deliverable in your area");
		}

		foodCart.setCustomer(customer);
		customer.setFoodCart(foodCart);

		return cartRepo.save(foodCart);
	}

	@Override
	public FoodCart increaseQuantity(String token, String itemName, int quantity)
			throws FoodCartException, LoginException, ItemException, CustomerException, TokenException {

		Claims claims = jwtTokenValidatorFilter.tokenValidatingforCustomar(token);
		if (claims == null)
			throw new LoginException("Please login to increase quantity");
		Customer customer = customerRepo.findById(claims.get("customertId", Integer.class))
				.orElseThrow(() -> new CustomerException("Please login as Customer"));

		FoodCart foodCart = customer.getFoodCart();
		Map<Integer, Integer> itemsMap = foodCart.getItems();
		Item item = null;

		for (Map.Entry<Integer, Integer> entry : itemsMap.entrySet()) {
			Item itemInCart = itemRepo.findById(entry.getKey()).get();
			if (itemInCart.getItemName().equalsIgnoreCase(itemName)) {
				item = itemInCart;
				break;
			}
		}

		if (item == null)
			throw new FoodCartException("Item is not available in the cart, please add the item first");

		Integer itemId = item.getItemId();

		if (item.getQuantity() >= (quantity + itemsMap.get(itemId)))
			itemsMap.put(itemId, itemsMap.get(itemId) + quantity);
		else
			throw new ItemException("Insufficient item quantity");

		return cartRepo.save(foodCart);
	}

	@Override
	public FoodCart reduceQuantity(String token, String itemName, int quantity)
			throws FoodCartException, LoginException, ItemException, CustomerException, TokenException {

		Claims claims = jwtTokenValidatorFilter.tokenValidatingforCustomar(token);
		if (claims == null)
			throw new LoginException("Please login to reduce quantity");
		Customer customer = customerRepo.findById(claims.get("customertId", Integer.class))
				.orElseThrow(() -> new CustomerException("Please login as Customer"));

		FoodCart foodCart = customer.getFoodCart();

		Map<Integer, Integer> itemsMap = foodCart.getItems();
		Item item = null;
		for (Map.Entry<Integer, Integer> entry : itemsMap.entrySet()) {
			Item itemInCart = itemRepo.findById(entry.getKey()).get();
			if (itemInCart.getItemName().equalsIgnoreCase(itemName)) {
				item = itemInCart;
				break;
			}
		}
		if (item == null)
			throw new FoodCartException("Item not found in your cart");

		Integer itemId = item.getItemId();

		if (item.getQuantity() == 0) {
			itemsMap.remove(itemId);
			cartRepo.save(foodCart);
			throw new ItemException(itemName + " is already out of stock and removed from your cart");
		}

		if (quantity < itemsMap.get(itemId))
			itemsMap.put(itemId, itemsMap.get(itemId) - quantity);
		else {
			itemsMap.remove(itemId);
			cartRepo.save(foodCart);
			throw new ItemException(item.getItemName() + " is removed from the cart");
		}

		return cartRepo.save(foodCart);
	}

	@Override
	public FoodCart removeItem(String token, String itemName)
			throws FoodCartException, CustomerException, LoginException, TokenException {

		Claims claims = jwtTokenValidatorFilter.tokenValidatingforCustomar(token);
		if (claims == null)
			throw new LoginException("Please login to remove item");
		Customer customer = customerRepo.findById(claims.get("customertId", Integer.class))
				.orElseThrow(() -> new CustomerException("Please login as Customer"));

		FoodCart foodCart = customer.getFoodCart();

		Map<Integer, Integer> itemsMap = foodCart.getItems();
		Item item = null;
		for (Map.Entry<Integer, Integer> entry : itemsMap.entrySet()) {
			Item itemInCart = itemRepo.findById(entry.getKey()).get();
			if (itemInCart.getItemName().equalsIgnoreCase(itemName)) {
				item = itemInCart;
				break;
			}
		}
		if (item == null)
			throw new FoodCartException(itemName + " not found in your cart");

		Integer itemId = item.getItemId();

		itemsMap.remove(itemId);

		return cartRepo.save(foodCart);
	}

	@Override
	public FoodCart clearCart(String token) throws FoodCartException, CustomerException, LoginException, TokenException {

		Claims claims = jwtTokenValidatorFilter.tokenValidatingforCustomar(token);
		if (claims == null)
			throw new LoginException("Please login to clear cart");
		Customer customer = customerRepo.findById(claims.get("customertId", Integer.class))
				.orElseThrow(() -> new CustomerException("Please login as Customer"));

		FoodCart foodCart = customer.getFoodCart();

		Map<Integer, Integer> itemsMap = foodCart.getItems();

		if (itemsMap.size() == 0)
			throw new FoodCartException("Cart is already empty");

		itemsMap.clear();

		return cartRepo.save(foodCart);
	}

	@Override
	public List<ItemQuantityDTO> viewCart(String token) throws LoginException, CustomerException, FoodCartException, TokenException {

		Claims claims = jwtTokenValidatorFilter.tokenValidatingforCustomar(token);
		if (claims == null)
			throw new LoginException("Please login to view your cart");
		Customer customer = customerRepo.findById(claims.get("customertId", Integer.class))
				.orElseThrow(() -> new CustomerException("Please login as Customer"));

		if (customer.getFoodCart().getItems().isEmpty())
			throw new FoodCartException("Item(s) not found in your cart");

		Map<Integer, Integer> itemsMap = customer.getFoodCart().getItems();

		List<ItemQuantityDTO> items = new ArrayList<>();

		for (Map.Entry<Integer, Integer> entry : itemsMap.entrySet()) {
			Item item = itemRepo.findById(entry.getKey()).get();
			ItemQuantityDTO dto = new ItemQuantityDTO(item.getItemId(), item.getItemName(), entry.getValue(),
					item.getCategory().getCategoryName(), item.getCost()*entry.getValue());
			items.add(dto);
		}

		return items;
	}

}
