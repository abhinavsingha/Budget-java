package com.sdd.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class BudgetAllocationSaveRequest {

    private List<AuthRequest> authRequests;
    private List<BudgetAllocationSubRequest> budgetRequest;

}
