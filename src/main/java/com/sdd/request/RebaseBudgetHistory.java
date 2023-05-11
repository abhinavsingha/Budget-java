package com.sdd.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RebaseBudgetHistory {
    private String unit;
    private String finYear;
    private String subHead;
    private double allocatedAmount;
    private double balAmount;
    private double unlockedAmount;
    private double expenditureAmount;
    private double allcAmntSubtrctExpnAmunt;
    private String status;
    private String amountType;
    private Date lastCbDate;
}
