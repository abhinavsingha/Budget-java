package com.sdd.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class CDARequestReBase {


    private String financialYearId;
    private String budgetHeadId;
    private String allocationTypeId;
    private String authGroupId;
    private String amountType;

}
