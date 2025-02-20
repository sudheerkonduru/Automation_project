package com.payroll_management_system.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "payrolls")
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Employee employee;
    
    @Column(name = "employee_id")
    private Long employeeId;
    
    
    private LocalDate payPeriodStart;
    private LocalDate payPeriodEnd;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double grossSalary;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double taxDeduction;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double providentFund;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double netSalary;

    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double employeeStateInsurance; // ESI deduction (1.75% of salary)

    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double totalDeductions;// Sum of all deductions
        
        // Update existing methods or add new ones as needed
    private LocalDate processedDate;
    private String status;
    private String department;
}
