package com.example.team_management.validation;

import org.springframework.stereotype.Component;

import com.example.team_management.dto.TeamDTO;
import com.example.team_management.exception.ValidationException;

@Component
public class TeamValidator {
    
    public void validateTeamCreation(TeamDTO team) {
        if (team.getName() == null || team.getName().trim().isEmpty()) {
            throw new ValidationException("Team name cannot be empty");
        }
        
        if (team.getParentTeamId() != null && team.getParentTeamId() <= 0) {
            throw new ValidationException("Invalid parent team ID");
        }
    }
    
    public void validateTeamUpdate(TeamDTO team, Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid team ID");
        }
        validateTeamCreation(team);
    }
}