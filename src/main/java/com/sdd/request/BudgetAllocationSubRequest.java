package com.sdd.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BudgetAllocationSubRequest {

    private String budgetFinanciaYearId;
    private String toUnitId;
    private String subHeadId;
    private String amount = "0";
    private String remark;
    private String revisedAmount;
    private String allocationTypeId;
    private String beAllocationTypeId;
    private String amountTypeId;
    private String transactionId;



    private String isAllocated;
}
