package com.documentManagement.controller;

import com.documentManagement.entity.FileEntity;
import com.documentManagement.service.FileService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    // ------------------ Common Endpoints ------------------

    @PostMapping(value = "/uploadFiles/{employeeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadFiles(@PathVariable Long employeeId,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Please select files to upload"));
        }
        try {
            List<FileEntity> savedFiles = fileService.saveFiles(employeeId, files);
            return ResponseEntity.ok(Map.of(
                    "message", "Files uploaded successfully",
                    "files", savedFiles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload files: " + e.getMessage()));
        }
    }

    @PutMapping(value = "/updateFiles/{employeeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateFiles(@PathVariable Long employeeId,
            @RequestPart("files") List<MultipartFile> files) {
        try {
            if (files.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Please select files to update"));
            }
            List<FileEntity> updatedFiles = fileService.updateFiles(employeeId, files);
            return ResponseEntity.ok(Map.of(
                    "message", "Files updated successfully",
                    "files", updatedFiles));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "File not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update files: " + e.getMessage()));
        }
    }

    @GetMapping("/download/{employeeId}")
    public ResponseEntity<?> downloadFile(@PathVariable Long employeeId) {
        try {
            FileEntity file = fileService.getFile(employeeId);
            byte[] fileData = fileService.downloadFile(employeeId);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + file.getFileName() + "\"")
                    .body(fileData);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "File not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error downloading file: " + e.getMessage()));
        }
    }

    @GetMapping("/getAllFiles")
    public ResponseEntity<?> getAllFiles() {
        try {
            List<FileEntity> files = fileService.getAllFiles();
            if (files.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving files: " + e.getMessage()));
        }
    }

    @DeleteMapping("/deleteFile/{employeeId}")
    public ResponseEntity<?> deleteFile(@PathVariable Long employeeId) {
        try {
            fileService.deleteFile(employeeId);
            return ResponseEntity.ok(Map.of("message", "File deleted successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "File not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error deleting file: " + e.getMessage()));
        }
    }

    // ------------------ Aadhar Endpoints (Single File) ------------------

    @PostMapping(value = "/uploadAadhar/{employeeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadAadhar(@PathVariable Long employeeId,
            @RequestPart("file") MultipartFile file) {
        try {
            FileEntity savedFile = fileService.uploadAadhar(employeeId, file);
            return ResponseEntity.ok(Map.of(
                    "message", "Aadhar uploaded successfully",
                    "file", savedFile));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload Aadhar: " + e.getMessage()));
        }
    }

    @PutMapping(value = "/updateAadhar/{employeeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateAadhar(@PathVariable Long employeeId,
            @RequestPart("file") MultipartFile file) {
        try {
            // Delete existing Aadhar and upload the new one
            fileService.deleteAadhar(employeeId);
            FileEntity updatedFile = fileService.uploadAadhar(employeeId, file);
            return ResponseEntity.ok(Map.of(
                    "message", "Aadhar updated successfully",
                    "file", updatedFile));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Aadhar not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update Aadhar: " + e.getMessage()));
        }
    }

    @GetMapping("/downloadAadhar/{employeeId}")
    public ResponseEntity<?> downloadAadhar(@PathVariable Long employeeId) {
        try {
            FileEntity file = fileService.downloadAadhar(employeeId);
            byte[] fileData = fileService.downloadFile(employeeId);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + file.getFileName() + "\"")
                    .body(fileData);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Aadhar not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error downloading Aadhar: " + e.getMessage()));
        }
    }

    @DeleteMapping("/deleteAadhar/{employeeId}")
    public ResponseEntity<?> deleteAadhar(@PathVariable Long employeeId) {
        try {
            fileService.deleteAadhar(employeeId);
            return ResponseEntity.ok(Map.of("message", "Aadhar deleted successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Aadhar not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error deleting Aadhar: " + e.getMessage()));
        }
    }

    // ------------------ PAN Endpoints (Single File) ------------------

    @PostMapping(value = "/uploadPan/{employeeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadPan(@PathVariable Long employeeId,
            @RequestPart("file") MultipartFile file) {
        try {
            FileEntity savedFile = fileService.uploadPan(employeeId, file);
            return ResponseEntity.ok(Map.of(
                    "message", "PAN uploaded successfully",
                    "file", savedFile));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload PAN: " + e.getMessage()));
        }
    }

    @PutMapping(value = "/updatePan/{employeeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePan(@PathVariable Long employeeId,
            @RequestPart("file") MultipartFile file) {
        try {
            fileService.deletePan(employeeId);
            FileEntity updatedFile = fileService.uploadPan(employeeId, file);
            return ResponseEntity.ok(Map.of(
                    "message", "PAN updated successfully",
                    "file", updatedFile));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "PAN not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update PAN: " + e.getMessage()));
        }
    }

    @GetMapping("/downloadPan/{employeeId}")
    public ResponseEntity<?> downloadPan(@PathVariable Long employeeId) {
        try {
            FileEntity file = fileService.downloadPan(employeeId);
            byte[] fileData = fileService.downloadFile(employeeId);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + file.getFileName() + "\"")
                    .body(fileData);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "PAN not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error downloading PAN: " + e.getMessage()));
        }
    }

    @DeleteMapping("/deletePan/{employeeId}")
    public ResponseEntity<?> deletePan(@PathVariable Long employeeId) {
        try {
            fileService.deletePan(employeeId);
            return ResponseEntity.ok(Map.of("message", "PAN deleted successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "PAN not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error deleting PAN: " + e.getMessage()));
        }
    }

    // ------------------ Experience Endpoints (Optional Single File)
    // ------------------

    @PostMapping(value = "/uploadExperience/{employeeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadExperience(@PathVariable Long employeeId,
            @RequestPart("file") MultipartFile file) {
        try {
            FileEntity savedFile = fileService.uploadExperience(employeeId, file);
            return ResponseEntity.ok(Map.of(
                    "message", "Experience document uploaded successfully",
                    "file", savedFile));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload experience document: " + e.getMessage()));
        }
    }

    @PutMapping(value = "/updateExperience/{employeeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateExperience(@PathVariable Long employeeId,
            @RequestPart("file") MultipartFile file) {
        try {
            fileService.deleteExperience(employeeId);
            FileEntity updatedFile = fileService.uploadExperience(employeeId, file);
            return ResponseEntity.ok(Map.of(
                    "message", "Experience document updated successfully",
                    "file", updatedFile));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Experience document not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update experience document: " + e.getMessage()));
        }
    }

    @GetMapping("/downloadExperience/{employeeId}")
    public ResponseEntity<?> downloadExperience(@PathVariable Long employeeId) {
        try {
            FileEntity file = fileService.downloadExperience(employeeId);
            byte[] fileData = fileService.downloadFile(employeeId);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + file.getFileName() + "\"")
                    .body(fileData);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Experience document not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error downloading experience document: " + e.getMessage()));
        }
    }

    @DeleteMapping("/deleteExperience/{employeeId}")
    public ResponseEntity<?> deleteExperience(@PathVariable Long employeeId) {
        try {
            fileService.deleteExperience(employeeId);
            return ResponseEntity.ok(Map.of("message", "Experience document deleted successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Experience document not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error deleting experience document: " + e.getMessage()));
        }
    }

    // ------------------ Educational Documents Endpoints (Multiple Files)
    // ------------------

    @PostMapping(value = "/uploadEducationalDocuments/{employeeId}/{eduType}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadEducationalDocuments(@PathVariable Long employeeId,
            @PathVariable String eduType,
            @RequestPart("files") List<MultipartFile> files) {
        try {
            List<FileEntity> savedFiles = fileService.uploadEducationalDocuments(employeeId, eduType, files);
            return ResponseEntity.ok(Map.of(
                    "message", "Educational documents (" + eduType + ") uploaded successfully",
                    "files", savedFiles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload educational documents: " + e.getMessage()));
        }
    }

    @PutMapping(value = "/updateEducationalDocuments/{employeeId}/{eduType}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateEducationalDocuments(@PathVariable Long employeeId,
            @PathVariable String eduType,
            @RequestPart("files") List<MultipartFile> files) {
        try {
            // Delete existing documents for the provided eduType before uploading new ones
            fileService.deleteEducationalDocuments(employeeId, eduType);
            List<FileEntity> updatedFiles = fileService.uploadEducationalDocuments(employeeId, eduType, files);
            return ResponseEntity.ok(Map.of(
                    "message", "Educational documents (" + eduType + ") updated successfully",
                    "files", updatedFiles));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Educational documents (" + eduType + ") not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update educational documents: " + e.getMessage()));
        }
    }

    @GetMapping("/downloadEducationalDocuments/{employeeId}/{eduType}")
    public ResponseEntity<?> downloadEducationalDocuments(@PathVariable Long employeeId,
            @PathVariable String eduType) {
        try {
            List<FileEntity> files = fileService.downloadEducationalDocuments(employeeId, eduType);
            return ResponseEntity.ok(files);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Educational documents (" + eduType + ") not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error downloading educational documents: " + e.getMessage()));
        }
    }

    @DeleteMapping("/deleteEducationalDocuments/{employeeId}/{eduType}")
    public ResponseEntity<?> deleteEducationalDocuments(@PathVariable Long employeeId,
            @PathVariable String eduType) {
        try {
            fileService.deleteEducationalDocuments(employeeId, eduType);
            return ResponseEntity.ok(Map.of("message", "Educational documents (" + eduType + ") deleted successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Educational documents (" + eduType + ") not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error deleting educational documents: " + e.getMessage()));
        }
    }
}