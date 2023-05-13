package com.sdd.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "CgUnit")
public class CgUnit {

    @Id
    @Column(name = "UNIT", nullable = false)
    private String unit;

    @Column(name = "DESCR", nullable = false)
    private String descr;

    @Column(name = "SHORT", nullable = false)
    private String cgUnitShort;

    @Column(name = "PURPOSE_CODE")
    private String purposeCode;

    @Column(name = "SUB_UNIT")
    private String subUnit;

    @Column(name = "IS_ACTIVE")
    private String isActive;

    @Column(name = "IS_FLAG")
    private String isFlag;


    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "UPDATED_ON", nullable = false)
    private Timestamp updatedOn;

    @Column(name = "STATION_ID", nullable = false)
    private String stationId;

    @Column(name = "BUD_GROUP_UNIT")
    private String budGroupUnit;
}
