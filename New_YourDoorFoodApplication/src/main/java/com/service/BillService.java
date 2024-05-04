package com.service;

import java.util.List;

import com.exception.BillException;
import com.exception.CustomerException;
import com.exception.LoginException;
import com.exception.TokenException;
import com.model.Bill;
import com.model.DateDTO;
import com.model.OrderDetails;



public interface BillService {
	
	public Bill genrateBill(OrderDetails orderDetails) throws BillException;
	
	public Bill viewBill(String token, Integer billId) throws BillException, CustomerException, LoginException, TokenException;
	
	public List<Bill> viewBill(String token, DateDTO dateDTO) throws BillException, CustomerException, LoginException, TokenException;
	
	public List<Bill> viewBills(String token) throws BillException, LoginException, CustomerException, TokenException;
	
	public Double getTotalCost(String token, Integer billId) throws BillException, CustomerException, LoginException, TokenException;
}
