package com.sdd.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UnitRebaseRequest {
    private String budgetHeadId;
    private String allocAmount;
    private String expAmount;
    private String balAmount;
    private String amountType;
    private String lastCbDate;

}
