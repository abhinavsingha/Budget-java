package com.sdd.entities.repository;

import com.sdd.entities.AllocationType;
import com.sdd.entities.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileUploadRepo extends JpaRepository<FileUpload, Long> {


}
