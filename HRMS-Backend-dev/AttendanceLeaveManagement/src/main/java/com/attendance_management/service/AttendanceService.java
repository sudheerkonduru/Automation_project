package com.attendance_management.service;

import com.attendance_management.model.Attendance;
import com.attendance_management.model.AttendanceStatus;
import com.attendance_management.repository.AttendanceRepository;
import com.attendance_management.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    private static final double REQUIRED_HOURS = 9.0;
    private static final double HALF_DAY_HOURS = 4.0;

    @Transactional
    public Attendance checkIn(Long employeeId) {
        // Find employee using employeeId
        employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        // Check if already checked in
        Attendance existingAttendance = attendanceRepository.findByEmployeeIdAndDateBetween(
                employeeId, today.atStartOfDay(), today.atTime(23, 59, 59))
                .stream()
                .findFirst()
                .orElse(null);

        if (existingAttendance != null) {
            if (existingAttendance.getCheckOutTime() == null) {
                throw new IllegalStateException("Already checked in for today");
            }

            throw new IllegalStateException("Already checked in for today");
        }

        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employeeId);
        attendance.setDate(now);
        attendance.setCheckInTime(now);
        attendance.setStatus(AttendanceStatus.PRESENT); // Set initial status

        return attendanceRepository.save(attendance);
    }

    public Attendance checkOut(Long employeeId, String workDescription) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);

        // Find today's attendance using date range
        Attendance attendance = attendanceRepository
                .findByEmployeeIdAndDateBetween(employeeId, startOfDay, endOfDay)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No check-in found for today"));

        if (attendance.getCheckOutTime() != null) {
            throw new IllegalStateException("Already checked out today");
        }

        attendance.setCheckOutTime(now);
        attendance.setWorkDescription(workDescription);

        // Calculate working hours
        double hours = ChronoUnit.MINUTES.between(
                attendance.getCheckInTime(),
                now) / 60.0;
        attendance.setWorkingHours(hours);

        // Update status based on working hours
        updateAttendanceStatus(attendance, hours);

        return attendanceRepository.save(attendance);
    }

    private void updateAttendanceStatus(Attendance attendance, double hours) {
        if (hours >= REQUIRED_HOURS) {
            attendance.setStatus(AttendanceStatus.PRESENT);
        } else if (hours >= HALF_DAY_HOURS) {
            attendance.setStatus(AttendanceStatus.HALF_DAY);
            attendance.setRemarks("Working hours less than required 9 hours");
        } else {
            attendance.setStatus(AttendanceStatus.ABSENT);
            attendance.setRemarks("Working hours less than half day requirement");
        }
    }

    public List<Attendance> getAttendanceSummary(Long employeeId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceRepository.findByEmployeeIdAndDateBetween(
                employeeId, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        return attendances;
    }

    public List<Attendance> getAllAttendanceForDate(LocalDate date) {
        return attendanceRepository.findByDateBetween(
            date.atStartOfDay(),
            date.atTime(23, 59, 59));
    }

    public List<Attendance> getEmployeeAttendance(Long employeeId, LocalDate date) {
        return attendanceRepository.findByEmployeeIdAndDateBetween(
                employeeId,
                date.atStartOfDay(),
                date.atTime(23, 59, 59));
    }

    public List<Attendance> getEmployeeAttendance(Long employeeId) {
        return attendanceRepository.findByEmployeeId(employeeId);
    }

    public List<Attendance> getAllAttendanceForDateRange(LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByDateBetween(
            startDate.atStartOfDay(),
            endDate.atTime(23, 59, 59));
    }
}