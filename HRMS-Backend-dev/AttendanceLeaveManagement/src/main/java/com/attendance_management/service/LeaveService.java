package com.attendance_management.service;

import com.attendance_management.dto.LeaveBalanceDTO;
import com.attendance_management.dto.LeaveRequestDTO;
import com.attendance_management.dto.LeaveResponseDTO;
import com.attendance_management.model.*;
import com.attendance_management.repository.LeaveRequestRepository;
import com.attendance_management.repository.LeaveBalanceRepository;
import com.attendance_management.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public void initializeLeaveBalances(Long employeeId) {
        // Validate employee exists
        employeeRepository.findByEmployeeId(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
            
        int currentYear = LocalDate.now().getYear();
        
        // Initialize SICK_LEAVE balance
        LeaveBalance sickLeaveBalance = new LeaveBalance();
        sickLeaveBalance.setEmployeeId(employeeId);
        sickLeaveBalance.setLeaveType(LeaveType.SICK_LEAVE);
        sickLeaveBalance.setYear(currentYear);
        sickLeaveBalance.setTotalLeaves(12);
        sickLeaveBalance.setUsedLeaves(0);
        sickLeaveBalance.setRemainingLeaves(12);
        leaveBalanceRepository.save(sickLeaveBalance);
        
        // Initialize PAID_LEAVE balance
        LeaveBalance paidLeaveBalance = new LeaveBalance();
        paidLeaveBalance.setEmployeeId(employeeId);
        paidLeaveBalance.setLeaveType(LeaveType.PAID_LEAVE);
        paidLeaveBalance.setYear(currentYear);
        paidLeaveBalance.setTotalLeaves(12);
        paidLeaveBalance.setUsedLeaves(0);
        paidLeaveBalance.setRemainingLeaves(12);
        leaveBalanceRepository.save(paidLeaveBalance);
    }

    @Transactional
    public LeaveResponseDTO applyLeave(LeaveRequestDTO leaveRequestDTO) {
        // Validate employee exists
        employeeRepository.findByEmployeeId(leaveRequestDTO.getEmployeeId())
            .orElseThrow(() -> new RuntimeException("Employee not found"));
            
        // Ensure leave balances exist
        ensureLeaveBalancesExist(leaveRequestDTO.getEmployeeId());
            
        // Validate dates
        validateLeaveDates(leaveRequestDTO.getStartDate(), leaveRequestDTO.getEndDate());
        
        // Calculate leave duration
        long leaveDays = calculateLeaveDuration(leaveRequestDTO.getStartDate(), leaveRequestDTO.getEndDate());
        
        // Check leave balance
        validateLeaveBalance(leaveRequestDTO.getEmployeeId(), leaveRequestDTO.getLeaveType(), leaveDays);
        
        // Check for overlapping approved leave requests
        validateNoOverlappingApprovedLeaves(leaveRequestDTO.getEmployeeId(), leaveRequestDTO.getStartDate(), leaveRequestDTO.getEndDate());
        
        // Create and save leave request
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployeeId(leaveRequestDTO.getEmployeeId());
        leaveRequest.setLeaveType(leaveRequestDTO.getLeaveType());
        leaveRequest.setStartDate(leaveRequestDTO.getStartDate());
        leaveRequest.setEndDate(leaveRequestDTO.getEndDate());
        leaveRequest.setReason(leaveRequestDTO.getReason());
        leaveRequest.setStatus(LeaveStatus.PENDING);
        
        LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);
        return convertToResponseDTO(savedRequest);
    }

    private void ensureLeaveBalancesExist(Long employeeId) {
        int currentYear = LocalDate.now().getYear();
        
        // Check and initialize SICK_LEAVE balance
        leaveBalanceRepository
            .findByEmployeeIdAndLeaveTypeAndYear(employeeId, LeaveType.SICK_LEAVE, currentYear)
            .orElseGet(() -> initializeLeaveBalance(employeeId, LeaveType.SICK_LEAVE, currentYear));
        
        // Check and initialize PAID_LEAVE balance
        leaveBalanceRepository
            .findByEmployeeIdAndLeaveTypeAndYear(employeeId, LeaveType.PAID_LEAVE, currentYear)
            .orElseGet(() -> initializeLeaveBalance(employeeId, LeaveType.PAID_LEAVE, currentYear));
    }

    private LeaveBalance initializeLeaveBalance(Long employeeId, LeaveType leaveType, int year) {
        LeaveBalance balance = new LeaveBalance();
        balance.setEmployeeId(employeeId);
        balance.setLeaveType(leaveType);
        balance.setYear(year);
        balance.setTotalLeaves(12);
        balance.setUsedLeaves(0);
        balance.setRemainingLeaves(12);
        return leaveBalanceRepository.save(balance);
    }

    private LeaveResponseDTO convertToResponseDTO(LeaveRequest leaveRequest) {
        LeaveResponseDTO dto = new LeaveResponseDTO();
        dto.setId(leaveRequest.getId());
        dto.setEmployeeId(leaveRequest.getEmployeeId());
        dto.setLeaveType(leaveRequest.getLeaveType());
        dto.setStartDate(leaveRequest.getStartDate());
        dto.setEndDate(leaveRequest.getEndDate());
        dto.setReason(leaveRequest.getReason());
        dto.setStatus(leaveRequest.getStatus());
        dto.setHrRemarks(leaveRequest.getHrRemarks());
        return dto;
    }

    public LeaveBalanceDTO convertToBalanceDTO(LeaveBalance leaveBalance) {
        LeaveBalanceDTO dto = new LeaveBalanceDTO();
        dto.setEmployeeId(leaveBalance.getEmployeeId());
        dto.setLeaveType(leaveBalance.getLeaveType());
        dto.setTotalLeaves(leaveBalance.getTotalLeaves());
        dto.setUsedLeaves(leaveBalance.getUsedLeaves());
        dto.setRemainingLeaves(leaveBalance.getRemainingLeaves());
        dto.setYear(leaveBalance.getYear());
        return dto;
    }

    // Helper methods
    private void validateLeaveDates(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        if (startDate.isBefore(today)) {
            throw new IllegalArgumentException("Cannot apply leave for past dates");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }

    private long calculateLeaveDuration(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    private void validateLeaveBalance(Long employeeId, LeaveType leaveType, long leaveDays) {
        LeaveBalance balance = leaveBalanceRepository
            .findByEmployeeIdAndLeaveTypeAndYear(employeeId, leaveType, LocalDate.now().getYear())
            .orElseThrow(() -> new RuntimeException("Leave balance not found"));
            
        if (balance.getRemainingLeaves() < leaveDays) {
            throw new IllegalStateException(
                String.format("Insufficient leave balance. Available: %d, Requested: %d",
                    balance.getRemainingLeaves(), leaveDays));
        }
    }

    private void validateNoOverlappingApprovedLeaves(Long employeeId, LocalDate startDate, LocalDate endDate) {
        List<LeaveRequest> overlappingLeaves = leaveRequestRepository
            .findByEmployeeIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                employeeId, LeaveStatus.APPROVED, endDate, startDate);
        
        if (!overlappingLeaves.isEmpty()) {
            throw new IllegalStateException("Leave request overlaps with an already approved leave");
        }
    }

    @Transactional
    public LeaveRequest processLeaveRequest(Long requestId, LeaveStatus status, String remarks) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Leave request not found"));
            
        // Prevent processing already approved/rejected requests
        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Can only process pending leave requests");
        }
        
        request.setStatus(status);
        request.setHrRemarks(remarks);
        
        // Update leave balance only if approved
        if (status == LeaveStatus.APPROVED) {
            updateLeaveBalance(request);
            
            // Update employee status using employeeId
            Employee employee = employeeRepository.findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
            employee.setStatus(EmployeeStatus.ON_LEAVE);
            employeeRepository.save(employee);
        }
        
        return leaveRequestRepository.save(request);
    }

    private void updateLeaveBalance(LeaveRequest request) {
        LeaveBalance balance = leaveBalanceRepository
            .findByEmployeeIdAndLeaveTypeAndYear(
                request.getEmployeeId(),
                request.getLeaveType(),
                LocalDate.now().getYear())
            .orElseThrow(() -> new RuntimeException("Leave balance not found"));
            
        // Increment used leaves by the duration of the leave
        long leaveDays = calculateLeaveDuration(request.getStartDate(), request.getEndDate());
        int newUsedLeaves = balance.getUsedLeaves() + (int) leaveDays;
        balance.setUsedLeaves(newUsedLeaves);
        balance.setRemainingLeaves(balance.getTotalLeaves() - newUsedLeaves);
        
        leaveBalanceRepository.save(balance);
    }

    public List<LeaveRequest> getAllLeaves(LeaveStatus status) {
        if (status != null) {
            return leaveRequestRepository.findByStatus(status);
        }
        return leaveRequestRepository.findAll();
    }

    public List<LeaveBalance> getAllLeaveBalances(int year) {
        return leaveBalanceRepository.findByYear(year);
    }

    public List<LeaveRequest> getEmployeeLeaves(Long employeeId, LeaveStatus status) {
        if (status != null) {
            return leaveRequestRepository.findByEmployeeIdAndStatus(employeeId, status);
        }
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    public List<LeaveBalance> getEmployeeLeaveBalances(Long employeeId, int year) {
        return leaveBalanceRepository.findByEmployeeIdAndYear(employeeId, year);
    }
}