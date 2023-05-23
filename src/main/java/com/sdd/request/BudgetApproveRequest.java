package com.sdd.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class BudgetApproveRequest {



    private String authGroupId;
    private String transactionId;
    private String remarks;
    private String status;
    private List<CdaCrDrAllocationRequest> cdaParkingId;

}
