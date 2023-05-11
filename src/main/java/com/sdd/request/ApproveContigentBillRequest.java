package com.sdd.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ApproveContigentBillRequest {

    private String status;
    private String remarks;
    private String groupId;

    private String cbId;

}
