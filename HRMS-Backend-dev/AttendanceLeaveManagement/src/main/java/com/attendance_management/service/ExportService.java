package com.attendance_management.service;

import com.attendance_management.model.Employee;
import com.attendance_management.repository.AttendanceRepository;
import com.attendance_management.repository.EmployeeRepository;
import com.attendance_management.repository.LeaveRequestRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ExportService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public ExportService(EmployeeRepository employeeRepository,
                         AttendanceRepository attendanceRepository,
                         LeaveRequestRepository leaveRequestRepository) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    // Exports for all employees for a given date range
    public byte[] exportAttendanceAndLeavesForAll(LocalDate startDate, LocalDate endDate) throws IOException {
        validateDates(startDate, endDate);
        List<Employee> employees = employeeRepository.findAll();
        return generateWorkbook(employees, startDate, endDate);
    }

    // Exports for a single employee by id for a given date range
    public byte[] exportAttendanceAndLeavesForEmployee(Long employeeId, LocalDate startDate, LocalDate endDate) 
            throws IOException {
        validateDates(startDate, endDate);
        Optional<Employee> optEmp = employeeRepository.findById(employeeId);
        if (optEmp.isEmpty()) {
            throw new IllegalArgumentException("Employee with id " + employeeId + " not found");
        }
        return generateWorkbook(List.of(optEmp.get()), startDate, endDate);
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        LocalDate currentDate = LocalDate.now();
        
        if (startDate.isAfter(currentDate)) {
            throw new IllegalArgumentException("Start date cannot be in the future");
        }
        
        if (endDate.isAfter(currentDate)) {
            throw new IllegalArgumentException("End date cannot exceed current date");
        }
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
    }

    // Generates the Excel workbook for the given employees and date range
    private byte[] generateWorkbook(List<Employee> employees, LocalDate startDate, LocalDate endDate) 
            throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Attendance & Leaves");

        // Create styles
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        int rowCount = 0;
        // Iterate over each employee
        for (Employee emp : employees) {
            // Create a merged row for employee header
            Row empHeaderRow = sheet.createRow(rowCount++);
            Cell empHeaderCell = empHeaderRow.createCell(0);
            empHeaderCell.setCellValue("Employee: " + emp.getFullName() + " (ID: " + emp.getEmployeeId() + ")");
            empHeaderCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(empHeaderRow.getRowNum(), empHeaderRow.getRowNum(), 0, 2));

            // Create column header for the table below
            Row tableHeader = sheet.createRow(rowCount++);
            String[] headers = {"Date", "Attendance Count", "Leave Count"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = tableHeader.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Get the first and last day of the month from the startDate
            LocalDate monthStart = startDate.withDayOfMonth(1);
            LocalDate monthEnd = startDate.withDayOfMonth(startDate.lengthOfMonth());

            // For each day in the month
            for (LocalDate date = monthStart; !date.isAfter(monthEnd); date = date.plusDays(1)) {
                Row dataRow = sheet.createRow(rowCount++);
                Cell dateCell = dataRow.createCell(0);
                dateCell.setCellValue(date.toString());
                dateCell.setCellStyle(dataStyle);

                int attendanceCount = attendanceRepository.countByEmployeeIdAndDate(
                        emp.getId(),
                        date.getYear(),
                        date.getMonthValue(),
                        date.getDayOfMonth());
                int leaveCount = leaveRequestRepository.countByEmployeeIdAndDateInLeaveRange(
                        emp.getId(),
                        date);

                Cell attCell = dataRow.createCell(1);
                attCell.setCellValue(attendanceCount);
                attCell.setCellStyle(dataStyle);

                Cell leaveCell = dataRow.createCell(2);
                leaveCell.setCellValue(leaveCount);
                leaveCell.setCellStyle(dataStyle);
            }
            // Add an empty row between employees
            rowCount++;
        }

        // Auto-size columns
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        return baos.toByteArray();
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        return headerStyle;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        return dataStyle;
    }
}
