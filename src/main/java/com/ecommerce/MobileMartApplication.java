package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class MobileMartApplication {

	public static void main(String[] args) {
		SpringApplication.run(MobileMartApplication.class, args);
	}

}
