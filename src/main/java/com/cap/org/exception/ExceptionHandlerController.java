package com.cap.org.exception;

import java.time.LocalTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.cap.org.exception.model.MyException;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler{

	@ExceptionHandler(value = {Exception.class})
	public ResponseEntity<MyException> exceptionHandling(Exception ex){
		
		MyException myException = new MyException(LocalTime.now(), ex.getMessage());
		
		return new ResponseEntity<MyException>(myException, HttpStatus.BAD_REQUEST);
		
	}
}
