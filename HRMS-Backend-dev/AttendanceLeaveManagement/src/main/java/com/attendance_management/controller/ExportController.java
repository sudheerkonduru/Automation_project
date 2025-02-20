package com.attendance_management.controller;

import com.attendance_management.service.ExportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    // Exports attendance and leaves for all employees for a given date range
    @GetMapping("/attendance-leaves")
    public ResponseEntity<byte[]> exportAttendanceAndLeavesForAll(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws Exception {
        byte[] fileContent = exportService.exportAttendanceAndLeavesForAll(startDate, endDate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "attendance_leaves.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileContent);
    }

    // Exports attendance and leaves for a specific employee for a given date range
    @GetMapping("/attendance-leaves/{employeeId}")
    public ResponseEntity<byte[]> exportAttendanceAndLeavesForEmployee(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws Exception {
        byte[] fileContent = exportService.exportAttendanceAndLeavesForEmployee(employeeId, startDate, endDate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "attendance_leaves_" + employeeId + ".xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileContent);
    }
}
