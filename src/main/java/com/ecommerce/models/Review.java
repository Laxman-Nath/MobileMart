package com.ecommerce.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Review {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private int rating;
	private String comment;
	@ManyToOne
	@JoinColumn(name="product_id",nullable=false)
	private Product product;
	@ManyToOne
	@JoinColumn(name="customer_id",nullable=false)
	private Customer customer;
	private LocalDate date;
}
