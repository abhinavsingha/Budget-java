package com.sdd.response;

import com.sdd.entities.AmountUnit;
import com.sdd.entities.CgUnit;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetReviResp {

    private CgUnit unit;
    private String allocationAmount;
    private String balAmount;
    private AmountUnit amountType;


    private String unloackedAmount;
    private String status;
    private String flag;


}
