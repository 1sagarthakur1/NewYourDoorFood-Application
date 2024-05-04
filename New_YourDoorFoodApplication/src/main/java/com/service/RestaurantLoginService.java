package com.service;

import com.exception.LoginException;
import com.exception.RestaurantException;
import com.model.LoginDTO;
import com.model.TokenGiverDTO;

public interface RestaurantLoginService {

	public TokenGiverDTO login(LoginDTO dto) throws LoginException, RestaurantException;
	
	public String logout(String key) throws LoginException;
	
}
