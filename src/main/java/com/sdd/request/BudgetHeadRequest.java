package com.sdd.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BudgetHeadRequest {


    private String finYearId;
    private String majorHead;
    private String allocationType;

    private String budgetHeadType;
    private String subHeadType;
}
