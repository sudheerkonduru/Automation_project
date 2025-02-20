package com.assetTracking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assets", uniqueConstraints = @UniqueConstraint(columnNames = "serial_no"))
@Data
@NoArgsConstructor
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "employee_id", nullable = false, unique = true)
    private Long employeeId;

    @NotBlank(message = "Serial number is required")
    @Column(name = "serial_no", unique = true, nullable = false)
    private String serialNo;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "RAM specification is required")
    private String ram;

    @NotBlank(message = "ROM specification is required")
    private String rom;

    @NotBlank(message = "Processor is required")
    private String processor;

    @Column(name = "date_of_issue", nullable = false)
    private LocalDate dateOfIssue;

     @ElementCollection
    private List<String> accessories = new ArrayList<>();

}