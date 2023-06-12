package com.sdd.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetReviReq {

    private String budgetFinancialYearId;
    private String subHead;
    private String allocTypeId;
    private String unitId;
}

