package com.example.team_management.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EmployeeRegistrationDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String gender;
    private LocalDate dateOfBirth;
    private String address;
    private String jobTitle;
    private String employeeType;
    private String department;
    private String designation;
    private String status;
    private LocalDate dateOfJoining;
}
