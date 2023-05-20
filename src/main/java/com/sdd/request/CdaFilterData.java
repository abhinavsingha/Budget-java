package com.sdd.request;

import com.sdd.entities.AmountUnit;
import com.sdd.entities.CdaParking;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Getter
@Setter
public class CdaFilterData {

    private String cdaParkingId;

    private String finYearId;
    private String budgetHeadId;
    private String remarks;
    private CdaParking ginNo;
    private String unitId;
    private String totalParkingAmount;
    private String remainingCdaAmount;
    private String authGroupId;
    private Timestamp createdOn;
    private Timestamp updatedOn;
    private String allocTypeId;
    private String isFlag;
    private String transactionId;
    private AmountUnit amountType;
}
