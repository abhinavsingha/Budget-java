package com.sdd.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class CDARequest {


    private String financialYearId;
    private String ginNo;
    private String budgetHeadId;
    private String amountTypeId;
    private String allocationTypeId;
    private String authGroupId;
    private String totalExpenditure;
    private List<CdaSubRequest> cdaRequest;


}
