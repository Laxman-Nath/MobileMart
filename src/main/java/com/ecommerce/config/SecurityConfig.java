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

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private OAuthSuccessHandler oAuthSuccessHandler;

	@Bean
	UserDetailsService userDetailsService() {
		return new CustomCustomerDetailsService(); // Make sure this is your UserDetailsService implementation
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable());

		http.authorizeHttpRequests(auth -> auth.requestMatchers("/**", "/login").permitAll().requestMatchers("/user/**")
				.hasRole("USER").requestMatchers("/admin/**").hasRole("ADMIN"));
		http.oauth2Login(oauth -> oauth.loginPage("/login").successHandler(oAuthSuccessHandler));
		http.formLogin(form -> form.loginPage("/login").successHandler(customSuccessHandler)
				.loginProcessingUrl("/authenticate").failureHandler(customFailureHandler));
		http.rememberMe(r -> r.key("uniqueAndSecretKey").tokenValiditySeconds(86400).rememberMeParameter("remember-me")
				.userDetailsService(userDetailsService));

		http.logout(logout -> logout.permitAll());

		return http.build();
	}
}
