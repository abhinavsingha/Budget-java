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
@Table(name = "BudgetUnitWiseSubHeadFilter")
public class BudgetUnitWiseSubHeadFilter {
    @Id
    @Column(name = "BUWS_FILTER_ID", nullable = false)
    private String buwsFilterId;

    @Column(name = "FIN_YEAR_ID")
    private String finYearId;

    @Column(name = "CODE_SUB_HEAD_ID")
    private String codeSubHeadId;

    @Column(name = "CODE_MAJOR_HEAD_ID")
    private String codeMajorHeadId;

    @Column(name = "UNIT_ID")
    private String unitId;

    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "UPDATED_ON", nullable = false)
    private Timestamp updatedOn;

    @Column(name = "PID_DATA")
    private String pidData;

    @Column(name = "allocationType")
    private String allocationType;


    @Column(name = "subHeadTypeId")
    private String subHeadTypeId;



}
