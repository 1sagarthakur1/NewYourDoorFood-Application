package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exception.RestaurantException;
import com.model.Restaurant;

@Repository
public interface RestaurantRepo extends JpaRepository<Restaurant, Integer>{

	public Restaurant findByRestaurantName(String restaurantName);

	public Restaurant findByMobileNumber(String mobileNumer) throws RestaurantException;

}
