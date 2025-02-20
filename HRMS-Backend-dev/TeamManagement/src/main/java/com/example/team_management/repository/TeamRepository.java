package com.example.team_management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.team_management.model.Team;
import com.example.team_management.model.TeamDesignation;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
	List<Team> findByDesignation(TeamDesignation designation);
	List<Team> findByDepartment(String department);
	List<Team> findByDepartmentAndDesignation(String department, TeamDesignation designation);
	List<Team> findByProjectCode(String projectCode);
	List<Team> findByParentTeamId(Long parentTeamId);
	boolean existsByNameAndDepartment(String name, String department);
}
