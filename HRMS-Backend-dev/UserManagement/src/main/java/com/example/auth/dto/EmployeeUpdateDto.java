package com.example.auth.dto;

import java.math.BigDecimal;

import com.example.auth.model.Employee;
import com.example.auth.model.EmployeeStatus;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmployeeUpdateDto {
	@NotNull(message = "Status is required")
	@Enumerated(EnumType.STRING)
	private EmployeeStatus status;

	@NotBlank(message = "Department is required")
	@Size(min = 2, max = 50, message = "Department must be between 2 and 50 characters")
	@Column(nullable = false)
	private String department;

	@NotBlank(message = "Designation is required")
	@Size(min = 2, max = 50, message = "Designation must be between 2 and 50 characters")
	@Column(name = "designation", nullable = false)
	private String designation;

	@PositiveOrZero(message = "Basic salary cannot be negative")
	@Column(precision = 10, scale = 2)
	private BigDecimal basicSalary;
	
	public Employee toEmployee() {
        Employee employee = new Employee();
        employee.setStatus(this.status);
        employee.setDepartment(this.department);
        employee.setDesignation(this.designation);
        employee.setBasicSalary(this.basicSalary);
        return employee;
    }
}
