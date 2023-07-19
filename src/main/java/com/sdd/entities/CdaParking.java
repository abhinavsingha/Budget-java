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
@Table(name = "CdaParking")
public class CdaParking {
    @Id
    @Column(name = "GIN_NO", nullable = false)
    private String ginNo;

    @Column(name = "CDA_NAME", nullable = false)
    private String cdaName;

    @Column(name = "STATION", nullable = false)
    private String station;

    @Column(name = "CDA_GROUP_CODE", nullable = false)
    private String cdaGroupCode;

    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "UPDATED_ON", nullable = false)
    private Timestamp updatedOn;



}
