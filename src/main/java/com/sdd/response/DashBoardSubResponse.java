package com.sdd.response;

import com.sdd.entities.BudgetFinancialYear;
import com.sdd.entities.BudgetHead;
import com.sdd.entities.CgUnit;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Getter
@Setter

public class DashBoardSubResponse {


   CgUnit unit;
   String status;
    String authGroupId;
   BudgetFinancialYear financialYearId;
   BudgetHead subHead;
   String allocatedAmount;
   Timestamp lastCBDate;

}
