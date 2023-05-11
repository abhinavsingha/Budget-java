package com.sdd.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class BudgetAllocationReportRequest {

    private String budgetFinancialYearId;
    private String toUnitId;
    private String subHead;


}
