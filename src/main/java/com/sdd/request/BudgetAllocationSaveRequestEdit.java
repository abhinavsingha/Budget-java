package com.sdd.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class BudgetAllocationSaveRequestEdit {

    private String msgId;
    private List<BudgetAllocationSubRequestEdit> budgetRequest;

}
