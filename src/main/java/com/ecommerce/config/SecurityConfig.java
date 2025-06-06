package com.ecommerce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.mail.Session;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
				.hasRole("USER").requestMatchers("/admin/**").hasRole("ADMIN").anyRequest().authenticated());
		http.exceptionHandling(exceptionHandling -> exceptionHandling.accessDeniedPage("/error"));
		http.oauth2Login(oauth -> oauth.loginPage("/login").successHandler(oAuthSuccessHandler));
		http.formLogin(form -> form.loginPage("/login").successHandler(customSuccessHandler)
				.loginProcessingUrl("/authenticate").failureHandler(customFailureHandler));
		http.rememberMe(r -> r.key("uniqueAndSecretKey").tokenValiditySeconds(86400).rememberMeParameter("remember-me")
				.userDetailsService(userDetailsService));

		// Logout configuration
		http.logout(logout -> logout.permitAll().invalidateHttpSession(true).clearAuthentication(true)
				.deleteCookies("JSESSIONID"));

		// Session management configuration
		http.sessionManagement(s -> s.sessionFixation().migrateSession());

		return http.build();
	}
}
