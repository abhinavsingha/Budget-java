package com.sdd.response;

import com.sdd.entities.AllocationType;
import com.sdd.entities.BudgetAllocationDetails;
import com.sdd.entities.CgUnit;
import com.sdd.entities.BudgetHead;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FindBudgetResponse {


    private BudgetHead subHead;
    private AllocationType allocationType;
    private BudgetAllocationDetails budgetAllocationsDetalis;
    private String amount;
    private String remark;
    private CgUnit unit;
}
