package com.payroll_management_system.service;

import com.payroll_management_system.model.*;
import com.payroll_management_system.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayrollService {

    private static final double TAX_RATE = 0.20;
    private static final double PF_RATE = 0.12;
    private static final double ESI_RATE = 0.0175;

    

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final PayrollRepository payrollRepository;

    /**
     * Generate payroll for an employee for a specific processing date
     * 
     * @param employeeId The ID of the employee
     * @param processedDate The date of payroll processing
     * @return Generated Payroll entity
     */
    @Transactional
    public Payroll generatePayroll(Long employeeId, LocalDate processedDate) {
        try {
            // Determine pay period (10th to 10th of next month)
            LocalDate payPeriodStart = calculatePayPeriodStart(processedDate);
            LocalDate payPeriodEnd = calculatePayPeriodEnd(payPeriodStart);

            // Fetch employee details
            Employee employee = getEmployeeOrThrow(employeeId);

            // Calculate salary components
            PayrollCalculation calculation = calculatePayroll(
                employee, 
                payPeriodStart, 
                payPeriodEnd
            );

            // Create and save payroll record
            return createPayrollRecord(
            		employeeId,
                employee, 
                calculation, 
                payPeriodStart, 
                payPeriodEnd
            );

        } catch (Exception e) {
            log.error("Payroll generation failed for employee {}: {}", employeeId, e.getMessage(), e);
            throw new RuntimeException("Payroll generation failed", e);
        }
    }

    /**
     * Comprehensive payroll calculation
     */
    private PayrollCalculation calculatePayroll(
        Employee employee, 
        LocalDate payPeriodStart, 
        LocalDate payPeriodEnd
    ) {
        // Base salary
        BigDecimal grossSalary = Optional.ofNullable(employee.getBasicSalary())
            .orElse(BigDecimal.ZERO);

        // Calculate working days and attendance
        int workingDays = calculateWorkingDays(payPeriodStart, payPeriodEnd);
        List<Attendance> attendances = getAttendances(employee.getId(), payPeriodStart, payPeriodEnd);
        List<LeaveRequest> approvedLeaves = getApprovedLeaves(employee.getId(), payPeriodStart, payPeriodEnd);

        // Calculate deductions
        double totalDeductionDays = calculateTotalDeductionDays(
            attendances, 
            approvedLeaves, 
            payPeriodStart, 
            payPeriodEnd
        );

        // Adjust gross salary based on attendance
        BigDecimal dailyWage = calculateDailyWage(grossSalary, workingDays);
        BigDecimal salaryDeduction = calculateSalaryDeduction(dailyWage, totalDeductionDays);
        BigDecimal adjustedGrossSalary = grossSalary.subtract(salaryDeduction);

        // Calculate statutory deductions
        BigDecimal taxDeduction = calculateTaxDeduction(adjustedGrossSalary);
        BigDecimal providentFund = calculateProvidentFund(adjustedGrossSalary);
        BigDecimal employeeStateInsurance = calculateESI(adjustedGrossSalary);
        BigDecimal totalDeductions = taxDeduction.add(providentFund).add(employeeStateInsurance);
        BigDecimal netSalary = adjustedGrossSalary.subtract(totalDeductions);

        return new PayrollCalculation(
            adjustedGrossSalary, 
            taxDeduction, 
            providentFund, 
            employeeStateInsurance, 
            totalDeductions, 
            netSalary
        );
    }

    /**
     * Create and save payroll record
     */
    private Payroll createPayrollRecord(
    	Long employeeId,
        Employee employee, 
        PayrollCalculation calculation, 
        LocalDate payPeriodStart, 
        LocalDate payPeriodEnd
    ) {
        Payroll payroll = new Payroll();
        payroll.setEmployeeId(employeeId);
        payroll.setEmployee(employee);
        payroll.setPayPeriodStart(payPeriodStart);
        payroll.setPayPeriodEnd(payPeriodEnd);
        payroll.setGrossSalary(calculation.grossSalary.doubleValue());
        payroll.setTaxDeduction(calculation.taxDeduction.doubleValue());
        payroll.setProvidentFund(calculation.providentFund.doubleValue());
        payroll.setEmployeeStateInsurance(calculation.employeeStateInsurance.doubleValue());
        payroll.setTotalDeductions(calculation.totalDeductions.doubleValue());
        payroll.setNetSalary(calculation.netSalary.doubleValue());
        payroll.setProcessedDate(LocalDate.now());
        payroll.setStatus("PROCESSED");
        payroll.setDepartment(employee.getDepartment());

        return payrollRepository.save(payroll);
    }

    // Utility methods for calculations

    private LocalDate calculatePayPeriodStart(LocalDate processedDate) {
        LocalDate payPeriodStart = processedDate.withDayOfMonth(10);
        return processedDate.getDayOfMonth() < 10 ? 
            payPeriodStart.minusMonths(1) : payPeriodStart;
    }

    private LocalDate calculatePayPeriodEnd(LocalDate payPeriodStart) {
        return payPeriodStart.plusMonths(1).withDayOfMonth(10);
    }

    private Employee getEmployeeOrThrow(Long employeeId) {
        return employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));
    }

    private List<Attendance> getAttendances(Long employeeId, LocalDate start, LocalDate end) {
        return attendanceRepository.findByEmployeeIdAndDateBetween(
            employeeId, start.atStartOfDay(), end.atStartOfDay()
        );
    }

    private List<LeaveRequest> getApprovedLeaves(Long employeeId, LocalDate start, LocalDate end) {
        return leaveRequestRepository
            .findByEmployeeIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                employeeId, LeaveStatus.APPROVED, end, start
            );
    }

    private BigDecimal calculateDailyWage(BigDecimal grossSalary, int workingDays) {
        return grossSalary.divide(BigDecimal.valueOf(workingDays), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateSalaryDeduction(BigDecimal dailyWage, double totalDeductionDays) {
        return dailyWage.multiply(BigDecimal.valueOf(totalDeductionDays));
    }

    private BigDecimal calculateTaxDeduction(BigDecimal adjustedGrossSalary) {
        return adjustedGrossSalary.multiply(BigDecimal.valueOf(TAX_RATE));
    }

    private BigDecimal calculateProvidentFund(BigDecimal adjustedGrossSalary) {
        return adjustedGrossSalary.multiply(BigDecimal.valueOf(PF_RATE));
    }

    private BigDecimal calculateESI(BigDecimal adjustedGrossSalary) {
        return adjustedGrossSalary.multiply(BigDecimal.valueOf(ESI_RATE));
    }

    private double calculateTotalDeductionDays(
        List<Attendance> attendances, 
        List<LeaveRequest> leaves, 
        LocalDate start, 
        LocalDate end
    ) {
        return calculateUnpaidLeaveDays(leaves, start, end) + 
               calculateUnpaidAbsenceDays(attendances, leaves, start, end);
    }

    private int calculateUnpaidLeaveDays(List<LeaveRequest> leaves, LocalDate start, LocalDate end) {
        return leaves.stream()
            .mapToInt(leave -> calculateOverlapDays(
                leave.getStartDate(), 
                leave.getEndDate(), 
                start, 
                end
            ))
            .sum();
    }

    private double calculateUnpaidAbsenceDays(
        List<Attendance> attendances, 
        List<LeaveRequest> leaves, 
        LocalDate start, 
        LocalDate end
    ) {
        return start.datesUntil(end.plusDays(1))
            .filter(date -> !isWeekend(date))
            .mapToDouble(date -> {
                boolean onLeave = leaves.stream().anyMatch(leave -> 
                    !date.isBefore(leave.getStartDate()) && 
                    !date.isAfter(leave.getEndDate())
                );

                if (onLeave) return 0.0;

                Optional<Attendance> attendance = attendances.stream()
                    .filter(a -> a.getDate().toLocalDate().equals(date))
                    .findFirst();

                return attendance
                    .map(a -> a.getStatus() == AttendanceStatus.ABSENT ? 1.0 : 
                               a.getStatus() == AttendanceStatus.HALF_DAY ? 0.5 : 0.0)
                    .orElse(1.0);
            })
            .sum();
    }

    private int calculateOverlapDays(LocalDate leaveStart, LocalDate leaveEnd, LocalDate periodStart, LocalDate periodEnd) {
        LocalDate start = leaveStart.isBefore(periodStart) ? periodStart : leaveStart;
        LocalDate end = leaveEnd.isAfter(periodEnd) ? periodEnd : leaveEnd;
        return start.isAfter(end) ? 0 : (int) ChronoUnit.DAYS.between(start, end) + 1;
    }

    private int calculateWorkingDays(LocalDate start, LocalDate end) {
        return (int) start.datesUntil(end.plusDays(1))
            .filter(date -> !isWeekend(date))
            .count();
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    /**
     * Internal class to hold payroll calculation results
     */
    private static class PayrollCalculation {
        final BigDecimal grossSalary;
        final BigDecimal taxDeduction;
        final BigDecimal providentFund;
        final BigDecimal employeeStateInsurance;
        final BigDecimal totalDeductions;
        final BigDecimal netSalary;

        PayrollCalculation(
            BigDecimal grossSalary,
            BigDecimal taxDeduction,
            BigDecimal providentFund,
            BigDecimal employeeStateInsurance,
            BigDecimal totalDeductions,
            BigDecimal netSalary
        ) {
            this.grossSalary = grossSalary;
            this.taxDeduction = taxDeduction;
            this.providentFund = providentFund;
            this.employeeStateInsurance = employeeStateInsurance;
            this.totalDeductions = totalDeductions;
            this.netSalary = netSalary;
        }
    }

    public List<Payroll> getEmployeePayrolls(Long employeeId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEmployeePayrolls'");
    }

    public List<Payroll> getPayrollsByPeriod(LocalDate startDate, LocalDate endDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPayrollsByPeriod'");
    }

    public List<Payroll> getPayrollsByDepartment(String department) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPayrollsByDepartment'");
    }

    public List<Payroll> getPayrollsByStatus(String status) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPayrollsByStatus'");
    }
}