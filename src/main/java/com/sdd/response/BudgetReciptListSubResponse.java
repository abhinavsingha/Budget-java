package com.sdd.response;

import com.sdd.entities.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;


@Getter
@Setter

public class BudgetReciptListSubResponse {


    private List<AuthorityTableResponse> authList;
    private String transactionId;
    private String allocationId;
    private BudgetFinancialYear finYear;
    private CgUnit fromUnit;
    private CgUnit toUnit;
    private BudgetHead subHead;
    private AmountUnit amountUnit;
    private AllocationType allocTypeId;
    private String allocationAmount;
    private String authGroupId;
    private String status;
    private String purposeCode;
    private Timestamp allocationDate;
    private String remarks;
    private String refTransactionId;
    private String userId;
    private Timestamp createdOn;
    private Timestamp updatedOn;
    private String unallocatedAmount;
    private String isCdaParked;
    List<CdaParkingTrans> cdaParkingListData;


}
