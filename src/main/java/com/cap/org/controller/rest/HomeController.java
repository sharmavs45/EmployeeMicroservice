package com.cap.org.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
	
	@Autowired
	private Environment environment;
	
	@GetMapping
	public String home() {
		return "Employee Microservice - HOME on Port : "+environment.getProperty("local.server.port");
	}

}
