package com.payroll_management_system.repository;

import com.payroll_management_system.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByEmployeeIdAndDateBetween(Long employeeId, LocalDateTime start, LocalDateTime end);
    List<Attendance> findByEmployeeId(Long employeeId);
}