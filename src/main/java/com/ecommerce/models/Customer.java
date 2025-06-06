package com.ecommerce.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Setter;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String name;
	private long mnumber;
	private String email;
	private String state;
	private String district;
	private String muncipility;
	private int ward;
	private String tole;
	private String password;
	private String cpassword;
	private String file;
	@OneToMany(mappedBy = "customer")
	private List<Review> reviews;
	private String role;
	private boolean isEnable;
	private boolean isAccountNonLocked;
	private int failedAttempt;
	private Date lockedTime;
	private String code;
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
	private List<Cart> carts=new ArrayList<>();
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
	private List<Order> orders=new ArrayList<>();
	private String provider;
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Product> products=new ArrayList<>();

}
