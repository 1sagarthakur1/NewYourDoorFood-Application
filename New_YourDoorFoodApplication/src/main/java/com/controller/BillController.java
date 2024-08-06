package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exception.BillException;
import com.exception.CustomerException;
import com.exception.LoginException;
import com.exception.TokenException;
import com.model.Bill;
import com.model.DateDTO;
import com.service.BillService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/YourDoorFood")
@CrossOrigin(value = "*")
public class BillController {
	@Autowired
	private BillService billService;

	@GetMapping("/bills/customer/view_bill/{billId}")
	public ResponseEntity<Bill> viewBillHandler(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("billId") Integer billId)
			throws BillException, CustomerException, LoginException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		Bill bill = billService.viewBill(jwtToken, billId);
		return new ResponseEntity<Bill>(bill, HttpStatus.ACCEPTED);
	}

	@GetMapping("/bills/customer/view_bills_date_filtered")
	public ResponseEntity<List<Bill>> viewBillBetweenDateHandler(
			@RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody DateDTO dateDTO)
			throws BillException, CustomerException, LoginException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		List<Bill> bills = billService.viewBill(jwtToken, dateDTO);
		return new ResponseEntity<List<Bill>>(bills, HttpStatus.ACCEPTED);
	}

	@GetMapping("/bills/customer/view_all_bills")
	public ResponseEntity<List<Bill>> viewAllBillsHandler(@RequestHeader("Authorization") String authorizationHeader)
			throws BillException, LoginException, CustomerException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		List<Bill> bills = billService.viewBills(jwtToken);
		return new ResponseEntity<List<Bill>>(bills, HttpStatus.ACCEPTED);
	}

	@GetMapping("/bills/customer/get_total_cost_of_bill/{billId}")
	public ResponseEntity<Double> getTotalCostOfBillHandler(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("billId") Integer billId)
			throws BillException, CustomerException, LoginException, TokenException {
		String jwtToken = extractJwtToken(authorizationHeader);
		Double totalCost = billService.getTotalCost(jwtToken, billId);
		return new ResponseEntity<Double>(totalCost, HttpStatus.ACCEPTED);
	}

	private String extractJwtToken(String authorizationHeader) {
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7);
		}
		return null;
	}
}
