package com.sdd.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.transaction.Transactional;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "BudgetAllocation")
public class BudgetAllocation {
    @Id
    @Column(name = "ALLOCATION_ID", nullable = false)
    private String allocationId;

    @Column(name = "REF_TRANS_ID")
    private String refTransId;

    @Column(name = "FIN_YEAR")
    private String finYear;

    @Column(name = "AUTH_GROUP_ID")
    private String authGroupId;

    @Column(name = "TO_UNIT")
    private String toUnit;

    @Column(name = "IS_TYPE")
    private String isTYpe;

    @Column(name = "FROM_UNIT")
    private String fromUnit;

    @Column(name = "SUB_HEAD")
    private String subHead;

    @Column(name = "ALLOCATION_AMOUNT")
    private String allocationAmount;

    @Column(name = "PREV_ALLOC_AMOUNT" ,columnDefinition = "varchar(255) default 0")
    private String prevAllocAmount;

    @Column(name = "PREV_INITIAL" ,columnDefinition = "varchar(255) default 0")
    private String prevInitial;

    @Column(name = "UNALLOCATED_AMOUNT")
    private String unallocatedAmount;

    @Column(name = "ALLOCATION_TYPE_ID")
    private String allocationTypeId;


    @Column(name = "REVISED_AMOUNT")
    private String revisedAmount;

    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "IS_FLAG")
    private String isFlag;

    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "UPDATED_ON")
    private Timestamp updatedOn;

    @Column(name = "AMOUNT_TYPE")
    private String amountType;

    @Column(name = "IS_BUDGET_REVISION")
    private String isBudgetRevision;

//    @Column(name = "REVISION_BY")
//    private String revisionBy;

    @Column(name = "RETURN_REMARKS")
    private String returnRemarks;


}
