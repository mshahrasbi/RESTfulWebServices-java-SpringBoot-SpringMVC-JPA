package com.mycompanyname.app.ws.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.mycompanyname.app.ws.service.UserService;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

	private final UserService userDetailsService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public WebSecurity(UserService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userDetailsService = userDetailsService;
	}
	

	
	
	/*
	 * Because we are using Sprint Security in our project, for use to be able to enable to open one of
	 * the Swagger urls like to view the documentation that was generated for us, we will need to configue
	 * Sprint security object. So in this method we will include Swagger URL, so that we can open them in the
	 * browser window and see without providing authorization header, so we add these two lines:
	 *		.antMatchers("/v2/api-docs", "/configuration/**", "/swagger*", "/webjars/**")
	 *		.permitAll()
	 * 
	 */


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.cors()
			.and()
			.csrf()
			.disable()
			.authorizeRequests()
			.antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL)
			.permitAll()
			.antMatchers(HttpMethod.GET, SecurityConstants.VERIFICATION_EMAIL_URL)
			.permitAll()
			.antMatchers(HttpMethod.POST, SecurityConstants.PASSWORD_RESET_REQUEST_URL)
			.permitAll()
			.antMatchers(HttpMethod.POST, SecurityConstants.PASSWORD_RESET_URL)
			.permitAll()
			.antMatchers(SecurityConstants.H2_CONSOLE)
			.permitAll()
			.antMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**")
			.permitAll()
			.anyRequest()
			.authenticated()
			.and()
			.addFilter(getAuthenticationFilter())
			.addFilter(new AuthorizationFilter(authenticationManager()))
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		http.headers().frameOptions().disable();
	}
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(this.userDetailsService).passwordEncoder(this.bCryptPasswordEncoder);
	}
	
	public AuthenticationFilter getAuthenticationFilter() throws Exception {
		final AuthenticationFilter filter = new AuthenticationFilter(authenticationManager());
		filter.setFilterProcessesUrl("/users/login");
		
		return filter;
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
		
		configuration.setAllowedOrigins(Arrays.asList("*")); // Arrays.asList("http://localhost:8088", "...")
		configuration.setAllowedHeaders(Arrays.asList("*")); // Arrays.asList("GET", "PUT", "POST"))
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(Arrays.asList("*"));	 // Arrays.asList("Authorization", "Cache-Control", "Content-Type"))
		
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		
		return source;
	}
	
}
 