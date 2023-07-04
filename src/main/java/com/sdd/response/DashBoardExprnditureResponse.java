package com.sdd.response;

import com.sdd.entities.AllocationType;
import com.sdd.entities.BudgetFinancialYear;
import com.sdd.entities.BudgetHead;
import com.sdd.entities.CgUnit;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashBoardExprnditureResponse {

  CgUnit cgUnit;

  BudgetFinancialYear budgetFinancialYear;

  BudgetHead budgetHead;

  String allocatedAmount;

  String expenditureAmount;

  String balAmount;

  String perAmount;

  String amountIn;

  String lastCBDate;
}
