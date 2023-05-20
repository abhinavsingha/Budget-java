package com.sdd.response;

import com.sdd.entities.AllocationType;
import com.sdd.entities.BudgetAllocationDetails;
import com.sdd.entities.BudgetHead;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter

public class BudgetRecioptSubDemoResponse {




    private BudgetHead budgetHead;
    private List<BudgetAllocationDetails> budgetAllocations;

    private AllocationType allocationType;

}
