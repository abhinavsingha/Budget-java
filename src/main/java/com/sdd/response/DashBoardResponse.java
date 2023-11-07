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

  String outBox;
  String Inbox;
  String approved;
  String archived;
  String rejectedBillCount;

  SubHeadWiseExpenditueResponse subHeadWiseExpenditure;
  UnitWiseExpenditueResponse unitWiseExpenditure;

  BudgetFinancialYear budgetFinancialYear;
  AllocationType allocationType;
}
