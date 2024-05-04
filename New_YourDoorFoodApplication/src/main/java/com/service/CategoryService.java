package com.service;

import java.util.List;

import com.exception.CategoryException;
import com.model.Item;

public interface CategoryService {
	
	public List<Item> getItemsByCategoryName(String categoryName, String pincode) throws CategoryException;
	
}
