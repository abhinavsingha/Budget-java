package com.sdd.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ApproveContigentBillRequest {

    private String status;
    private String remarks;
    private String groupId;

    private String cbId;
    private List<CdaTransAllocationRequest> cdaParkingId;
}
