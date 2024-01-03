package com.sdd.response;

import com.sdd.entities.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CdaParkingHistoryDto {

    private String cdaParkingUpdateId;
    private String oldAmount;
    private CdaParking oldGinNo;
    private String newAmount;
    private CdaParking newGinNo;
    private CgUnit unitId;
    private String authGroupId;
    private String createdOn;
    private String updatedOn;
    private AmountUnit amountType;
    private HrData updatedBy;
    private BudgetHead subHead;

}
