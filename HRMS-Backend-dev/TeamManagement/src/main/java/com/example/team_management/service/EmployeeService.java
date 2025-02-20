package com.example.team_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.team_management.dto.EmployeeRegistrationDTO;

import com.example.team_management.model.Employee;
import com.example.team_management.model.EmployeeRole;
import com.example.team_management.model.Gender;
import com.example.team_management.repository.EmployeeRepository;
import com.example.team_management.model.EmployeeStatus;
import com.example.team_management.model.EmployeeType;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public Employee registerEmployee(EmployeeRegistrationDTO registrationDto) {
        if (employeeRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Employee employee = new Employee();
        employee.setFirstName(registrationDto.getFirstName());
        employee.setLastName(registrationDto.getLastName());
        employee.setEmail(registrationDto.getEmail());
        employee.setPassword(registrationDto.getPassword()); // In production, password should be encrypted
        employee.setPhoneNumber(registrationDto.getPhoneNumber());
        employee.setGender(Gender.valueOf(registrationDto.getGender().toUpperCase()));
        employee.setDateOfBirth(registrationDto.getDateOfBirth());
        employee.setAddress(registrationDto.getAddress());
        employee.setRole(EmployeeRole.EMPLOYEE); // Default role
        employee.setDateOfJoining(LocalDate.now());
        employee.setEmployeeType(EmployeeType.valueOf(registrationDto.getEmployeeType().toUpperCase()));
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartment(registrationDto.getDepartment());
        employee.setDesignation(registrationDto.getDesignation());

        // Generate employee ID
        employee.setEmployeeId(generateEmployeeId());

        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Long employeeId, EmployeeRegistrationDTO updateDto) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setFirstName(updateDto.getFirstName());
        employee.setLastName(updateDto.getLastName());
        employee.setPhoneNumber(updateDto.getPhoneNumber());
        employee.setGender(Gender.valueOf(updateDto.getGender().toUpperCase()));
        employee.setDateOfBirth(updateDto.getDateOfBirth());
        employee.setAddress(updateDto.getAddress());
        employee.setEmployeeType(EmployeeType.valueOf(updateDto.getEmployeeType().toUpperCase()));
        employee.setDepartment(updateDto.getDepartment());
        employee.setDesignation(updateDto.getDesignation());

        return employeeRepository.save(employee);
    }

    private Long generateEmployeeId() {
        // Simple implementation - you might want to make this more sophisticated
        return System.currentTimeMillis() % 100000;
    }

    public Employee getEmployeeById(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public void updateEmployeeStatus(Long employeeId, String status) {
        Employee employee = getEmployeeById(employeeId);
        employee.setStatus(EmployeeStatus.valueOf(status.toUpperCase()));
        employeeRepository.save(employee);
    }
}