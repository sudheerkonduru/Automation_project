// package com.payroll_management_system.controller;

// import com.payroll_management_system.model.Payroll;
// import com.payroll_management_system.service.PayrollService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.web.bind.annotation.*;

// import java.time.LocalDate;

// @RestController
// @RequestMapping("//payroll")
// @RequiredArgsConstructor
// public class PayrollController {

//     private final PayrollService payrollService;

//     @PostMapping("/generate/{employeeId}")
//     public Payroll generatePayroll(@PathVariable Long employeeId) {
//         return payrollService.generatePayroll(employeeId, LocalDate.now());
//     }
// }

package com.payroll_management_system.controller;

import com.payroll_management_system.model.Payroll;
import com.payroll_management_system.service.PayrollService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
@Slf4j
public class PayrollController {

    private final PayrollService payrollService;

    /**
     * Generate payroll for an employee for the current month
     * 
     * @param employeeId The ID of the employee
     * @return Generated Payroll
     */
    @PostMapping("/generate/{employeeId}")
    public ResponseEntity<Payroll> generatePayroll(
        @PathVariable Long employeeId,
        @RequestParam(required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
        LocalDate processedDate
    ) {
        try {
            Payroll payroll = payrollService.generatePayroll(
                employeeId, 
                processedDate != null ? processedDate : LocalDate.now()
            );
            return ResponseEntity.ok(payroll);
        } catch (Exception e) {
            log.error("Payroll generation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieve payroll for a specific employee
     * 
     * @param employeeId The ID of the employee
     * @return List of Payroll records
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Payroll>> getEmployeePayrolls(
        @PathVariable Long employeeId
    ) {
        try {
            List<Payroll> payrolls = payrollService.getEmployeePayrolls(employeeId);
            return ResponseEntity.ok(payrolls);
        } catch (Exception e) {
            log.error("Failed to retrieve payrolls: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieve payroll for a specific period
     * 
     * @param startDate Start of the pay period
     * @param endDate End of the pay period
     * @return List of Payroll records
     */
    @GetMapping("/period")
    public ResponseEntity<List<Payroll>> getPayrollsByPeriod(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        try {
            List<Payroll> payrolls = payrollService.getPayrollsByPeriod(startDate, endDate);
            return ResponseEntity.ok(payrolls);
        } catch (Exception e) {
            log.error("Failed to retrieve payrolls by period: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieve payrolls by department
     * 
     * @param department Department name
     * @return List of Payroll records
     */
    @GetMapping("/department/{department}")
 
    public ResponseEntity<List<Payroll>> getPayrollsByDepartment(
        @PathVariable String department
    ) {
        try {
            List<Payroll> payrolls = payrollService.getPayrollsByDepartment(department);
            return ResponseEntity.ok(payrolls);
        } catch (Exception e) {
            log.error("Failed to retrieve payrolls by department: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieve payrolls by status
     * 
     * @param status Payroll status
     * @return List of Payroll records
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payroll>> getPayrollsByStatus(
        @PathVariable String status
    ) {
        try {
            List<Payroll> payrolls = payrollService.getPayrollsByStatus(status);
            return ResponseEntity.ok(payrolls);
        } catch (Exception e) {
            log.error("Failed to retrieve payrolls by status: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}