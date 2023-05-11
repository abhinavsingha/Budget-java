package com.sdd.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BudgetDeleteRequest {


    private String unitId;
    private String transactionId;
    private String remarks;
    private String status;
}
