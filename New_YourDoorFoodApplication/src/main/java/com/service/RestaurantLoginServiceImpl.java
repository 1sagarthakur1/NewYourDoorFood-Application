package com.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.config.JwtTokenGeneratorFilter;
import com.config.PasswordConverter;
import com.exception.LoginException;
import com.exception.RestaurantException;
import com.model.CurrentUserSession;
import com.model.LoginDTO;
import com.model.Restaurant;
import com.model.TokenGiverDTO;
import com.repository.RestaurantRepo;
import com.repository.SessionRepo;

@Service
public class RestaurantLoginServiceImpl implements RestaurantLoginService{

	@Autowired
	private SessionRepo sessionRepo;
	
	@Autowired
	private RestaurantRepo restaurantRepo;
	
	@Autowired
	private PasswordConverter passwordConverter;
	
	@Autowired
	private JwtTokenGeneratorFilter jwtTokenGeneratorFilter;
	
	@Override
	public TokenGiverDTO login(LoginDTO dto) throws LoginException, RestaurantException {
		
		Restaurant restaurant = restaurantRepo.findByMobileNumber(dto.getMobileNumber());
		
		if(restaurant==null) throw new RestaurantException("Please enter a valid mobile number");
		
		if(!passwordConverter.verifyPassword(dto.getPassword(), restaurant.getPassword())) throw new LoginException("Incorrect password");

		String token = jwtTokenGeneratorFilter.tokenGerneratorForRestaurant(restaurant);		
		TokenGiverDTO tokenGiverDTO = new TokenGiverDTO(token, LocalDateTime.now());		
		return tokenGiverDTO;
	}

	@Override
	public String logout(String key) throws LoginException {
		
		CurrentUserSession currentUserSession = sessionRepo.findByUuid(key);
		
		if(currentUserSession==null) throw new LoginException("Invalid User key");
		
		sessionRepo.delete(currentUserSession);
		
		return "Logged out successfully";
	}

}
