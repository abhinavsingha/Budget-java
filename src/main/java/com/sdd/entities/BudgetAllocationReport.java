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
@Table(name = "budgetAllocationReport")
public class BudgetAllocationReport {

    @Id
    @Column(name = "BG_ID", nullable = false)
    private String bgId;

    @Column(name = "SUB_HEAD_DES")
    private String subHeadDes;

    @Column(name = "SUB_HEAD_ID")
    private String subHeadId;

    @Column(name = "UNIT_NAME")
    private String unitName;

    @Column(name = "UNIT_ID")
    private String unitId;

    @Column(name = "AUTH_GROUP_ID", nullable = false)
    private String authGroupId;

    @Column(name = "ALLOCATION_TYPE")
    private String allocationType;

    @Column(name = "ALLOCATION_TYPE_ID")
    private String allocationTypeId;

    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "UPDATED_ON")
    private Timestamp updatedOn;

    @Column(name = "ALLOCATION_DATE")
    private String allocationDate;

    @Column(name = "REVISED_AMOUNT")
    private String revisedAmount;

    @Column(name = "TOTAL_AMOUNT")
    private String totalAmount;

    @Column(name = "FIN_YEAR_ID")
    private String finYearId;

    @Column(name = "FIN_YEAR_DES")
    private String finYearDes;

    @Column(name = "REMARK")
    private String remark;

    @Column(name = "AMOUNT_TYPE")
    private String amountType;

    @Column(name = "IS_FLAG")
    private String isFlag;

}
