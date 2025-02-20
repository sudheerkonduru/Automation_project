package com.example.team_management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.team_management.model.Employee;
import com.example.team_management.model.Team;
import com.example.team_management.repository.EmployeeRepository;
import com.example.team_management.repository.TeamRepository;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    @Autowired
    private final TeamRepository teamRepository;
    @Autowired
    private final EmployeeRepository employeeRepository;

    // Gets detailed information about a specific team by ID
    @GetMapping("/team/{teamId}")
    public ResponseEntity<Team> getTeamDetails(@PathVariable Long teamId) {
        return teamRepository.findById(teamId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Gets a list of all employees in a specific team
    @GetMapping("/team/{teamId}/members")
    public ResponseEntity<List<Employee>> getTeamMembers(@PathVariable Long teamId) {
        return teamRepository.findById(teamId)
                .map(team -> ResponseEntity.ok(team.getMembers()))
                .orElse(ResponseEntity.notFound().build());
    }

    // Gets profile information for a specific employee by ID
    @GetMapping("/profile")
    public ResponseEntity<Employee> getProfile(@RequestParam Long employeeId) {
        return employeeRepository.findById(employeeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}