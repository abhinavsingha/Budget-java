package com.sdd.entities.repository;


import com.sdd.entities.FileUpload;
import com.sdd.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {


    FileUpload findByUploadID(String docTypeId);
}
