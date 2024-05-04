package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exception.CategoryException;
import com.model.Item;
import com.service.CategoryService;

@RestController
@RequestMapping(value = "/api/yourDoorFood")
public class CategoryController {

	@Autowired
	private CategoryService iCategoryService;

	@GetMapping("/categories/view_all_items/{cateName}/{pincode}")
	public ResponseEntity<List<Item>> getItemsByCategoryName(@PathVariable("cateName") String categoryName,
			@PathVariable("pincode") String pincode) throws CategoryException {
		return new ResponseEntity<>(iCategoryService.getItemsByCategoryName(categoryName, pincode), HttpStatus.OK);

	}

}
