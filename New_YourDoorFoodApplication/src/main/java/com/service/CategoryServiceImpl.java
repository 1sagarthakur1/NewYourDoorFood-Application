package com.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exception.CategoryException;
import com.model.Category;
import com.model.Item;
import com.repository.CategoryRepo;

@Service
public class CategoryServiceImpl implements  CategoryService{
	
	@Autowired
	private CategoryRepo categoryRepo;

	@Override
	public List<Item> getItemsByCategoryName(String categoryName, String pincode) throws CategoryException {
	    
	    Category category = categoryRepo.findByCategoryName(categoryName);
	    
	    if(category == null) throw new CategoryException("Category not found as: " + categoryName);
	    
	    List<Item> items = category.getItems();
	    
	    List<Item> filteredItems = new ArrayList<>();
	    
	    for(Item i : items) {
	    	if(i.getRestaurant().getAddress().getPincode().equals(pincode)) {
	    		filteredItems.add(i);
	    	}
	    }
	    
	    if(filteredItems.isEmpty()) throw new CategoryException("Item(s) not found in this category currently in your area");
	    
	    return filteredItems;
	}

}
