package com.example.team_management.controller;


import lombok.RequiredArgsConstructor;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.team_management.dto.TeamDTO;
import jakarta.validation.Valid;
import com.example.team_management.model.Team;
import com.example.team_management.service.TeamService;


// import jakarta.validation.constraints.Pattern;
// import com.example.team_management.dto.EmployeeRegistrationDTO;
// import com.example.team_management.model.Employee;
// import com.example.team_management.service.EmployeeService;
// import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/hr")
@RequiredArgsConstructor
public class HRController {
    private final TeamService teamService;
    // private final EmployeeService employeeService;

    // Team Management Endpoints

    // Creates a new team with the provided details
    @PostMapping("/create-teams")
    public ResponseEntity<Team> createTeam(@Valid @RequestBody TeamDTO teamDTO) {
        return ResponseEntity.ok(teamService.createTeam(teamDTO));
    }

    // Creates a new sub-team under a specified parent team
    @PostMapping("/teams/{parentTeamId}/subteams")
    public ResponseEntity<Team> createSubTeam(
            @PathVariable Long parentTeamId,
            @Valid @RequestBody TeamDTO subTeamDTO) {
        return ResponseEntity.ok(teamService.createSubTeam(parentTeamId, subTeamDTO));
    }

    // Retrieves all teams with a specific designation
    @GetMapping("/teams/designation/{designation}")
    public ResponseEntity<List<Team>> getTeamsByDesignation(
            @PathVariable String designation) {
        return ResponseEntity.ok(teamService.getTeamsByDesignation(designation));
    }

    // Adds an employee as a member of a specific team
    @PostMapping("/teams/{teamId}/members/{employeeId}")
    public ResponseEntity<Void> addMemberToTeam(
            @PathVariable Long teamId,
            @PathVariable Long employeeId) {
        teamService.addMemberToTeam(teamId, employeeId);
        return ResponseEntity.ok().build();
    }

    // Removes an employee from a specific team
    @DeleteMapping("/teams/{teamId}/members/{employeeId}")
    public ResponseEntity<Void> removeMemberFromTeam(
            @PathVariable Long teamId,
            @PathVariable Long employeeId) {
        teamService.removeMemberFromTeam(teamId, employeeId);
        return ResponseEntity.ok().build();
    }

    // // Employee Management Endpoints

    // // Registers a new employee with the provided information
    // @PostMapping("/employees")
    // public ResponseEntity<Employee> registerEmployee(@Valid @RequestBody EmployeeRegistrationDTO registrationDto) {
    //     Employee employee = employeeService.registerEmployee(registrationDto);
    //     return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    // }

    // // Updates an existing employee's information
    // @PutMapping("/employees/{employeeId}")
    // public ResponseEntity<Employee> updateEmployee(
    //         @PathVariable Long employeeId,
    //         @RequestBody EmployeeRegistrationDTO updateDto) {
    //     return ResponseEntity.ok(employeeService.updateEmployee(employeeId, updateDto));
    // }

    // // Updates an employee's status (active/inactive/etc)
    // @PutMapping("/employees/{employeeId}/status")
    // public ResponseEntity<Void> updateEmployeeStatus(
    //         @PathVariable Long employeeId,
    //         @RequestParam @Pattern(regexp = "^(Active|Inactive)$") String status) {
    //     employeeService.updateEmployeeStatus(employeeId, status);
    //     return ResponseEntity.ok().build();
    // }

    // // Retrieves employee information by their ID
    // @GetMapping("/employees/{employeeId}")
    // public ResponseEntity<Employee> getEmployee(@PathVariable Long employeeId) {
    //     return ResponseEntity.ok(employeeService.getEmployeeById(employeeId));
    // }
}