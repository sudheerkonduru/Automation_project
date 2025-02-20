package com.EmpolyeeManagement.app.services;

import com.EmpolyeeManagement.app.model.Employee;
import com.EmpolyeeManagement.app.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee getByEmployeeId(Long employeeId) {
        return employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with employeeId: " + employeeId));
    }

    public Employee updateByEmployeeId(Long employeeId, Employee updatedEmployee) {
        Employee existingEmployee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with employeeId: " + employeeId));

        // Update fields
        if (updatedEmployee.getPhoneNumber() != null) {
            existingEmployee.setPhoneNumber(updatedEmployee.getPhoneNumber());
        }
        if (updatedEmployee.getPassword() != null) {
            existingEmployee.setPassword(updatedEmployee.getPassword());
        }


        return employeeRepository.save(existingEmployee);
    }
} 
