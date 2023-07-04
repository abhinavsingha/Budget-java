package com.sdd.response;

import com.sdd.entities.BudgetHead;
import com.sdd.entities.CgUnit;
import com.sdd.entities.ContigentBill;
import com.sdd.entities.HrData;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CDAReportResponse {


    private String name;
    private String reportType;


    // Reserve Found Case
    private String allocationAmount;
}
