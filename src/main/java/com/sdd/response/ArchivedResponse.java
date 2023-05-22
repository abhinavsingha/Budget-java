package com.sdd.response;

import com.sdd.entities.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ArchivedResponse {

    private AllocationType allocationType;
    private BudgetFinancialYear financialYear;
    private CgUnit toUnit;
    private CgUnit fromUnit;
    private String allocationAmount;
    private String balAmount;
    private String approvedAmount;
    private String ReceiptAmount;
    private AmountUnit amountType;
    private String status;
    private String remarks;
    private Date submissionDate;
    private Date approvedDate;
    private String groupId;
    private BudgetHead budgetHead;
    private String msg;

}
