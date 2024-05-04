package com.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.config.JwtTokenGeneratorFilter;
import com.config.PasswordConverter;
import com.exception.CustomerException;
import com.exception.LoginException;
import com.model.CurrentUserSession;
import com.model.Customer;
import com.model.LoginDTO;
import com.model.TokenGiverDTO;
import com.repository.CustomerRepo;
import com.repository.SessionRepo;

@Service
public class CustomerLoginServiceImpl implements CustomerLoginService {

	@Autowired
	private SessionRepo sessionRepo;

	@Autowired
	private CustomerRepo customerRepo;

	@Autowired
	private PasswordConverter passwordConverter;

	@Autowired
	private JwtTokenGeneratorFilter jwtTokenGeneratorFilter;

	@Override
	public TokenGiverDTO login(LoginDTO dto) throws LoginException, CustomerException {

		Customer customer = customerRepo.findByMobileNumber(dto.getMobileNumber());

		if (customer == null)
			throw new CustomerException("Please enter a valid mobile number");

		if (!passwordConverter.verifyPassword(dto.getPassword(), customer.getPassword()))
			throw new LoginException("Invalid Password...");

		String token = jwtTokenGeneratorFilter.tokenGerneratorForCustomer(customer);

		TokenGiverDTO tokenGiverDTO = new TokenGiverDTO(token, LocalDateTime.now());

		return tokenGiverDTO;
	}

	@Override
	public String logout(String key) throws LoginException {

		CurrentUserSession currentUserSession = sessionRepo.findByUuid(key);

		if (currentUserSession == null)
			throw new LoginException("Invalid User key");

		sessionRepo.delete(currentUserSession);

		return "Logged out successfully";
	}

}
