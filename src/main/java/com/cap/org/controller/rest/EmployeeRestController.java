package com.cap.org.controller.rest;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cap.org.model.Employee;
import com.cap.org.model.EmployeeLogin;
import com.cap.org.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.converters.JsonXStream;

@RestController
@RequestMapping("/employees") //http:localhost:8080/employees
public class EmployeeRestController {
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private Environment environment;

	
	/*
	 * @PostMapping("/login") public Employee login(@RequestBody EmployeeLogin
	 * loginDetails) {
	 * 
	 * return null; }
	 */
	
	@PostMapping("/parseJson")
	//public String parseJsonWithoutPojo(JsonAutoDetect json) {
	public String parseJsonWithoutPojo(JsonXStream json) {
		//JsonXStream jsonXStream = new JsonXStream();
		//jsonXStream.fromXML(json);
		
		ObjectMapper omJson = new ObjectMapper();
		//omJson.readValue(json, JSONObject.class);
		
		
		System.out.println("Reading JSON : "+json.toString());
		return "Parsing Successful";
	}
	
	@GetMapping("/param")
	public ResponseEntity<List<Employee>> getSpecificEmployees(
				@RequestParam(value = "name", defaultValue="") String name, 
				@RequestParam(value ="salary", defaultValue="0") double salary){
		
		List<Employee> employees = employeeService.getSpecificEmployees(name, salary);
		
		return new ResponseEntity<List<Employee>>(employees, HttpStatus.FOUND);
	}
	
	@GetMapping("/service-status")
	public String employeeServiceStatus(){
		
		return "Running At "+LocalDateTime.now()+" \n On Port : "+
		environment.getProperty("local.server.port")+"  ";
	}
	
	@GetMapping
	public ResponseEntity<List<Employee>> getEmployees(){
		
		List<Employee> employees =  employeeService.getAllEmployees();
	
		return new ResponseEntity<List<Employee>>(employees, HttpStatus.FOUND);
	}
	
	
	@PostMapping(consumes = {
					MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_VALUE
				},		
				produces = {
					MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_VALUE
				})
	public ResponseEntity<List<Employee>> 
					createEmployee(@Valid @RequestBody Employee employee){
		
		employeeService.createNewEmployee(employee);
		
		return new ResponseEntity<List<Employee>>(employeeService.getAllEmployees(), HttpStatus.ACCEPTED);
	}
	
	
	@GetMapping(path = "/{id}", 
			produces = {MediaType.APPLICATION_XML_VALUE,
						MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<Employee> getEmployee(@PathVariable("id") int id){
		
		Employee employee = employeeService.getEmployeeById(id);
		
		return new ResponseEntity<Employee>(employee, HttpStatus.FOUND);
		
	}
	
	@PutMapping(path = "/{id}")
	public ResponseEntity<Employee> updateEmployee(
			@RequestBody Employee employee, @PathVariable("id") int id){
		
		employee = employeeService.updateEmployee(employee, id);
		return new ResponseEntity<Employee>(employee, HttpStatus.CREATED);
	}
	
	
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<List<Employee>> deleteEmployee(@PathVariable("id") int id) throws Exception{
		
		employeeService.deleteEmployeeById(id);
		
		return new ResponseEntity<List<Employee>>
							(employeeService.getAllEmployees(), HttpStatus.ACCEPTED);
	}
	
	
	@PatchMapping(path = "/{id}")
	public ResponseEntity<Employee> patchEmployee(@PathVariable("id") int id,
				@RequestBody Employee employee){
		
		employee = employeeService.patchEmployee(employee, id);
		return new ResponseEntity<Employee>(employee, HttpStatus.ACCEPTED);
	}
	
	
	
	

}
