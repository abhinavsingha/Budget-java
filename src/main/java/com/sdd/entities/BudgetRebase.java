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
@Table(name = "BudgetRebase")
public class BudgetRebase {
    @Id
    @Column(name = "BUDGET_REBASE_ID", nullable = false)
    private String budgetRebaseId;

    @Column(name = "REF_TRANS_ID")
    private String refTransId;

    @Column(name = "AUTH_GRP_ID")
    private String authGrpId;

    @Column(name = "FIN_YEAR")
    private String finYear;

    @Column(name = "REBASE_UNIT_ID")
    private String rebaseUnitId;

    @Column(name = "HEAD_UNIT_ID")
    private String headUnitId;

    @Column(name = "FRM_STATION_ID")
    private String frmStationId;

    @Column(name = "TO_STATION_ID")
    private String toStationId;

    @Column(name = "TO_HEAD_UNIT_ID")
    private String toHeadUnitId;

    @Column(name = "OCCURANCE_DATE")
    private Timestamp occuranceDate;

    @Column(name = "BUDGETHEAD_ID")
    private String budgetHeadId;

    @Column(name = "ALLOC_FROM_UNIT")
    private String allocFromUnit;

    @Column(name = "ALLOC_AMOUNT")
    private String allocAmount;

    @Column(name = "EXP_AMOUNT")
    private String expAmount;

    @Column(name = "BAL_AMOUNT")
    private String balAmount;

    @Column(name = "CDA_BAL_AMOUNT")
    private String remCdaBal;

    @Column(name = "AMOUNT_TYPE")
    private String amountType;

    @Column(name = "ALLOC_TYPE")
    private String allocTypeId;

    @Column(name = "AUTHORITY_ID")
    private String authorityId;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "LOGIN_UNIT")
    private String loginUnit;

    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "UPDATED_ON")
    private Timestamp updatedOn;

    @Column(name = "LASTCB_DATE")
    private Timestamp lastCbDate;


}
