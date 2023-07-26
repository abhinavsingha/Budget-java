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
@Table(name = "CDA_REVISION_DATA")
public class CdaRevisionData {
    @Id
    @Column(name = "CDA_RR_ID", nullable = false)
    private String cdaRrId;

    @Column(name = "FIN_YEAR_ID")
    private String finYearId;

    @Column(name = "BUDGET_HEAD_ID")
    private String budgetHeadId;

    @Column(name = "TO_UNID_ID")
    private String toUnitId;

    @Column(name = "FROM_UNID_ID")
    private String fromUnitId;

    @Column(name = "AMOUNT")
    private String amount;

    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "UPDATED_ON")
    private Timestamp updatedOn;

    @Column(name = "ALLOC_TYPE_ID")
    private String allocTypeId;

    @Column(name = "AUTH_GROUP_ID")
    private String authGroupId;

    @Column(name = "isFlag")
    private String isFlag;


    @Column(name = "AMOUNT_TYPE")
    private String amountType;

    @Column(name = "CDA_TRANS_ID")
    private String cdaTransId;

    @Column(name = "IS_SELF")
    private String isSelf;



}
