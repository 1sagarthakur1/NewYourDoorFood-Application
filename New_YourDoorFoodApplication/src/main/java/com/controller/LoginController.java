package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exception.CustomerException;
import com.exception.LoginException;
import com.exception.RestaurantException;
import com.exception.TokenException;
import com.model.LoginDTO;
import com.model.TokenGiverDTO;
import com.service.CustomerLoginService;
import com.service.RestaurantLoginService;

@RestController
@RequestMapping("/api/YourDoorFood")
@CrossOrigin(value = "*")
public class LoginController {

	@Autowired
	private CustomerLoginService customerLoginService;
	
	@Autowired
	private RestaurantLoginService restaurantLoginService;
	
	@PostMapping("/login/customer/login")
	public ResponseEntity<TokenGiverDTO> customerLoginHandler(@RequestBody LoginDTO dto) throws LoginException, CustomerException, TokenException{
		TokenGiverDTO tokenGiverDTO = customerLoginService.login(dto);
		return new ResponseEntity<>(tokenGiverDTO, HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/login/restaurant/login")
	public ResponseEntity<TokenGiverDTO> restaurantLoginHandler(@RequestBody LoginDTO dto) throws LoginException, RestaurantException{
		TokenGiverDTO tokenGiverDTO = restaurantLoginService.login(dto);
		return new ResponseEntity<>(tokenGiverDTO, HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/logout/user/{key}")
	public ResponseEntity<String> customerLogoutHandler(@PathVariable("key") String key) throws LoginException{
		
		String result = customerLoginService.logout(key);
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
}
