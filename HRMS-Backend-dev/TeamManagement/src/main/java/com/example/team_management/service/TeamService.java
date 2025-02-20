package com.example.team_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.team_management.model.Employee;
import com.example.team_management.model.Team;
import com.example.team_management.repository.EmployeeRepository;
import com.example.team_management.repository.TeamRepository;
import com.example.team_management.dto.TeamDTO;
import com.example.team_management.exception.ResourceNotFoundException;
import com.example.team_management.exception.ValidationException;
import com.example.team_management.model.TeamDesignation;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public Team createTeam(TeamDTO teamDTO) {
        validateTeamStructure(teamDTO);
        
        Team team = new Team();
        team.setName(teamDTO.getName());
        team.setDesignation(teamDTO.getDesignation());
        team.setDepartment(teamDTO.getDepartment());
        team.setDescription(teamDTO.getDescription());
        team.setProjectCode(teamDTO.getProjectCode());
        team.setAllowedRoles(teamDTO.getAllowedRoles());

        if (teamDTO.getParentTeamId() != null) {
            Team parentTeam = teamRepository.findById(teamDTO.getParentTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent team not found"));
            team.setParentTeam(parentTeam);
        }

        return teamRepository.save(team);
    }

    @Transactional
    public void addMemberToTeam(Long teamId, Long employeeId) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        
        // Validate access control
        if (!team.getAllowedRoles().contains(employee.getRole())) {
            throw new ValidationException("Employee role is not allowed in this team");
        }
        
        employee.setTeam(team);
        employeeRepository.save(employee);
    }

    public List<Team> getTeamsByDepartmentAndDesignation(String department, TeamDesignation designation) {
        return teamRepository.findByDepartmentAndDesignation(department, designation);
    }

    public List<Team> getSubTeamsByProject(String projectCode) {
        return teamRepository.findByProjectCode(projectCode);
    }

    private void validateTeamStructure(TeamDTO teamDTO) {
        if (teamDTO.getParentTeamId() != null) {
            Team parentTeam = teamRepository.findById(teamDTO.getParentTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent team not found"));
            
            // Validate department consistency
            if (!parentTeam.getDepartment().equals(teamDTO.getDepartment())) {
                throw new ValidationException("Sub-team must be in the same department as parent team");
            }
            
            // Validate designation hierarchy
            if (parentTeam.getDesignation() != teamDTO.getDesignation()) {
                throw new ValidationException("Sub-team must maintain designation consistency with parent team");
            }
        }
    }

    public Team createSubTeam(Long parentTeamId, TeamDTO subTeamDTO) {
        Team parentTeam = teamRepository.findById(parentTeamId)
            .orElseThrow(() -> new ResourceNotFoundException("Parent team not found"));
        Team subTeam = convertToEntity(subTeamDTO);
        subTeam.setParentTeam(parentTeam);
        return teamRepository.save(subTeam);
    }

    private Team convertToEntity(TeamDTO teamDTO) {
        Team team = new Team();
        team.setName(teamDTO.getName());
        team.setDesignation(teamDTO.getDesignation());
        team.setDepartment(teamDTO.getDepartment());
        team.setDescription(teamDTO.getDescription());
        team.setProjectCode(teamDTO.getProjectCode());
        team.setAllowedRoles(teamDTO.getAllowedRoles());
        return team;
    }

    public List<Team> getTeamsByDesignation(String designation) {
        TeamDesignation teamDesignation = TeamDesignation.valueOf(designation.toUpperCase());
        return teamRepository.findByDesignation(teamDesignation);
    }

    @Transactional
    public void removeMemberFromTeam(Long teamId, Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        
        if (!employee.getTeam().getId().equals(teamId)) {
            throw new ValidationException("Employee is not a member of this team");
        }
        
        employee.setTeam(null);
        employeeRepository.save(employee);
    }
}