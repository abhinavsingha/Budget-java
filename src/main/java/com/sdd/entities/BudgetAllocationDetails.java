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

@Transactional
@Table(name = "BudgetAllocationDetails")
public class BudgetAllocationDetails {

    @Id
    @Column(name = "TRANSACTION_ID", nullable = false)
    private String transactionId;


    @Column(name = "ALLOCATION_ID", nullable = false)
    private String allocationId;

    @Column(name = "FIN_YEAR")
    private String finYear;

    @Column(name = "FROM_UNIT", nullable = false)
    private String fromUnit;

    @Column(name = "TO_UNIT", nullable = false)
    private String toUnit;

    @Column(name = "BUDGET_HEAD", nullable = false)
    private String subHead;

    @Column(name = "ALLOC_TYPE_ID", nullable = false)
    private String allocTypeId;

    @Column(name = "ALLOCATION_AMOUNT", nullable = false)
    private String allocationAmount;


    @Column(name = "AUTH_GROUP_ID", nullable = false)
    private String authGroupId;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "PURPOSE_CODE")
    private String purposeCode;

    @Column(name = "ALLOCATION_DATE", nullable = false)
    private Timestamp allocationDate;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "REF_TRANSACTION_ID", nullable = false)
    private String refTransactionId;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "UPDATED_ON", nullable = false)
    private Timestamp updatedOn;

    @Column(name = "IS_DELETE")
    private String isDelete;

    @Column(name = "REVISED_AMOUNT")
    private String revisedAmount;

    @Column(name = "IS_BUDGET_REVISION")
    private String isBudgetRevision;

    @Column(name = "AMOUNT_TYPE")
    private String amountType;


    @Column(name = "RETURN_REMARKS")
    private String returnRemarks;


}
