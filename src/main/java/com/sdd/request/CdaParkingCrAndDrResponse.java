package com.sdd.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sdd.entities.AmountUnit;
import com.sdd.entities.CdaParking;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CdaParkingCrAndDrResponse {

    private String cdaCrdrId;
    private String finYearId;
    private String budgetHeadId;
    private CdaParking ginNo;
    private String unitId;
    private String amount;
    private String remainingAmount;
    private String allocationAmount;
    private String iscrdr;
    private Timestamp createdOn;
    private Timestamp updatedOn;
    private String allocTypeId;
    private String authGroupId;
    private String isFlag;
    private String transactionId;
    private AmountUnit amountType;
    private AmountUnit amountTypeMain;
    private String cdaParkingTrans;

}
