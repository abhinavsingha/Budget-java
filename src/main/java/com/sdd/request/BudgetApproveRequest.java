package com.sdd.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BudgetApproveRequest {



    private String authGroupId;
    private String transactionId;
    private String remarks;
    private String status;

}
