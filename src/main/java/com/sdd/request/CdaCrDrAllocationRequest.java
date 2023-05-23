package com.sdd.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CdaCrDrAllocationRequest {


    private String cdacrDrId;
//    private String cdaAmount;


    // Use For only Rejection Case
    private String allocatedAmount;
    private String allocatedAmountType;
}
