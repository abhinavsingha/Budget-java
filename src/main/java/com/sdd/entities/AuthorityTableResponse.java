package com.sdd.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;


@Getter
@Setter

public class AuthorityTableResponse {

    private String authorityId;
    private String authGroupId;
    private String authority;
    private Timestamp authDate;
    private String authUnit;

    private String remarks;
    private Timestamp createdOn;
    private Timestamp updatedOn;
    FileUpload docId;

}
