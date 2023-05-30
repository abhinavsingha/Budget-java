package com.sdd.response;

import com.sdd.entities.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;


@Getter
@Setter

public class CdaParkingTransSubResponse {

    private String cdaParkingId;
    private BudgetFinancialYear finYearId;
    private AllocationType allocationType;
    private AmountUnit amountUnit;
    private BudgetHead budgetHead;
    private String remarks;
    private CdaParking ginNo;
    private String remainingCdaAmount;
    private String totalParkingAmount;
    private String unitId;
    private String authGroupId;
    private Timestamp createdOn;
    private Timestamp updatedOn;
    private String transactionId;
    private List<AuthorityTableResponse> authList;

}
