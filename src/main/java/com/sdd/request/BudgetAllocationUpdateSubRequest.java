package com.sdd.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BudgetAllocationUpdateSubRequest {

    private String budgetFinanciaYearId;
    private String toUnitId;
    private String authorityId;
    private String transactionId;

    private String subHeadId;
    private String amount;
    private String remark;
    private String allocationTypeId;
}
