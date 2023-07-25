package com.sdd.response;

import com.sdd.entities.BudgetFinancialYear;
import com.sdd.entities.BudgetHead;
import com.sdd.entities.CgUnit;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GrTotalObj {

    String unitName;

    String finYear;

    String allocType;

    String amountIn;

    String allocatedAmount;

    String expenditureAmount;

    String balAmount;

    String perAmount;

    String lastCBDate;
}
