package com.zxy.auth.service;

import com.zxy.security.config.AuthTokenProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.LinkedHashSet;
import java.util.List;

@Configuration
@EnableWebSecurity
public class AuthSecurityConfiguration {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService,
													 PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}

	@Bean
	public AuthenticationManager authenticationManager(DaoAuthenticationProvider provider) {
		return new ProviderManager(provider);
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource(AuthTokenProperties properties) {
		AuthTokenProperties.Cors cors = properties.getCors();
		if (cors.isAllowCredentials() && cors.getAllowedOrigins().contains("*")) {
			throw new IllegalStateException("启用凭据 CORS 时不允许使用通配符 Origin");
		}
		LinkedHashSet<String> allowedHeaders = new LinkedHashSet<>(cors.getAllowedHeaders());
		allowedHeaders.add(properties.getCsrf().getHeaderName());
		allowedHeaders.add(properties.getAuth().getClientTypeHeader());
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(cors.getAllowedOrigins());
		configuration.setAllowedMethods(cors.getAllowedMethods());
		configuration.setAllowedHeaders(List.copyOf(allowedHeaders));
		configuration.setExposedHeaders(cors.getExposedHeaders());
		configuration.setAllowCredentials(cors.isAllowCredentials());
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		cors.getPathWhitelist().forEach(path -> source.registerCorsConfiguration(path, configuration));
		return source;
	}

	@Bean
	public SecurityFilterChain authSecurityFilterChain(HttpSecurity http,
												 DaoAuthenticationProvider provider) throws Exception {
		http
				.authenticationProvider(provider)
				.csrf(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/api/auth/login", "/api/auth/refresh", "/actuator/**", "/error").permitAll()
						.anyRequest().authenticated())
				.cors(Customizer.withDefaults());
		return http.build();
	}
}

