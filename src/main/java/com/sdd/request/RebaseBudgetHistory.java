package com.sdd.request;

import com.sdd.entities.AmountUnit;
import com.sdd.entities.BudgetHead;
import com.sdd.response.CdaDetailsForRebaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class RebaseBudgetHistory {
    private String unit;
    private String finYear;
    private String authGrupId;
    private BudgetHead subHead;
    private double allocatedAmount;
    private double expenditureAmount;
    private double allcAmntSubtrctExpnAmunt;
    private String status;
    private AmountUnit amountType;
    private Date lastCbDate;
    private List<CdaDetailsForRebaseResponse> cdaData;
}
