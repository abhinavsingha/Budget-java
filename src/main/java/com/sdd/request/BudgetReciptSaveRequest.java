package com.sdd.request;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.util.ArrayList;


@Getter
@Setter
public class BudgetReciptSaveRequest {


    private String budgetFinancialYearId;
    private String allocationType;

    private String majorMinerHead;
    private String subHeadType;

    private String amountTypeId;

    ArrayList<AuthRequest> authListData;
    ArrayList<BudgetReciptSubRequest> receiptSubRequests;



    private String budgetHeadType;
    private String majorHeadId;

}
