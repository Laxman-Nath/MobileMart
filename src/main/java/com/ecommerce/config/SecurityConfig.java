package com.ecommerce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	private CustomSuccessHandler customSuccessHandler;
	@Autowired
	private CustomFailureHandler customFailureHandler;

	@Bean
	UserDetailsService userDetailsService() {
		return new CustomCustomerDetailsService();
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider dProvider = new DaoAuthenticationProvider();
		dProvider.setUserDetailsService(userDetailsService());
		dProvider.setPasswordEncoder(passwordEncoder());
		return dProvider;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth.requestMatchers("/user/**").hasRole("USER")
						.requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/**").permitAll())
				.formLogin(form -> form.loginPage("/login").successHandler(customSuccessHandler)
						.loginProcessingUrl("/authenticate").failureHandler(customFailureHandler))
				.logout(logout -> logout.permitAll());
		return http.build();
	}
}
