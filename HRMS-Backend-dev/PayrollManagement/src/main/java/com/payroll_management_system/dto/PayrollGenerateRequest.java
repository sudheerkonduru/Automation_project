package com.payroll_management_system.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PayrollGenerateRequest {
    private LocalDate startDate;
    private LocalDate endDate;
} 