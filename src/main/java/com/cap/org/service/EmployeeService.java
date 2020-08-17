package com.cap.org.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private Logger logger = LoggerFactory.getLogger(EmployeeService.class);
		
	EmployeeRepo employeeRepo;	
	BCryptPasswordEncoder bCryptPasswordEncoder;
	Environment environment;
	
	@Autowired
	public EmployeeService(EmployeeRepo employeeRepo, 
				BCryptPasswordEncoder bCryptPasswordEncoder,
				Environment environment) {
		logger.info("EmployeeRepo  Injected to Service : "+employeeRepo.toString());
		logger.info("BCryptPasswordEncoder  Injected to Service : "+bCryptPasswordEncoder.toString());
		logger.info("Accepting Incoming Requests from only IP : "+environment.getProperty("gateway.ip"));
		
		this.employeeRepo = employeeRepo;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.environment = environment;
	}
	
//	@PostConstruct
//	public void employeeServicePostConstruct() {
//		logger.info("EmployeeService has been registered with IOC");
//	}
//	
//	@PreDestroy
//	public void employeeServicePreDestroy() {
//		logger.info("EmployeeService has been Destroyed from IOC");
//	}
//	
//	//No Args Constructor
//	public EmployeeService() {
//		logger.info("EmployeeService Constructor");
//	}

	

	public List<Employee> getSpecificEmployees(String name, double salary) {
		
		logger.info("Get Mapping with Request Parameters : Name = "+name
				+" Salary = "+salary);
		
		//List<Employee> employees = employeeRepo.findSpecifiedEmployees(name, salary);
		List<Employee> employees = employeeRepo.findAll();
		logger.info("Employees count = "+employees.size());
		employees.forEach(emp ->{
			logger.info(" "+emp);
		});
		
		
		return employees;
	}

	public List<Employee> getAllEmployees() {
		
		logger.info("Get Mapping :: Service to get All Employees");
		
		List<Employee> employees = employeeRepo.findAll();
		employees.forEach(emp ->{
			logger.info(" "+emp);
		});
		
		return employees;
	}

	public void createNewEmployee(Employee employeeRec) {
		
		
		String encryptedPassword = bCryptPasswordEncoder.encode(employeeRec.getPassword());
		logger.info("Encrypted Password to be stored in DB : "+encryptedPassword);
		
		employeeRec.setEmployeeId(0);
		employeeRec.setPassword(encryptedPassword);
		Integer maxId = employeeRepo.getMaxEmployeeId();
		
		if(maxId==null) {
			logger.info("First record in DB : "+employeeRec.getName()+0);
			employeeRec.setUsername(employeeRec.getName()+0);
		}
		else {
			logger.info("Creating username : "+employeeRec.getName()+(maxId+1));
			employeeRec.setUsername(employeeRec.getName()+(maxId+1));
		}
		
		ModelMapper modelMapper = new ModelMapper();
		Employee employee = modelMapper.map(employeeRec, Employee.class);
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		
		
		logger.info("Received Employee : "+employeeRec);
		logger.info("Model Mapped Employee : "+employee);
		
		logger.info(LocalTime.now()+"Saving Employee : "+employee);		
		employeeRepo.save(employee);
		
	}

	public Employee getEmployeeById(int id) {
		
		logger.info(LocalTime.now()+" Getting Employee by Id Service "+id);
		
		Optional<Employee> employee = employeeRepo.findById(id);
		logger.error("optional Employee by Query : "+employee);
		
		if(employee.isPresent()) {
			Employee emp = employee.get();
			return emp;
		}
		else {
			logger.error("User Not Found for username : "+id);
			throw new MyCustomException(LocalTime.now()+" Employee Does not Exist with ID : "+id);
		}
		
	}

	public Employee updateEmployee(Employee employee, int id) {		

		logger.info("Updating Employee Service -  with ID :"+id+"  "+employee);
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
			logger.info(LocalTime.now()+" No Employee Exists with :"+id+" : Creating New "+employee);
			employeeRepo.save(employee);
			return employee;
		}
		
	}

	public void deleteEmployeeById(int id) throws Exception {
		
		logger.info(LocalTime.now()+" Deleting Employee Service with ID :"+id);
		
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
		logger.info(LocalTime.now()+" Patch Employee Service -  with ID :"+id+"  "+employeeRec);
		
		if(employeeExisting.isPresent()) {
			Employee patchEmployee = employeeExisting.get();
			
			patchEmployee.setAge(employeeRec.getAge());
			patchEmployee.setName(employeeRec.getName());
			patchEmployee.setEmployeeId(id);
			patchEmployee.setSalary(employeeRec.getSalary());
			
			employeeRepo.save(patchEmployee);
		}else {
			logger.error(LocalTime.now()+" No Employee Exists with :"+id);
			throw new MyCustomException(LocalTime.now()+" No Employee Exists with :"+id);
		}
		
		return null;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		logger.info(LocalTime.now()+" loadUserByUsername With userId : "+username);
		
		Optional<Employee> employeeOpt = employeeRepo.findByUsername(username);
		
		logger.info("Optional Employee : "+employeeOpt);
		Employee employee = null;
		if(employeeOpt.isPresent()) {
			employee = employeeOpt.get();
			logger.info("Employee for username : "+username+"  -> "+employee.toString());
		}else {
			logger.error("User Not Found for username : "+username);
			throw new MyCustomException("User Not Found for username : "+username);
		}
		
		
		return new User(employee.getUsername(), 
				employee.getPassword(), 
				true, true, true, true, new ArrayList<>());
		
	}
	
	

}
