package com.sdd.response;

import com.sdd.entities.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class CdaDetailsForRebaseResponse {
    private AmountUnit amountUnit;
    private String remarks;
    private CdaParking ginNo;
    private String remainingCdaAmount;
    private String totalParkingAmount;
    private String subHeadId;
}