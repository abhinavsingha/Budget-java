package com.sdd.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sdd.entities.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;


@Getter
@Setter
public class CbReportResponse {


    private String expenditureAmount;
    private String balanceAmount;
    private String currentBillAmount;
    private String getGst;
    private String allocatedAmount;
    private String hindiAmount;
    private String remeningAmount;
    private String onAccountData;
    private String onAurthyData;
    private ContigentBill cbData;
    private CgUnit unitData;
    private BudgetHead budgetHead;

    private HrData approver;
    private HrData verifer;

    private Authority authorityDetails;

}
