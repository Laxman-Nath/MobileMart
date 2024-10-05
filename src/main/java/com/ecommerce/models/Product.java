package com.ecommerce.models;

import jakarta.persistence.GeneratedValue;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

@Entity
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column(length = 100)
	private String name;

	// Description
	// body
	private String dimension;
	private String weight;
	private String build;
	private String sim;
	private String color;

	// display
	private String type;
	private String size;
	private String resolution;

	// performance
	private String os;
	private String chipSet;
	private String cpu;
	private String gpu;

	// Memory
	private String cardSlot;
	private String internal;

	// back camera
	private String bFeatures;
	private String bVideo;

	// front camera
	private String fFeatures;
	private String fVideo;

	// Sound
	private String loudSpeaker;
	private String sFeatures;

	// Battery
	private String bType;
	private String charging;

	// Connectivity
	private String cFeatures;

	// Security
	private String security;

	private String status;
	private String category;
	private String policy;
	private double price;
	private int quantity;
	private String file;
	private int discountPercent;
	private double discountedPrice;
	@OneToOne(mappedBy = "product")
	private Sale sale;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
	private List<Review> reviews;

	@OneToMany(mappedBy = "product")
	private List<CartProduct> cartProducts = new ArrayList<>();

	@OneToMany(mappedBy = "product")
	private List<OrderItem> orderItems;
}
