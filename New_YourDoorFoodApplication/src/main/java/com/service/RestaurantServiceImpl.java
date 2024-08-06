package com.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.config.JwtTokenValidatorFilter;
import com.config.PasswordConverter;
import com.exception.CustomerException;
import com.exception.ItemException;
import com.exception.LoginException;
import com.exception.RestaurantException;
import com.exception.TokenException;
import com.model.CurrentUserSession;
import com.model.Customer;
import com.model.Item;
import com.model.MessageDTO;
import com.model.ResetPasswordDTO;
import com.model.Restaurant;
import com.model.Suggestion;
import com.repository.CustomerRepo;
import com.repository.ItemRepo;
import com.repository.RestaurantRepo;
import com.repository.SessionRepo;

import io.jsonwebtoken.Claims;

@Service
public class RestaurantServiceImpl implements RestaurantService {

	@Autowired
	private RestaurantRepo restaurantRepo;

	@Autowired
	private SessionRepo sessionRepo;

	@Autowired
	private CustomerRepo customerRepo;
	
	@Autowired
	private ItemRepo itemRepo;

	@Autowired
	private PasswordConverter passwordConverter;

	@Autowired
	private JwtTokenValidatorFilter jwtTokenValidatorFilter;

	@Override
	public Restaurant addRestaurant(Integer verificationId, Restaurant restaurant) throws RestaurantException {

		List<CurrentUserSession> list = sessionRepo.findAll();
		for (CurrentUserSession c : list) {
			sessionRepo.delete(c);
		}
		if (verificationId != 8080)
			throw new RestaurantException("Enter vaild verification id");
		Restaurant restaurantExist = restaurantRepo.findByMobileNumber(restaurant.getMobileNumber());
		if (restaurantExist != null)
			throw new RestaurantException("Mobile number already registered");

		List<Restaurant> restaurants = restaurantRepo.findAll();

		for (Restaurant r : restaurants) {
			if (r.getAddress().getPincode().equals(restaurant.getAddress().getPincode())
					&& r.getRestaurantName().equals(restaurant.getRestaurantName())) {
				throw new RestaurantException("Restaurant  with this name is already present in your area");
			}
		}

		restaurant.setPassword(passwordConverter.hashPassword(restaurant.getPassword()));

		return restaurantRepo.save(restaurant);

	}

	@Override
	public Restaurant updateRestaurant(String token, Restaurant updatedRestaurant)
			throws RestaurantException, LoginException, TokenException {

		Claims claims = jwtTokenValidatorFilter.tokenValidatingforRestaurant(token);
		if (claims == null) {
			throw new RestaurantException("Token is not valid");
		}
		Restaurant restaurant = restaurantRepo.findById(claims.get("restaurantId", Integer.class))
				.orElseThrow(() -> new RestaurantException("Please login as Restaurant"));

		List<Restaurant> restaurants = restaurantRepo.findAll();

		for (Restaurant r : restaurants) {
			if (r.equals(restaurant))
				continue;
			if (r.getAddress().getPincode().equals(updatedRestaurant.getAddress().getPincode())
					&& r.getRestaurantName().equalsIgnoreCase(updatedRestaurant.getRestaurantName())) {
				throw new RestaurantException(
						"Can't change restaurant name, restaurant with this name is already present in your area");
			}
		}

		if (updatedRestaurant.getRestaurantName() != null)
			restaurant.setRestaurantName(updatedRestaurant.getRestaurantName());
		if (updatedRestaurant.getManagerName() != null)
			restaurant.setManagerName(updatedRestaurant.getManagerName());
		if (updatedRestaurant.getOpenTime() != null)
			restaurant.setOpenTime(updatedRestaurant.getOpenTime());
		if (updatedRestaurant.getCloseTime() != null)
			restaurant.setCloseTime(updatedRestaurant.getCloseTime());
		if (updatedRestaurant.getEmail() != null)
			restaurant.setEmail(updatedRestaurant.getEmail());
		restaurant.setMobileNumber(updatedRestaurant.getMobileNumber());
		restaurant.setAddress(updatedRestaurant.getAddress());
		return restaurantRepo.save(restaurant);
	}

	@Override
	public Restaurant viewRestaurant(Integer restaurantId) throws RestaurantException {
		Restaurant restaurant = restaurantRepo.findById(restaurantId)
				.orElseThrow(() -> new RestaurantException("Restaurant not found with the id: " + restaurantId));

		return restaurant;

	}

	@Override
	public MessageDTO restaurantStatus(Integer restaurantId) throws RestaurantException {
		Restaurant restaurant = restaurantRepo.findById(restaurantId)
				.orElseThrow(() -> new RestaurantException("Restaurant not found with the id: " + restaurantId));

		if (LocalTime.now().isAfter(restaurant.getCloseTime()) || LocalTime.now().isBefore(restaurant.getOpenTime()))
			return new MessageDTO(LocalDateTime.now(), "Closed");

		return new MessageDTO(LocalDateTime.now(), "Open");

	}

	@Override
	public List<Restaurant> viewNearByRestaurant(String cityName, String pincode) throws RestaurantException {
		List<Restaurant> nearByRestaurants = new ArrayList<>();

		List<Restaurant> restaurants = restaurantRepo.findAll();

		for (Restaurant r : restaurants) {
			if (r.getAddress().getCity().equalsIgnoreCase(cityName) && r.getAddress().getPincode().equals(pincode)) {
				nearByRestaurants.add(r);
			}
		}

		if (nearByRestaurants.isEmpty())
			throw new RestaurantException("Restaurant(s) not found in your area");

		return nearByRestaurants;

	}

