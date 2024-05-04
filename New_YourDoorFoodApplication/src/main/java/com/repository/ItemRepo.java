 package com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.model.Category;
import com.model.Item;

@Repository
public interface ItemRepo extends JpaRepository<Item, Integer>{


	public List<Item> findByItemName(String itemName);
	
	public List<Item> findByCategory(Category category);
	
    public List<Item> findByRestaurantRestaurantId(Integer restaurantId);

}
