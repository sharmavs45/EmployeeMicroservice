package com.cap.org.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.cap.org.exception.model.MyCustomException;
import com.cap.org.model.Employee;
import com.cap.org.repository.EmployeeRepo;

import lombok.NoArgsConstructor;

@Transactional
@Service
@NoArgsConstructor
public class EmployeeService implements UserDetailsService{
		
	EmployeeRepo employeeRepo;	
	BCryptPasswordEncoder bCryptPasswordEncoder;
	Environment environment;
	
	@Autowired
	public EmployeeService(EmployeeRepo employeeRepo, 
				BCryptPasswordEncoder bCryptPasswordEncoder,
				Environment environment) {
		System.out.println("EmployeeRepo  Injected to Service : "+employeeRepo.toString());
		System.out.println("BCryptPasswordEncoder  Injected to Service : "+bCryptPasswordEncoder.toString());
		System.out.println("Accepting Incoming Requests from only IP : "+environment.getProperty("gateway.ip"));
		
		this.employeeRepo = employeeRepo;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.environment = environment;
	}
	
//	@PostConstruct
//	public void employeeServicePostConstruct() {
//		System.out.println("EmployeeService has been registered with IOC");
//	}
//	
//	@PreDestroy
//	public void employeeServicePreDestroy() {
//		System.out.println("EmployeeService has been Destroyed from IOC");
//	}
//	
//	//No Args Constructor
//	public EmployeeService() {
//		System.out.println("EmployeeService Constructor");
//	}

	

	public List<Employee> getSpecificEmployees(String name, double salary) {
		
		System.out.println("Get Mapping with Request Parameters : Name = "+name
				+" Salary = "+salary);
		
		//List<Employee> employees = employeeRepo.findSpecifiedEmployees(name, salary);
		List<Employee> employees = employeeRepo.findAll();
		System.out.println("Employees count = "+employees.size());
		employees.forEach(emp ->{
			System.out.println(" "+emp);
		});
		
		
		return employees;
	}

	public List<Employee> getAllEmployees() {
		
		System.out.println("Get Mapping - Service to get All Employees");
		
		List<Employee> employees = employeeRepo.findAll();
		employees.forEach(emp ->{
			System.out.println(" "+emp);
		});
		
		return employees;
	}

	public void createNewEmployee(Employee employeeRec) {
		
		
		String encryptedPassword = bCryptPasswordEncoder.encode(employeeRec.getPassword());
		System.out.println("Encrypted Password to be stored in DB : "+encryptedPassword);
		
		employeeRec.setEmployeeId(0);
		employeeRec.setPassword(encryptedPassword);
		Integer maxId = employeeRepo.getMaxEmployeeId();
		
		if(maxId==null) {
			System.out.println("First record in DB : "+employeeRec.getName()+0);
			employeeRec.setUsername(employeeRec.getName()+0);
		}
		else {
			System.out.println("Creating username : "+employeeRec.getName()+(maxId+1));
			employeeRec.setUsername(employeeRec.getName()+(maxId+1));
		}
		
		ModelMapper modelMapper = new ModelMapper();
		Employee employee = modelMapper.map(employeeRec, Employee.class);
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		
		
		System.out.println("Received Employee : "+employeeRec);
		System.out.println("Model Mapped Employee : "+employee);
		
		System.out.println(LocalTime.now()+"Saving Employee : "+employee);		
		employeeRepo.save(employee);
		
	}

	public Employee getEmployeeById(int id) {
		
		System.out.println(LocalTime.now()+" Getting Employee by Id Service "+id);
		
		Optional<Employee> employee = employeeRepo.findById(id);
		
		if(employee.isPresent()) {
			Employee emp = employee.get();
			return emp;
		}
		else
			throw new MyCustomException(LocalTime.now()+" Employee Does not Exist with ID : "+id);
		
	}

	public Employee updateEmployee(Employee employee, int id) {		

		System.out.println("Updating Employee Service -  with ID :"+id+"  "+employee);
		Optional<Employee> employeeExisting = employeeRepo.findById(id);
		
		if(employeeExisting.isPresent()) {
			Employee emp = employeeExisting.get();
			
			emp.setAge(employee.getAge());
			emp.setName(employee.getName());
			emp.setEmployeeId(id);
			emp.setSalary(employee.getSalary());
			
			employeeRepo.save(emp);
			
			return emp;
		}else {
			System.out.println(LocalTime.now()+" No Employee Exists with :"+id+" : Creating New "+employee);
			employeeRepo.save(employee);
			return employee;
		}
		
	}

	public void deleteEmployeeById(int id) throws Exception {
		
		System.out.println(LocalTime.now()+" Deleting Employee Service with ID :"+id);
		
		Optional<Employee> employeeExisting = employeeRepo.findById(id);
		if(employeeExisting.isPresent()) {
			Employee emp = employeeExisting.get();
			employeeRepo.delete(emp);
		}else {
			throw new Exception(LocalTime.now()+" Employee Does not Exist with ID : "+id);
		}
		
	}

	public Employee patchEmployee(Employee employeeRec, int id) {
		
		Optional<Employee> employeeExisting = employeeRepo.findById(id);
		System.out.println(LocalTime.now()+" Patch Employee Service -  with ID :"+id+"  "+employeeRec);
		
		if(employeeExisting.isPresent()) {
			Employee patchEmployee = employeeExisting.get();
			
			patchEmployee.setAge(employeeRec.getAge());
			patchEmployee.setName(employeeRec.getName());
			patchEmployee.setEmployeeId(id);
			patchEmployee.setSalary(employeeRec.getSalary());
			
			employeeRepo.save(patchEmployee);
		}else {
			System.out.println(LocalTime.now()+" No Employee Exists with :"+id);
			throw new MyCustomException(LocalTime.now()+" No Employee Exists with :"+id);
		}
		
		return null;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		System.out.println(LocalTime.now()+" loadUserByUsername With userId : "+username);
		
		Optional<Employee> employeeOpt = employeeRepo.findByUsername(username);
		
		System.out.println("Optional Employee : "+employeeOpt);
		Employee employee = null;
		if(employeeOpt.isPresent()) {
			employee = employeeOpt.get();
			System.out.println("Employee for username : "+username+"  -> "+employee.toString());
		}else {
			throw new MyCustomException("User Not Found for username : "+username);
		}
		
		
		return new User(employee.getUsername(), 
				employee.getPassword(), 
				true, true, true, true, new ArrayList<>());
		
	}
	
	

}
