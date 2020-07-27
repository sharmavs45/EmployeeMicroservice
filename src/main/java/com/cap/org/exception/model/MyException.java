package com.cap.org.exception.model;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class MyException{
	
	private LocalTime localTime;
	
	private String message;

	

}
