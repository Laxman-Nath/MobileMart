package com.ecommerce.models;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Sale {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String pName;
	private double price;
	private double discountPercent;
	private double discountedPrice;
	private String file;
	private LocalDate saleStartDate;
	private LocalDate saleEndDate;
	@OneToOne
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;
	@Override
	public String toString() {
		return "Sale [id=" + id + ", price=" + price + ", discountPercent=" + discountPercent + ", discountedPrice="
				+ discountedPrice + ", file=" + file + ", saleStartDate=" + saleStartDate + ", saleEndDate="
				+ saleEndDate + ", product=" + product + "]";
	}
	
}
