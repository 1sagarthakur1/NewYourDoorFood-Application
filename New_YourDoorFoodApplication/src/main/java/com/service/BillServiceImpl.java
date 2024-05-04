package com.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.config.JwtTokenValidatorFilter;
import com.exception.BillException;
import com.exception.CustomerException;
import com.exception.LoginException;
import com.exception.TokenException;
import com.model.Bill;
import com.model.Customer;
import com.model.DateDTO;
import com.model.ItemQuantityDTO;
import com.model.OrderDetails;
import com.repository.BillRepo;
import com.repository.CustomerRepo;
import com.repository.OrderDetailsRepo;

import io.jsonwebtoken.Claims;

@Service
public class BillServiceImpl implements BillService {

	@Autowired
	private BillRepo billRepo;

	@Autowired
	private CustomerRepo customerRepo;

	@Autowired
	private OrderDetailsRepo orderDetailsRepo;

	@Autowired
	private JwtTokenValidatorFilter jwtTokenValidatorFilter;

	@Override
	public Bill genrateBill(OrderDetails orderDetails) throws BillException {
		Bill bill = new Bill();

		bill.setBillDate(LocalDateTime.now());
		bill.setOrderDetails(orderDetails);
		bill.setGrandTotal(orderDetails.getTotalAmount() + bill.getDeliveryCost());

		Integer totalItems = 0;
		for (ItemQuantityDTO e : orderDetails.getItems()) {
			totalItems += e.getOrderedQuantity();
		}
		bill.setTotalItems(totalItems);

		return billRepo.save(bill);
	}

	@Override
	public Bill viewBill(String token, Integer billId)
			throws BillException, CustomerException, LoginException, TokenException {

		Claims claims = jwtTokenValidatorFilter.tokenValidatingforCustomar(token);
		if (claims == null)
			throw new LoginException("Please login to view your bill");
		Customer customer = customerRepo.findById(claims.get("customertId", Integer.class))
				.orElseThrow(() -> new CustomerException("Please login as Customer"));

		Bill bill = billRepo.findById(billId).orElseThrow(() -> new BillException("Bill not found"));

		if (bill.getOrderDetails().getCustomerId() != customer.getCustomerID())
			throw new BillException("Bill not found");

		return bill;
	}

	@Override
	public List<Bill> viewBill(String token, DateDTO dateDTO) throws BillException, CustomerException, LoginException, TokenException {
		LocalDate startDate = dateDTO.getStartDate();
		LocalDate endDate = dateDTO.getEndDate();

		Claims claims = jwtTokenValidatorFilter.tokenValidatingforCustomar(token);
		if (claims == null)
			throw new LoginException("Please login to view bill(s)");
		Customer customer = customerRepo.findById(claims.get("customertId", Integer.class))
				.orElseThrow(() -> new CustomerException("Please login as Customer"));

		List<OrderDetails> orderDetails = orderDetailsRepo.findByCustomerId(customer.getCustomerID());

		if (orderDetails.isEmpty())
			throw new BillException("Bill(s) not found");

		List<OrderDetails> filteredOrders = new ArrayList<>();
		for (OrderDetails o : orderDetails) {
			LocalDate date = o.getOrderDate().toLocalDate();

			if (date.isEqual(endDate) || date.isEqual(startDate)
					|| (date.isAfter(startDate) && date.isBefore(endDate))) {
				filteredOrders.add(o);
			}
		}
		if (filteredOrders.isEmpty())
			throw new BillException("Bill(s) not found within these dates");

		List<Bill> bills = new ArrayList<>();
		for (OrderDetails o : filteredOrders) {
			bills.add(o.getBill());
		}

		return bills;
	}

	@Override
	public List<Bill> viewBills(String token) throws BillException, LoginException, CustomerException, TokenException {
		
		Claims claims = jwtTokenValidatorFilter.tokenValidatingforCustomar(token);
		if (claims == null)
			throw new LoginException("Please login to view bill(s)");
		Customer customer = customerRepo.findById(claims.get("customertId", Integer.class))
				.orElseThrow(() -> new CustomerException("Please login as Customer"));

		List<OrderDetails> orderDetails = orderDetailsRepo.findByCustomerId(customer.getCustomerID());
		if (orderDetails.isEmpty())
			throw new BillException("Bill(s) not found");

		List<Bill> bills = new ArrayList<>();
		for (OrderDetails o : orderDetails) {
			bills.add(o.getBill());
		}

		return bills;
	}

	@Override
	public Double getTotalCost(String token, Integer billId) throws BillException, CustomerException, LoginException, TokenException {
		
		Claims claims = jwtTokenValidatorFilter.tokenValidatingforCustomar(token);
		if (claims == null)
			throw new LoginException("Please login to view total cost of your order");
		Customer customer = customerRepo.findById(claims.get("customertId", Integer.class))
				.orElseThrow(() -> new CustomerException("Please login as Customer"));

		Bill bill = billRepo.findById(billId).orElseThrow(() -> new BillException("Bill not found"));
		if (bill.getOrderDetails().getCustomerId() != customer.getCustomerID())
			throw new BillException("Bill not found");

		return bill.getGrandTotal();
	}
}
