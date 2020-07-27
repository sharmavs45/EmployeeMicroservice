package com.cap.org.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@XmlRootElement
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Employee implements Comparable<Employee> {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "employee_id")
	private int employeeId;
	
	//@Email
	@NotNull(message = "Name Should not be Empty")
	private String name;
	
	@Min(value = 1)
	private double salary;
	
	@Min(value = 15, message = "Age Must be Greater than 15")
	@Max(value = 100, message = "Age Must be Less than 100")
	private int age;
	
	//@NotNull
	private String username;
	
	@NotNull
	private String password; 
	
	
	@Override
	public int compareTo(Employee emp2) {
		
		if(this.age > emp2.age)
			return 1;
		else
			return -1;

	}

}
