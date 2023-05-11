package com.sdd.request;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;


@Getter
@Setter
public class BudgetReciptUpdateRequest {


    private String budgetFinancialYearId;
    private String allocationTypeId;
    private String amountTypeId;
    private String budgetHeadId;
    private String allocationAmount;



}
