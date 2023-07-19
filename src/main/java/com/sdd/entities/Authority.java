package com.sdd.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.transaction.Transactional;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Authority")
public class Authority {

    @Id
    @Column(name = "AUTH_ID", nullable = false)
    private String authorityId;

    @Column(name = "AUTH_GROUP_ID", nullable = false)
    private String authGroupId;

    @Column(name = "AUTHORITY")
    private String authority;

    @Column(name = "AUTH_DATE" , nullable = false)
    private Timestamp authDate;

    @Column(name = "AUTH_UNIT" , nullable = false)
    private String authUnit;


    @Column(name = "DOC_ID" , nullable = false)
    private String docId;


    @Column(name = "remarks" )
    private String remarks;


    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "UPDATED_ON", nullable = false)
    private Timestamp updatedOn;


}
