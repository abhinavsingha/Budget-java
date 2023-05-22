package com.sdd.response;

import com.sdd.entities.AllocationType;
import com.sdd.entities.CgUnit;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class ApprovedResponse {
    private String type;
    private AllocationType allocationType;
    private Date submissionDate;
    private Date approvedDate;
    private String status;
    private String remarks;
    private CgUnit toUnit;
    private CgUnit fromUnit;
    private String isBgOrCg;
    private String groupId;
    private String amount;

    private String msg;
}
