package com.documentManagement.service;

import com.documentManagement.entity.FileEntity;
import com.documentManagement.repository.FileRepository;
import com.documentManagement.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FileService {
    private final FileRepository fileRepository;
    private final EmployeeRepository employeeRepository;
    private final S3Client s3Client;
    private final String bucketName;

    public FileService(FileRepository fileRepository, EmployeeRepository employeeRepository, S3Client s3Client, @Value("${cloud.aws.s3.bucket}") String bucketName) {
        this.fileRepository = fileRepository;
        this.employeeRepository = employeeRepository;
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    // ------------------ Common Methods ------------------

    public List<FileEntity> saveFiles(Long employeeId, List<MultipartFile> files) throws Exception {
        employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));

        List<FileEntity> savedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File cannot be empty");
            }
            try {
                String key = file.getOriginalFilename();
                s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build(),
                        RequestBody.fromBytes(file.getBytes()));

                FileEntity fileEntity = new FileEntity();
                fileEntity.setFileName(key);
                fileEntity.setFileType(file.getContentType());
                fileEntity.setS3Key(key);
                fileEntity.setFileSize(file.getSize());
                fileEntity.setEmployeeId(employeeId);
                // If needed, set documentType on each file externally
                savedFiles.add(fileRepository.save(fileEntity));
            } catch (IOException e) {
                throw new Exception("Failed to store file", e);
            }
        }
        return savedFiles;
    }

    public FileEntity getFile(Long employeeId) throws Exception {
        return fileRepository.findByEmployeeId(employeeId)
                .stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("File not found for employee ID: " + employeeId));
    }

    public List<FileEntity> getAllFiles() {
        return fileRepository.findAll();
    }

    public void deleteFile(Long employeeId) {
        FileEntity fileEntity = fileRepository.findByEmployeeId(employeeId)
                .stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("File not found for employee ID: " + employeeId));

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileEntity.getS3Key())
                .build());

        fileRepository.delete(fileEntity);
    }

    public List<FileEntity> updateFiles(Long employeeId, List<MultipartFile> files) throws Exception {
        List<FileEntity> updatedFiles = new ArrayList<>();
        // Get all files for the employee; we then resolve each update by file name.
        List<FileEntity> employeeFiles = fileRepository.findAllByEmployeeId(employeeId);
        for (MultipartFile file : files) {
            String key = file.getOriginalFilename();
            FileEntity existingFile = employeeFiles.stream()
                    .filter(f -> key != null && key.equals(f.getFileName()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("File not found for employee ID: " + employeeId + " with file name: " + key));

            s3Client.putObject(PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build(),
                    RequestBody.fromBytes(file.getBytes()));

            existingFile.setFileName(key);
            existingFile.setFileType(file.getContentType());
            existingFile.setS3Key(key);
            existingFile.setFileSize(file.getSize());
            updatedFiles.add(fileRepository.save(existingFile));
        }
        return updatedFiles;
    }

    public byte[] downloadFile(Long employeeId) throws Exception {
        FileEntity fileEntity = fileRepository.findByEmployeeId(employeeId)
                .stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("File not found for employee ID: " + employeeId));
        return downloadFileFromS3(fileEntity.getS3Key());
    }

    // ------------------ Aadhar Services (Single File) ------------------

    public FileEntity uploadAadhar(Long employeeId, MultipartFile file) throws Exception {
        return uploadDocument(employeeId, file, "AADHAR");
    }

    public FileEntity downloadAadhar(Long employeeId) throws Exception {
        return downloadDocument(employeeId, "AADHAR");
    }

    public void deleteAadhar(Long employeeId) throws Exception {
        deleteDocument(employeeId, "AADHAR");
    }

    public FileEntity updateAadhar(Long employeeId, MultipartFile file) throws Exception {
        deleteAadhar(employeeId);
        return uploadAadhar(employeeId, file);
    }

    // ------------------ Pan Services (Single File) ------------------

    public FileEntity uploadPan(Long employeeId, MultipartFile file) throws Exception {
        return uploadDocument(employeeId, file, "PAN");
    }

    public FileEntity downloadPan(Long employeeId) throws Exception {
        return downloadDocument(employeeId, "PAN");
    }

    public void deletePan(Long employeeId) throws Exception {
        deleteDocument(employeeId, "PAN");
    }

    public FileEntity updatePan(Long employeeId, MultipartFile file) throws Exception {
        deletePan(employeeId);
        return uploadPan(employeeId, file);
    }

    // ------------------ Experience Services (Optional Single File) ------------------

    public FileEntity uploadExperience(Long employeeId, MultipartFile file) throws Exception {
        return uploadDocument(employeeId, file, "EXPERIENCE");
    }

    public FileEntity downloadExperience(Long employeeId) throws Exception {
        return downloadDocument(employeeId, "EXPERIENCE");
    }

    public void deleteExperience(Long employeeId) throws Exception {
        deleteDocument(employeeId, "EXPERIENCE");
    }

    public FileEntity updateExperience(Long employeeId, MultipartFile file) throws Exception {
        deleteExperience(employeeId);
        return uploadExperience(employeeId, file);
    }

    // ------------------ Educational Documents Services (Multiple Files) ------------------
    // eduType can be "SSC", "INTERMEDIATE", "DEGREE", "EXPERIENCE", etc.

    public List<FileEntity> uploadEducationalDocuments(Long employeeId, String eduType, List<MultipartFile> files) throws Exception {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("No educational documents selected for upload");
        }
        employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));

        List<FileEntity> savedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File cannot be empty");
            }
            try {
                String originalFilename = file.getOriginalFilename();
                String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
                String filename = generateFileName(employeeId, eduType, fileExtension);
                String key = employeeId + "/" + eduType + "/" + filename;

                s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build(),
                        RequestBody.fromBytes(file.getBytes()));

                FileEntity fileEntity = new FileEntity();
                fileEntity.setFileName(filename);
                fileEntity.setFileType(file.getContentType());
                fileEntity.setS3Key(key);
                fileEntity.setFileSize(file.getSize());
                fileEntity.setEmployeeId(employeeId);
                fileEntity.setDocumentType(eduType);
                savedFiles.add(fileRepository.save(fileEntity));
            } catch (IOException e) {
                throw new Exception("Failed to store educational document", e);
            }
        }
        return savedFiles;
    }

    public List<FileEntity> downloadEducationalDocuments(Long employeeId, String eduType) throws Exception {
        List<FileEntity> employeeFiles = fileRepository.findByEmployeeId(employeeId);
        List<FileEntity> eduFiles = employeeFiles.stream()
                .filter(f -> eduType.equalsIgnoreCase(f.getDocumentType()))
                .collect(Collectors.toList());
        if (eduFiles.isEmpty()) {
            throw new EntityNotFoundException("No educational documents found for type: " + eduType);
        }
        return eduFiles;
    }

    public void deleteEducationalDocuments(Long employeeId, String eduType) throws Exception {
        List<FileEntity> employeeFiles = fileRepository.findByEmployeeId(employeeId);
        List<FileEntity> eduFiles = employeeFiles.stream()
                .filter(f -> eduType.equalsIgnoreCase(f.getDocumentType()))
                .collect(Collectors.toList());
        if (eduFiles.isEmpty()) {
            throw new EntityNotFoundException("No educational documents found for type: " + eduType);
        }
        for (FileEntity fileEntity : eduFiles) {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileEntity.getS3Key())
                    .build());
            fileRepository.delete(fileEntity);
        }
    }

    public List<FileEntity> updateEducationalDocuments(Long employeeId, String eduType, List<MultipartFile> files) throws Exception {
        deleteEducationalDocuments(employeeId, eduType);
        return uploadEducationalDocuments(employeeId, eduType, files);
    }

    // ------------------ Private Helper Methods ------------------

    private FileEntity uploadDocument(Long employeeId, MultipartFile file, String docType) throws Exception {
        employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));
        
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Check for existing documents of Aadhar and Pan type
        if ("AADHAR".equalsIgnoreCase(docType) || "PAN".equalsIgnoreCase(docType)) {
            List<FileEntity> existingFiles = fileRepository.findByEmployeeId(employeeId)
                    .stream()
                    .filter(f -> docType.equalsIgnoreCase(f.getDocumentType()))
                    .collect(Collectors.toList());
                    
            if (!existingFiles.isEmpty()) {
                throw new IllegalStateException("Document of type " + docType + " already exists. Please use update endpoint instead.");
            }
        }

        // Get file extension from original filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";

        // Generate appropriate filename based on document type
        String filename = generateFileName(employeeId, docType, fileExtension);
        String key = employeeId + "/" + docType + "/" + filename;

        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build(),
                RequestBody.fromBytes(file.getBytes()));

        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(filename);
        fileEntity.setFileType(file.getContentType());
        fileEntity.setS3Key(key);
        fileEntity.setFileSize(file.getSize());
        fileEntity.setEmployeeId(employeeId);
        fileEntity.setDocumentType(docType);
        return fileRepository.save(fileEntity);
    }

    private String generateFileName(Long employeeId, String docType, String fileExtension) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        switch (docType.toUpperCase()) {
            case "AADHAR":
                return "AADHAR_" + employeeId + "_" + timestamp + fileExtension;
            case "PAN":
                return "PAN_" + employeeId + "_" + timestamp + fileExtension;
            case "EXPERIENCE":
                return "EXP_" + employeeId + "_" + timestamp + fileExtension;
            case "SSC":
                return "SSC_" + employeeId + "_" + timestamp + fileExtension;
            case "INTERMEDIATE":
                return "INTER_" + employeeId + "_" + timestamp + fileExtension;
            case "DEGREE":
                return "DEGREE_" + employeeId + "_" + timestamp + fileExtension;
            default:
                return docType + "_" + employeeId + "_" + timestamp + fileExtension;
        }
    }

    private FileEntity downloadDocument(Long employeeId, String docType) throws Exception {
        Optional<FileEntity> fileOp = fileRepository.findByEmployeeId(employeeId)
                .stream()
                .filter(f -> docType.equalsIgnoreCase(f.getDocumentType()))
                .findFirst();
        return fileOp.orElseThrow(() -> new EntityNotFoundException("Document of type " + docType + " not found for employee ID: " + employeeId));
    }

    private void deleteDocument(Long employeeId, String docType) throws Exception {
        FileEntity fileEntity = fileRepository.findByEmployeeId(employeeId)
                .stream()
                .filter(f -> docType.equalsIgnoreCase(f.getDocumentType()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Document of type " + docType + " not found for employee ID: " + employeeId));
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileEntity.getS3Key())
                .build());
        fileRepository.delete(fileEntity);
    }

    private byte[] downloadFileFromS3(String key) throws Exception {
        System.out.println("Downloading from S3 with key: " + key);
        return s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build(), ResponseTransformer.toBytes()).asByteArray();
    }
}