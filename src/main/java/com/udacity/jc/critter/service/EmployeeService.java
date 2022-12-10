package com.udacity.jc.critter.service;

import com.udacity.jc.critter.dataaccess.EmployeeRepository;
import com.udacity.jc.critter.domain.Employee;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private  EmployeeRepository employeeRepository;
    public EmployeeService(EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }
    public Iterable<Employee> findAllEmployees(){
        return employeeRepository.findAll();
    }
    public Optional<Employee> findEmployeeById(Long id){
        return employeeRepository.findById(id);
    }
    public Employee saveEmployee(Employee employee){
        return employeeRepository.save(employee);
    }
}
