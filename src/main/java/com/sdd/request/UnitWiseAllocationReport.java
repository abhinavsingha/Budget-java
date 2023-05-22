package com.sdd.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnitWiseAllocationReport {
    private String finYearId;
    private String unitId;
    private String amountTypeId;
    private String allocationTypeId;

}
