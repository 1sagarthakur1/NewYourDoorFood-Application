package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exception.CustomerException;
import com.exception.LoginException;
import com.exception.TokenException;
import com.model.Address;
import com.model.Customer;
import com.model.LoginDTO;
import com.model.MessageDTO;
import com.model.ResetPasswordDTO;
import com.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/YourDoorFood")
@CrossOrigin(value = "*")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@PostMapping("/customers/create_account")
	public ResponseEntity<Customer> addCustomer(@Valid @RequestBody Customer customer) throws CustomerException {
		ResponseEntity<Customer> customerResponseEntity = new ResponseEntity<>(customerService.addCustomer(customer),
				HttpStatus.CREATED);
		return customerResponseEntity;
	}

	@PutMapping("/customers/update_basic_details")
	public ResponseEntity<Customer> updateCustomerDetails(@RequestHeader("Authorization") String authorizationHeader,
			@Valid @RequestBody Customer customer) throws CustomerException, LoginException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		ResponseEntity<Customer> customerResponseEntity = new ResponseEntity<>(
				customerService.updateCustomer(jwtToken, customer), HttpStatus.ACCEPTED);
		return customerResponseEntity;
	}

	@DeleteMapping("/customers/delete_account")
	public ResponseEntity<MessageDTO> deleteCustomerByid(@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody LoginDTO loginDTO) throws CustomerException, LoginException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		ResponseEntity<MessageDTO> customerResponseEntity = new ResponseEntity<>(
				customerService.removeCustomer(jwtToken, loginDTO), HttpStatus.ACCEPTED);
		return customerResponseEntity;
	}

	@GetMapping("/customers/view_profile")
	public ResponseEntity<Customer> findCustomer(@RequestHeader("Authorization") String authorizationHeader)
			throws CustomerException, LoginException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		ResponseEntity<Customer> customerResponseEntity = new ResponseEntity<>(customerService.viewCustomer(jwtToken),
				HttpStatus.ACCEPTED);
		return customerResponseEntity;
	}
	
	@GetMapping("/customers/get_all_cutomers")
	public ResponseEntity<List<Customer>> findAllCustomers()
			throws CustomerException{
		ResponseEntity<List<Customer>> customerResponseEntity = new ResponseEntity<>(customerService.getAllCustomers(),
				HttpStatus.ACCEPTED);
		return customerResponseEntity;
	}

	@PutMapping("/customers/update_address")
	public ResponseEntity<MessageDTO> updateCustomerAddress(@RequestHeader("Authorization") String authorizationHeader,
			@Valid @RequestBody Address address) throws CustomerException, LoginException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		ResponseEntity<MessageDTO> customerResponseEntity = new ResponseEntity<>(
				customerService.updateAddress(jwtToken, address), HttpStatus.ACCEPTED);
		return customerResponseEntity;
	}

	@PutMapping("/customers/update_password")
	public ResponseEntity<MessageDTO> updateCustomerPassword(@RequestHeader("Authorization") String authorizationHeader,
			@Valid @RequestBody ResetPasswordDTO resetPasswordDTO)
			throws CustomerException, LoginException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		ResponseEntity<MessageDTO> customerResponseEntity = new ResponseEntity<>(
				customerService.updatepassword(jwtToken, resetPasswordDTO), HttpStatus.ACCEPTED);
		return customerResponseEntity;
	}

	private String extractJwtToken(String authorizationHeader) {
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7);
		}
		return null;
	}
}
