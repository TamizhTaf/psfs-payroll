package com.psfs.service.security;

import com.psfs.service.api.util.ServiceUtil;
import com.psfs.service.security.jwt.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain; 
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

	private final UserDetailsServiceImpl userDetailsService;
	private final JwtRequestFilter jwtRequestFilter;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, JwtRequestFilter jwtRequestFilter) {
		this.userDetailsService = userDetailsService;
		this.jwtRequestFilter = jwtRequestFilter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf((csrf) -> {
			csrf.disable();
		});
		 	
		http.authorizeHttpRequests((auth) -> {
			auth.requestMatchers("/api/**").authenticated()  // API endpoints require authentication
			.requestMatchers("/liveness/**").permitAll()  
			.requestMatchers("/api/auth/register/**").permitAll() 
			.requestMatchers("/api/auth/signin/**").permitAll() 
			.requestMatchers("/api/auth/signout/**").permitAll() // Liveness endpoints are public
			.anyRequest().authenticated();  // All other requests require authentication
		});


		http.sessionManagement((sess) -> {
			sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		});

		http.addFilterBefore(this.jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(this.userDetailsService);
		provider.setPasswordEncoder(ServiceUtil.passwordEncoder());
		return provider;
	}

	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}