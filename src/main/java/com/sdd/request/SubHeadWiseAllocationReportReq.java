package com.sdd.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubHeadWiseAllocationReportReq {
    private String finYearId;
    private String amountTypeId;
    private String subHeadId;
    private String allocationTypeId;
}
