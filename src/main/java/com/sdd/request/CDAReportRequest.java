package com.sdd.request;

import com.sdd.response.CDAReportResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;


@Getter
@Setter
public class CDAReportRequest {




    private String financialYearId;
    private String majorHead;
    private String allocationTypeId;
    private String cdaType;
    private String amountType;
    private String subHeadType;

    private String reportType;
    private String unitId;
    private String budgetHeadId;


}
