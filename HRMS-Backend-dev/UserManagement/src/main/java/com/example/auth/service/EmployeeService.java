package com.example.auth.service;

import com.example.auth.model.Employee;
import com.example.auth.model.EmployeeStatus;
import com.example.auth.repository.EmployeeRepository;

import jakarta.persistence.EntityNotFoundException;

import com.example.auth.exception.ResourceNotFoundException;
import com.example.auth.dto.EmployeeUpdateDto;
import com.example.auth.exception.ApiException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class EmployeeService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeValidationService validationService;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder,
            EmployeeValidationService validationService) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.validationService = validationService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Employee not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
            employee.getEmail(),
            employee.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + employee.getRole().name()))
        );
    }

    @Transactional
    public Employee registerEmployee(Employee employee) {
        if (employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
            throw new ApiException("Email already registered", HttpStatus.CONFLICT);
        }
        // Validate the employee
        validationService.validateNewEmployee(employee);
        
        // Validate phone number format
        if (employee.getPhoneNumber() != null && !employee.getPhoneNumber().trim().isEmpty() 
            && !employee.getPhoneNumber().matches("^\\+[1-9]\\d{9,14}$")) {
            throw new ApiException("Phone number must be in E.164 format (e.g., +1234567890)", HttpStatus.BAD_REQUEST);
        }
        
        // Encrypt the password
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));

        // Set default status if not provided
        if (employee.getStatus() == null) {
            employee.setStatus(EmployeeStatus.ACTIVE);
        }

        return employeeRepository.save(employee);
    }
    
    @Transactional
    public Employee updateEmployeeById(Long employeeId, EmployeeUpdateDto updatedEmployee) {
    	Employee existingEmployee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with employeeId: " + employeeId));

        // Update fields
        if (updatedEmployee.getStatus() != null) {
            existingEmployee.setStatus(updatedEmployee.getStatus());
        }
        if (updatedEmployee.getDepartment() != null) {
            existingEmployee.setDepartment(updatedEmployee.getDepartment());
        }
        if (updatedEmployee.getDesignation() != null) {
            existingEmployee.setDesignation(updatedEmployee.getDesignation());
        } 
        if (updatedEmployee.getBasicSalary() != null) {
            existingEmployee.setBasicSalary(updatedEmployee.getBasicSalary());
        }
        return employeeRepository.save(existingEmployee);
    }
 

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    public Employee getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + email));
    }


}