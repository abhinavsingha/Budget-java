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
@Table(name = "CgUnit")
public class CgUnit {

    @Id
    @Column(name = "UNIT", nullable = false)
    private String unit;

    @Column(name = "DESCR")
    private String descr;

    @Column(name = "SHORT")
    private String cgUnitShort;

    @Column(name = "PURPOSE_CODE")
    private String purposeCode;

    @Column(name = "SUB_UNIT")
    private String subUnit;

    @Column(name = "IS_ACTIVE")
    private String isActive;

    @Column(name = "IS_SHIP")
    private String isShip;


    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "UPDATED_ON")
    private Timestamp updatedOn;

    @Column(name = "STATION_ID")
    private String stationId;

    @Column(name = "BUD_GROUP_UNIT")
    private String budGroupUnit;

    @Column(name = "BUDGET_UNIT_CODE")
    private String budgetUnitCode;

    @Column(name = "IS_REBASE_AUTHORITY")
    private String isRebaseAuthority;

    @Column(name = "unit_rhq")
    private String unitRhq;

    @Column(name = "unit_dhq")
    private String unitDhq;
}
