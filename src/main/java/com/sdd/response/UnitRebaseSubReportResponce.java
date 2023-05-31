package com.sdd.response;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UnitRebaseSubReportResponce {
    private String finYear;
    private String  AllocationType;
    private String subHead;
    private String allocationAmount;
    private String expenditureAmount;
    private String balAmount;
    private String amountType;
    private Date lastCbDate;

}
