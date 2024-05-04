package com.service;

import com.exception.CustomerException;
import com.exception.LoginException;
import com.exception.TokenException;
import com.model.LoginDTO;
import com.model.TokenGiverDTO;

public interface CustomerLoginService {

	public TokenGiverDTO login(LoginDTO dto) throws LoginException, CustomerException, TokenException;
	
	public String logout(String key) throws LoginException;
	
}
