package com.sdd.response;

import com.sdd.entities.AmountUnit;
import com.sdd.entities.CdaParkingTrans;
import com.sdd.entities.CgUnit;
import com.sdd.request.CdaFilterData;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BudgetReviResp {
    private CgUnit unit;
    private String allocationAmount;
    private String expenditureAmount;
    private AmountUnit amountType;
    private String status;
    private String revisedAmount;
    private String flag;
    List<CdaFilterData> cdaTransData;

}
