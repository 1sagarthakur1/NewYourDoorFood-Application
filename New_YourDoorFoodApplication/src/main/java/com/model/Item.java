package com.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
public class Item {

	@Id
	@JsonProperty(access = Access.READ_ONLY)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itemGenrator")
	@SequenceGenerator(name = "itemGenrator", sequenceName = "itemgen", allocationSize = 1, initialValue = 11)
	private Integer itemId;

	@NotNull(message = "itemName should not be null")
	@NotBlank(message = "itemName should not be blank")
	@NotEmpty(message = "itemName should not be empty")
	private String itemName;

	@NotNull(message = "itemName should not be null")
	@NotBlank(message = "itemName should not be blank")
	@NotEmpty(message = "itemName should not be empty")
	private String itemImage;
	
	@NotNull(message = "category should not be null")
	@ManyToOne(cascade = CascadeType.ALL)
	private Category category;
	
	@NotNull(message = "category should not be null")
	private String description;

	@NotNull(message = "quantity should not be null")
	private Integer quantity;

	@NotNull(message = "cost should not be null")
	private Double cost;

//	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL)
	private Restaurant restaurant;
}
