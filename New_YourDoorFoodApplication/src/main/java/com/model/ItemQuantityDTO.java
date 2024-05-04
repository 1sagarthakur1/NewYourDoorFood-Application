package com.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemQuantityDTO {

	private Integer itemId;
	private String itemName;
	private Integer orderedQuantity;
	private String category;
	private Double cost;
	
}
