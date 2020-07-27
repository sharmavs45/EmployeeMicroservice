package com.cap.org.security;

import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cap.org.service.EmployeeService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter{
	
	private Environment environment;
	private EmployeeService employeeService;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	public WebSecurityConfiguration(Environment environment
			,EmployeeService employeeService
			,BCryptPasswordEncoder bCryptPasswordEncoder) { 
		
		System.out.println(LocalTime.now()+" WebSecurityConfiguration Constructor .\n "
				+ "Injecting Environment : "+environment);
		
		this.environment = environment;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.employeeService = employeeService;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		System.out.println(LocalTime.now()+" configure with Http Security");
		
		http.csrf().disable();
		
		http
			.authorizeRequests()
			.antMatchers("/employees/**")
			//.hasIpAddress(environment.getProperty("gateway.ip"))
			.permitAll()
			.and()
			.addFilter(new AuthenticationFilter());
			
	}
	
	private AuthenticationFilter getAuthenticationFilter() throws Exception {
		
		
		AuthenticationFilter authenticationFilter = new AuthenticationFilter();
		authenticationFilter.setAuthenticationManager(authenticationManager());
		
		System.out.println(" authenticationFilter -> "+authenticationFilter.toString());
		
		return authenticationFilter;
	}

	@Override
	protected void configure
		(AuthenticationManagerBuilder authenticationManagerBuilder)	throws Exception {
		
		System.out.println("AuthenticationManagerBuilder -> "+authenticationManagerBuilder.toString());
		authenticationManagerBuilder
			.userDetailsService(employeeService)
			.passwordEncoder(bCryptPasswordEncoder);
		
	}
	
	
	
	

}











