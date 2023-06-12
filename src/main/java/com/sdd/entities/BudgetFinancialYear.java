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
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Transactional
@Table(name = "BudgetFinancialYear")
public class BudgetFinancialYear {
    @Id
    @Column(name = "SERIAL_NO")
    private String serialNo;


    @Column(name = "FIN_YEAR")
    private String finYear;

    @Column(name = "FROM_DATE")
    private Date fromDate;


    @Column(name = "IS_FLAG")
    private String isFlag;

    @Column(name = "TO_DATE")
    private Date toDate;


    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "UPDATED_ON", nullable = false)
    private Timestamp updatedOn;


}
