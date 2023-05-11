package com.sdd.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "FileUpload")
public class FileUpload {

    @Id
    @Column(name = "UPLOAD_ID", nullable = false)
    private String uploadID;

    @Column(name = "PATH_URL", nullable = false)
    private String pathURL;

    @Column(name = "CREATED_ON")
    private Timestamp createdOn;



    @Column(name = "UPDATED_ON", nullable = false)
    private Timestamp updatedOn;


}
