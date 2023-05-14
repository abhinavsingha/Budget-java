package com.sdd.response;

import com.sdd.entities.AllocationType;
import com.sdd.entities.BudgetFinancialYear;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DashBoardResponse {

  HradataResponse userDetails;
  AllocationType allocationType;
  String outBox;
  String Inbox;

  SubHeadWiseExpenditueResponse subHeadWiseExpenditure;
  UnitWiseExpenditueResponse unitWiseExpenditure;

  BudgetFinancialYear budgetFinancialYear;
}
