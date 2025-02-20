package com.attendance_management.repository;

import com.attendance_management.model.Attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByEmployeeId(Long employeeId);
    Optional<Attendance> findByEmployeeIdAndDate(Long employeeId, LocalDateTime date);
    List<Attendance> findByEmployeeIdAndDateBetween(Long employeeId, LocalDateTime startDateTime, LocalDateTime endDateTime);
    List<Attendance> findByDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    int countByEmployeeId(Long employeeId);
    @Query("SELECT COUNT(a) FROM Attendance a " +
    "WHERE a.employeeId = :employeeId " +
    "AND FUNCTION('YEAR', a.date) = :year " +
    "AND FUNCTION('MONTH', a.date) = :month " +
    "AND FUNCTION('DAY', a.date) = :day")
int countByEmployeeIdAndDate(@Param("employeeId") Long employeeId,
                          @Param("year") int year,
                          @Param("month") int month,
                          @Param("day") int day);
    
}