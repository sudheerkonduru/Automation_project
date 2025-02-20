package com.EmpolyeeManagement.app.controller;

import com.EmpolyeeManagement.app.model.Employee;
import com.EmpolyeeManagement.app.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Employee> getByEmployeeId(@PathVariable Long employeeId) {
        Employee employee = employeeService.getByEmployeeId(employeeId);
        return ResponseEntity.ok(employee);
    }

    @PutMapping("/employee/{employeeId}")
    public ResponseEntity<Employee> updateByEmployeeId(
            @PathVariable Long employeeId,
            @RequestBody Employee employee) {
        Employee updatedEmployee = employeeService.updateByEmployeeId(employeeId, employee);
        return ResponseEntity.ok(updatedEmployee);
    }
}
