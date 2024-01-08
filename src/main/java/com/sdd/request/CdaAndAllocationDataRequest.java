package com.sdd.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CdaAndAllocationDataRequest {


    private String financialYearId;
    private String budgetHeadId;
    private String amountType;
    private String allocationTypeId;

}
