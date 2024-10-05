package com.ecommerce.models;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class Category {
 @Id
 @GeneratedValue(strategy=GenerationType.IDENTITY)
  private int id;
  private String name;
  private boolean isActive;
  private String file;
  
  

}
