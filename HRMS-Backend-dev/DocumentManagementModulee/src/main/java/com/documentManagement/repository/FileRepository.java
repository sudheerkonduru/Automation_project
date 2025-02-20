package com.documentManagement.repository;

import com.documentManagement.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
	 List<FileEntity> findAllByEmployeeId(Long employeeId);
	 List<FileEntity> findByEmployeeId(Long employeeId);
}