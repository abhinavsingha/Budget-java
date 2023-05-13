package com.sdd.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CDAReportRequest {




    private String financialYearId;
    private String majorHead;
    private String allocationTypeId;
    private String cdaType;
    private String amountType;
    private String subHeadType;

}
