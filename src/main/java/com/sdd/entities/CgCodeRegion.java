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
@Transactional
@Table(name = "CgCode_Region")
public class CgCodeRegion {

    @Id
    @Column(name = "REGION_ID")
    private String regionId;

    @Column(name = "DESCR")
    private String descr;

    @Column(name = "IS_FLAG")
    private String isFlag;

    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "UPDATED_ON", nullable = false)
    private Timestamp updatedOn;

}
