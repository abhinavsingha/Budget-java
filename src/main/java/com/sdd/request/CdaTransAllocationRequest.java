package com.sdd.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class CdaTransAllocationRequest {


    private String cdaParkingId;
    private String cdaAmount;


    // Use For only Rejection Case
    private String allocatedAmount;
    private String allocatedAmountType;
}
