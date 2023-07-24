package com.sdd.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class RebaseNotificationResp {
    private String unitRebaseName;
    private String loginUnitName;
    private String fromUnitName;
    private String toUnitName;
    private Date dateOfRebase;
    private String fromStation;
    private String toStation;
    private String finYear;
    private String  AllocationType;
    private String subHead;
    private String allocationAmount;
    private String expenditureAmount;
    private String balAmount;
    private String amountType;
    private String authGrpId;
    private Date lastCbDate;
}
