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
@Table(name = "BudgetRebase")
public class BudgetRebase {
    @Id
    @Column(name = "BUDGET_REBASE_ID", nullable = false)
    private String budgetRebaseId;

    @Column(name = "REF_TRANS_ID", nullable = false)
    private String refTransId;

    @Column(name = "FIN_YEAR", nullable = false)
    private String finYear;

    @Column(name = "REBASE_UNIT_ID", nullable = false)
    private String rebaseUnitId;

    @Column(name = "HEAD_UNIT_ID", nullable = false)
    private String headUnitId;

    @Column(name = "FRM_STATION_ID", nullable = false)
    private String frmStationId;

    @Column(name = "TO_STATION_ID", nullable = false)
    private String toStationId;

    @Column(name = "TO_HEAD_UNIT_ID", nullable = false)
    private String toHeadUnitId;

    @Column(name = "OCCURANCE_DATE", nullable = false)
    private Timestamp occuranceDate;

    @Column(name = "BUDGETHEAD_ID", nullable = false)
    private String budgetHeadId;

    @Column(name = "ALLOC_AMOUNT", nullable = false)
    private String allocAmount;

    @Column(name = "EXP_AMOUNT", nullable = false)
    private String expAmount;

    @Column(name = "BAL_AMOUNT", nullable = false)
    private String balAmount;

    @Column(name = "AMOUNT_TYPE", nullable = false)
    private String amountType;

    @Column(name = "AUTHORITY_ID", nullable = false)
    private String authorityId;

    @Column(name = "USER_ID", nullable = false)
    private String userId;

    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "UPDATED_ON", nullable = false)
    private Timestamp updatedOn;


}
