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

    @Column(name = "TO_UNIT_ID", nullable = false)
    private String toUnitId;

    @Column(name = "FROM_UNIT_ID", nullable = false)
    private String fromUnitId;

    @Column(name = "STATION_ID", nullable = false)
    private String stationId;

    @Column(name = "LAST_CB_DATE", nullable = false)
    private Timestamp lastCbDate;

    @Column(name = "AUTHORITY_ID", nullable = false)
    private String authorityId;

    @Column(name = "USER_ID", nullable = false)
    private String userId;

    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "UPDATED_ON", nullable = false)
    private Timestamp updatedOn;


}
