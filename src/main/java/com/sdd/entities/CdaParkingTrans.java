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
@Table(name = "CdaParkingTrans")
public class CdaParkingTrans {
    @Id
    @Column(name = "CDA_PARKING_ID", nullable = false)
    private String cdaParkingId;

    @Column(name = "FIN_YEAR_ID")
    private String finYearId;

    @Column(name = "BUDGET_HEAD_ID")
    private String budgetHeadId;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "GIN_NO")
    private String ginNo;

    @Column(name = "UNID_ID")
    private String unitId;

    @Column(name = "REMAINING_CDA_AMOUNT")
    private String remainingCdaAmount;

    @Column(name = "AUTH_GROUP_ID")
    private String authGroupId;

    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "UPDATED_ON")
    private Timestamp updatedOn;

    @Column(name = "ALLOC_TYPE_ID")
    private String allocTypeId;

    @Column(name = "isFlag")
    private String isFlag;

    @Column(name = "TRANSACTION_ID")
    private String transactionId;

    @Column(name = "AMOUNT_TYPE")
    private String amountType;

}
