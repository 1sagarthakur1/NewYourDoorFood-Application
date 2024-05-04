package com.service;


import java.util.List;

import com.exception.CustomerException;
import com.exception.LoginException;
import com.exception.TokenException;
import com.model.Address;
import com.model.Customer;
import com.model.LoginDTO;
import com.model.MessageDTO;
import com.model.ResetPasswordDTO;

public interface CustomerService {
	
    public Customer addCustomer(Customer customer) throws CustomerException;
	
	public Customer updateCustomer(String token, Customer customer) throws CustomerException, LoginException, TokenException;
	
    public MessageDTO removeCustomer(String token, LoginDTO loginDTO)throws CustomerException, LoginException, TokenException;
	
	public Customer viewCustomer(String token)throws CustomerException, LoginException, TokenException;
	
	public List<Customer> getAllCustomers() throws CustomerException;
	
	public MessageDTO updateAddress(String token, Address address) throws CustomerException, LoginException, TokenException;
	
	public MessageDTO updatepassword(String token, ResetPasswordDTO resetPasswordDTO) throws CustomerException, LoginException, TokenException;
}
