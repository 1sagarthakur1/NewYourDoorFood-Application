package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.model.FoodCart;


public interface FoodCartRepo extends JpaRepository<FoodCart, Integer>{

	


}
