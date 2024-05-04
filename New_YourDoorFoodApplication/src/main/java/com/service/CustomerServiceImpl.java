package com.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.config.JwtTokenValidatorFilter;
import com.config.PasswordConverter;
import com.exception.CustomerException;
import com.exception.LoginException;
import com.exception.TokenException;
import com.model.Address;
import com.model.Customer;
import com.model.LoginDTO;
import com.model.MessageDTO;
import com.model.ResetPasswordDTO;
import com.repository.CustomerRepo;

import io.jsonwebtoken.Claims;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepo customerRepo;

	@Autowired
	private PasswordConverter passwordConverter;

	@Autowired
	private JwtTokenValidatorFilter jwtTokenValidatorFilter;

	@Override
	public Customer addCustomer(Customer customer) throws CustomerException {
		Customer customerExist = customerRepo.findByMobileNumber(customer.getMobileNumber());
		if (customerExist != null)
			throw new CustomerException("Customer already registered with this mobile number");
		customer.setPassword(passwordConverter.hashPassword(customer.getPassword()));
		return customerRepo.save(customer);
	}

	@Override
	public Customer updateCustomer(String token, Customer customer)
			throws CustomerException, LoginException, TokenException {

		Claims claims = jwtTokenValidatorFilter.tokenValidatingforCustomar(token);
		if (claims == null)
			throw new LoginException("Please login to update your details");
		Customer existingCustomer = customerRepo.findById(claims.get("customertId", Integer.class))
				.orElseThrow(() -> new CustomerException("Please login as Customer"));

		if (customer.getAddress() != null)
			existingCustomer.setAddress(customer.getAddress());
		if (customer.getAge() != null)
			existingCustomer.setAge(customer.getAge());
		if (customer.getGender() != null)
			existingCustomer.setGender(customer.getGender());
		if (customer.getName() != null)
			existingCustomer.setName(customer.getName());
		if (customer.getEmail() != null)
			existingCustomer.setEmail(customer.getEmail());
		existingCustomer.setMobileNumber(customer.getMobileNumber());

		return customerRepo.save(existingCustomer);

	}

	@Override
	public MessageDTO removeCustomer(String token, LoginDTO loginDTO)
			throws CustomerException, LoginException, TokenException {

		Claims claims = jwtTokenValidatorFilter.tokenValidatingforCustomar(token);
		if (claims == null)
			throw new LoginException("Please login to remove customer");
		Customer existingCustomer = customerRepo.findById(claims.get("customertId", Integer.class))
				.orElseThrow(() -> new CustomerException("Please login as Customer"));

		if (!existingCustomer.getMobileNumber().equals(loginDTO.getMobileNumber()))
			throw new CustomerException("Enter valid username");

		if (passwordConverter.verifyPassword(loginDTO.getPassword(), existingCustomer.getPassword()))
			throw new CustomerException("Enter valid password");

		customerRepo.delete(existingCustomer);

		return new MessageDTO(LocalDateTime.now(), "Your account deleted successfully...");

	}

	@Override
	public Customer viewCustomer(String token) throws CustomerException, LoginException, TokenException {

		Claims claims = jwtTokenValidatorFilter.tokenValidatingforCustomar(token);
		if (claims == null)
			throw new LoginException("Please login to view your details");
		Customer existingCustomer = customerRepo.findById(claims.get("customertId", Integer.class))
				.orElseThrow(() -> new CustomerException("Please login as Customer"));

		return existingCustomer;

	}

	@Override
	public MessageDTO updatepassword(String token, ResetPasswordDTO resetPasswordDTO)
			throws CustomerException, LoginException, TokenException {

		String currentPassword = resetPasswordDTO.getCurrentPassword();
		String newPassword = resetPasswordDTO.getNewPassword();

		Claims claims = jwtTokenValidatorFilter.tokenValidatingforCustomar(token);
		if (claims == null)
			throw new LoginException("Please login to update your password");
		Customer existingCustomer = customerRepo.findById(claims.get("customertId", Integer.class))
				.orElseThrow(() -> new CustomerException("Please login as Customer"));

		if (!passwordConverter.verifyPassword(currentPassword, existingCustomer.getPassword()))
			throw new CustomerException("Enter vaild current password");

		existingCustomer.setPassword(passwordConverter.hashPassword(newPassword));

		customerRepo.save(existingCustomer);

		return new MessageDTO(LocalDateTime.now(), "Password updated sucssesfully");
	}

	@Override
	public MessageDTO updateAddress(String token, Address address)
			throws CustomerException, LoginException, TokenException {
		Claims claims = jwtTokenValidatorFilter.tokenValidatingforCustomar(token);
		if (claims == null)
			throw new LoginException("Please login to update your address");
		Customer customer = customerRepo.findById(claims.get("customertId", Integer.class))
				.orElseThrow(() -> new CustomerException("Please login as Customer"));

		customer.setAddress(address);
		customerRepo.save(customer);

		return new MessageDTO(LocalDateTime.now(), "Address updated sucssesfully");
	}

	@Override
	public List<Customer> getAllCustomers() throws CustomerException {

		List<Customer> list = customerRepo.findAll();
		if (list.isEmpty())
			throw new CustomerException("No any customer");
		return list;
	}

}
