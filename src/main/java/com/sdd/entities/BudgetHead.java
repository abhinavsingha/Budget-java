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
@Table(name = "BudgetHead")
public class BudgetHead {
    @Id
    @Column(name = "BUDGET_CODE_ID", nullable = false)
    private String budgetCodeId;

    @Column(name = "CODE_SUBHEAD_ID")
    private String codeSubHeadId;

    @Column(name = "MAJOR_HEAD")
    private String majorHead;

    @Column(name = "MINOR_HEAD")
    private String minorHead;

    @Column(name = "SUBHEAD_DESCR")
    private String subHeadDescr;

    @Column(name = "SUBHEAD_SHORT")
    private String subheadShort;

    @Column(name = "IS_ACTIVE")
    private String isActive;

    @Column(name = "REAMRKS")
    private String remark;

    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "UPDATED_ON", nullable = false)
    private Timestamp updatedOn;

    @Column(name = "BUDGET_HEAD_CODE", nullable = false)
    private String budgetHeadId;

    @Column(name = "SUB_HEAD_TYPE_ID")
    private String subHeadTypeId;

    @Column(name = "SERIAL_NUMBER")
    private String serialNumber;
}
