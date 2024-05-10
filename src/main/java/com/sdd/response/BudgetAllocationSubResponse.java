package com.sdd.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sdd.entities.*;
import com.sdd.request.CdaParkingCrAndDrResponse;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;


@Getter
@Setter
public class BudgetAllocationSubResponse {





    private String transactionId;
    private String allocationId;
    private BudgetFinancialYear finYear;
    private AmountUnit amountUnit;
    private AmountUnit remeningBalanceUnit;
    private CgUnit fromUnit;
    private CgUnit toUnit;
    private BudgetHead subHead;
    private AllocationType allocTypeId;
    private String allocationAmount;
    private List<CdaParkingCrAndDrResponse> cdaData;
    private String authGroupId;
    private String returnRemarks;
    private String status;
    private String purposeCode;
    private Timestamp allocationDate;
    private String isFlag;
    private String unallocatedAmount;
    private String isBudgetRevision;
    private String remarks;
    private String refTransactionId;
    private String userId;
    private String isTYpe;
    private Timestamp createdOn;
    private Timestamp updatedOn;
    private String revisedAmount;
    private double totalAllocationAmount;//addded by deevan 10/05/2024
    private String isCDAparking;
    private List<CdaParkingTrans> cdaList;

}
