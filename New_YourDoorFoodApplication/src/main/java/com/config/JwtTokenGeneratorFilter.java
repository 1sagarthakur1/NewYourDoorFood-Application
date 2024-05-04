package com.config;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.model.Customer;
import com.model.Restaurant;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtTokenGeneratorFilter {

	public String tokenGerneratorForRestaurant(Restaurant restaurant) {

		Date now = new Date();
		Date expirationDate = new Date(now.getTime() + 3600 * 1000); // 1 hour

		String token = Jwts.builder().setSubject(restaurant.getRestaurantName())
				.claim("restaurantId", restaurant.getRestaurantId()).claim("mobileNumber", restaurant.getMobileNumber())
				.claim("managerName", restaurant.getManagerName()).setIssuedAt(now).setExpiration(expirationDate)
				.signWith(SignatureAlgorithm.HS256, SecurityConstants.JWT_KEY_RESTAURANT).compact();

		// Print the generated token
		System.out.println(token);
		return token;
	}

	public String tokenGerneratorForCustomer(Customer customer) {

		Date now = new Date();
		Date expirationDate = new Date(now.getTime() + 3600 * 1000); // 1 hour

		// Generate the token
		String token = Jwts.builder().setSubject("customer")
				.claim("customertId", customer.getCustomerID()).claim("mobileNumber", customer.getMobileNumber())
				.claim("name", customer.getName()).setIssuedAt(now).setExpiration(expirationDate)
				.signWith(SignatureAlgorithm.HS256, SecurityConstants.JWT_KEY_CUSTOMER).compact();

		// Print the generated token
		System.out.println(token);
		return token;
	}

}
