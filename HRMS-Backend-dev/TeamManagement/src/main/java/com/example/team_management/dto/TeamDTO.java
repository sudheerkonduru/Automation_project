package com.example.team_management.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

import com.example.team_management.model.EmployeeRole;
import com.example.team_management.model.TeamDesignation;

@Data
public class TeamDTO {
    private Long id;

    @NotBlank(message = "Team name is required")
    private String name;

    @NotNull(message = "Team designation is required")
    private TeamDesignation designation;

    @NotBlank(message = "Department is required")
    private String department;

    private String description;

    private Long parentTeamId;

    @NotEmpty(message = "At least one allowed role must be specified")
    private Set<EmployeeRole> allowedRoles;

    @NotBlank(message = "Project code is required")
    private String projectCode;
}