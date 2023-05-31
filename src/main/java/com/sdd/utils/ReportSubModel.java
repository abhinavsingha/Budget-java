package com.sdd.utils;

import com.sdd.entities.BudgetHead;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ReportSubModel {


    private String remark;
    private String type;
    private String finYear;
    private String amount;
    private String amountType;
    private BudgetHead budgetHead;
    private String unit;
    private String revisedAmount;



}
