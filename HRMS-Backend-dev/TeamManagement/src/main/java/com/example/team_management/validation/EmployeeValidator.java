package com.example.team_management.validation;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.example.team_management.dto.EmployeeRegistrationDTO;
import com.example.team_management.exception.ValidationException;
import com.example.team_management.model.EmployeeType;
import com.example.team_management.model.EmployeeStatus;

@Component
public class EmployeeValidator {
    
    public void validateEmployeeCreation(EmployeeRegistrationDTO employee) {
        // Name validation
        if (employee.getFirstName() == null || employee.getFirstName().trim().isEmpty()) {
            throw new ValidationException("Employee name cannot be empty");
        }
        if (employee.getLastName() == null || employee.getLastName().trim().isEmpty()) {
            throw new ValidationException("Employee name cannot be empty");
        }
        
        // Email validation
        if (employee.getEmail() == null || !employee.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Invalid email format");
        }
        
        // Phone number validation
        if (employee.getPhoneNumber() == null || !employee.getPhoneNumber().matches("^\\+?[0-9]{10,14}$")) {
            throw new ValidationException("Invalid phone number");
        }
        
        // Job title validation
        if (employee.getJobTitle() == null || employee.getJobTitle().trim().isEmpty()) {
            throw new ValidationException("Job title cannot be empty");
        }
        
        // Date of birth validation
        if (employee.getDateOfBirth() == null || employee.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new ValidationException("Invalid date of birth");
        }
        
        // Date of joining validation
        if (employee.getDateOfJoining() == null || employee.getDateOfJoining().isAfter(LocalDate.now())) {
            throw new ValidationException("Invalid date of joining");
        }
        
        // Employee type validation
        if (employee.getEmployeeType() == null) {
            throw new ValidationException("Employee type cannot be null");
        }
        try {
            EmployeeType.valueOf(employee.getEmployeeType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid employee type");
        }
        
        // Status validation
        if (employee.getStatus() == null) {
            throw new ValidationException("Employee status cannot be null");
        }
        try {
            EmployeeStatus.valueOf(employee.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid employee status");
        }
    }
    
    public void validateEmployeeUpdate(EmployeeRegistrationDTO employee, Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid employee ID");
        }
        validateEmployeeCreation(employee);
    }
}