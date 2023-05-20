package com.sdd.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GetAmountRequest {


    private String unitId;
    private String budgetFinancialYearId;
    private String budgetHeadId;


}
