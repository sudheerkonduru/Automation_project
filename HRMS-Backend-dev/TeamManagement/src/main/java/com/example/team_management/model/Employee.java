package com.example.team_management.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.math.BigDecimal;


import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String firstName;
	private String lastName;

	@Column(unique = true, nullable = false)
	private String email; // Unique constraint for email
	
	private Long employeeId;
	
	@JsonIgnore
	private String password;

	@Column(name = "phone_number", nullable = false)
	private String phoneNumber;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Column(name = "date_of_birth")
	private LocalDate dateOfBirth;
	private String address;

	@Enumerated(EnumType.STRING)
	private EmployeeRole role;

	private LocalDate dateOfJoining;

	@Enumerated(EnumType.STRING)
	private EmployeeType employeeType;

	@Enumerated(EnumType.STRING)
	private EmployeeStatus status;

	private String department;
    @Column(precision = 10, scale = 2)
    private BigDecimal basicSalary;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal taxDeduction;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal providentFund;
    
    @Column(name = "designation")
    private String designation;
    
    @ManyToOne
    @JoinColumn(name = "hr_id")
    private HR hr;
    
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;
}
