package com.cap.org.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cap.org.model.Employee;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Integer>{

	@Query("from Employee e where e.name like %:name% and e.salary > :salary")
	public List<Employee> findSpecifiedEmployees(String name, double salary);
	
	@Query(value = "select * from employee where name like %?1% and salary > ?2",
			nativeQuery = true)
	public List<Employee> findSpecifiedEmployeesBySQL(String name, double salary);

	@Query(value = "select max(employee_id) from employee", nativeQuery = true)
	public Integer getMaxEmployeeId();

	
	public Optional<Employee> findByUsername(String username);

}
