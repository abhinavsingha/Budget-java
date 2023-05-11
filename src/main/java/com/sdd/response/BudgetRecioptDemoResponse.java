package com.sdd.response;

import com.sdd.entities.AllocationType;
import com.sdd.entities.BudgetFinancialYear;
import com.sdd.entities.BudgetHead;
import com.sdd.entities.CgUnit;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Getter
@Setter

public class BudgetRecioptDemoResponse {




    private BudgetHead budgetHead;
    private String re = "0.0";
    private String be = "0.0";
    private String ma = "0.0";
    private String voa = "0.0";
    private String sg = "0.0";
    private String transactionId;

}