	@Override
	public List<Restaurant> viewRestaurantByItemName(String itemname, String pincode) throws RestaurantException {

		List<Restaurant> nearByRestaurants = new ArrayList<>();
		List<Restaurant> restaurants = restaurantRepo.findAll();

		for (Restaurant r : restaurants) {
			if (r.getAddress().getPincode().equals(pincode)) {
				nearByRestaurants.add(r);
			}
		}
		if (nearByRestaurants.isEmpty())
			throw new RestaurantException("Restaurant(s) not found in your area with " + itemname);

		List<Restaurant> filteredRestaurants = new ArrayList<>();
		for (Restaurant r : nearByRestaurants) {
			List<Item> items = r.getItems();
			for (Item i : items) {
				if (i.getItemName().equalsIgnoreCase(itemname) && i.getQuantity() > 0) {
					filteredRestaurants.add(r);
				}
			}
		}

		if (filteredRestaurants.isEmpty())
			throw new RestaurantException("Restaurant(s) not found in your area currently serving " + itemname
					+ ". You can give suggestion to add your dish.");

		return filteredRestaurants;

	}

	@Override
	public MessageDTO giveSuggestionAboutItem(String token, Suggestion suggestion, String pincode)
			throws CustomerException, LoginException, RestaurantException, TokenException {

//		CurrentUserSession currentUserSession = sessionRepo.findByUuid(key);
		
		Claims claims = jwtTokenValidatorFilter.tokenValidatingforCustomar(token);
		if (claims == null) {
			throw new CustomerException("Please login to suggest your dish");
		}
		
		Customer customer = customerRepo.findById(claims.get("customertId",Integer.class))
				.orElseThrow(() -> new CustomerException("Please login as Customer"));

		List<Restaurant> nearByRestaurants = new ArrayList<>();
		List<Restaurant> restaurants = restaurantRepo.findAll();

		for (Restaurant r : restaurants) {
			if (r.getAddress().getPincode().equals(pincode)) {
				nearByRestaurants.add(r);
			}
		}
		if (nearByRestaurants.isEmpty())
			throw new RestaurantException("Restaurant(s) not found in your area");

		for (Restaurant r : nearByRestaurants) {
			List<Item> items = r.getItems();
			for (Item i : items) {
				if (i.getItemName().equalsIgnoreCase(suggestion.getItemName()) && i.getQuantity() > 0) {
					throw new RestaurantException(suggestion.getItemName() + " is already present in your area");
				}
			}
		}

		for (Restaurant r : nearByRestaurants) {
			r.getSuggestions().add(suggestion);
			restaurantRepo.save(r);
		}
		return new MessageDTO(LocalDateTime.now(), "Thankyou " + customer.getName() + ", for the suggestion of " + suggestion.getItemName());
	}

	@Override
	public List<Suggestion> viewSuggestions(String token) throws LoginException, RestaurantException, TokenException {

		Claims claims = jwtTokenValidatorFilter.tokenValidatingforRestaurant(token);
		if (claims == null) {
			throw new RestaurantException("Please login view suggestions");
		}
		Restaurant restaurant = restaurantRepo.findById(claims.get("restaurantId",Integer.class))
				.orElseThrow(() -> new RestaurantException("Please login as Restaurant"));

		List<Suggestion> suggestions = restaurant.getSuggestions();
		if (suggestions.isEmpty())
			throw new RestaurantException("Suggestions not found");
		
		return suggestions;

	}

	@Override
	public MessageDTO updatepassword(String token, ResetPasswordDTO resetPasswordDTO)
			throws RestaurantException, LoginException, TokenException {

		String currentPassword = resetPasswordDTO.getCurrentPassword();
		String newPassword = resetPasswordDTO.getNewPassword();
		
		Claims claims = jwtTokenValidatorFilter.tokenValidatingforRestaurant(token);
		if (claims == null) {
			throw new RestaurantException("Please login to change password");
		}
		
		Restaurant restaurant = restaurantRepo.findById(claims.get("restaurantId",Integer.class))
				.orElseThrow(() -> new RestaurantException("Please login as Restaurant"));
				

		if (!passwordConverter.verifyPassword(currentPassword, restaurant.getPassword()))
			throw new RestaurantException("Invaild current password");

		restaurant.setPassword(passwordConverter.hashPassword(newPassword));
		restaurantRepo.save(restaurant);
		
		return new MessageDTO(LocalDateTime.now(), "Password updated sucssesfully");

	}

	@Override
	public Restaurant getRestaurantByItemId(Integer itemId) throws RestaurantException, ItemException {
		Optional<Item> item = itemRepo.findById(itemId);
		
		if(item.isEmpty()) {
			throw new ItemException("Item is not present with"+ itemId +"id");
		}
		
		Restaurant restaurant = item.get().getRestaurant();
		if(restaurant == null) {
			throw new ItemException("Resturant is not present");
		}
		return restaurant;
	}

	@Override
	public List<Restaurant> getAllRestuList() throws RestaurantException {
		// TODO Auto-generated method stub
		List<Restaurant> list = restaurantRepo.findAll();
		if(list.isEmpty()) {
			throw new RestaurantException("No any Restaurant"); 
		}
		return list;
	}
	

}
