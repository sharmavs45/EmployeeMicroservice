package com.cap.org.security;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.cap.org.model.Employee;
import com.cap.org.model.EmployeeLogin;
import com.cap.org.repository.EmployeeRepo;
import com.cap.org.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, 
				HttpServletResponse response) throws AuthenticationException {
		
		System.out.println(LocalTime.now()+" Authentication In Progress ::: Git hub check");
		List<Employee> employees = new ArrayList<>(); 

		try {
			EmployeeLogin loginDetails = new ObjectMapper()
						.readValue(request.getInputStream(), EmployeeLogin.class);
		
			System.out.println("Available Login Credentials : \n "
										+loginDetails.getUsername()
										+ " \n "
										+loginDetails.getPassword());
			
			@SuppressWarnings("unchecked")
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
					new UsernamePasswordAuthenticationToken(
					loginDetails.getUsername(), 
					loginDetails.getPassword(),
					(Collection<? extends GrantedAuthority>) employees);
					//new ArrayList<>());
					//setAuthenticationManager(authenticationManager);
			System.out.println("usernamePasswordAuthenticationToken -> "+usernamePasswordAuthenticationToken.toString());
			return getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken); 
					
		
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, 
			HttpServletResponse response, 
			FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		System.out.println(LocalTime.now()+" Authentication Successful");
		
		//super.successfulAuthentication(request, response, chain, authResult);
	}
	

}
