package com.sdd.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class BudgetAllocationUpdateRequest {

    private String amount;
    private String transactionId;

}
