package com.sdd.request;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class UnitRebaseSaveReq {

    private String authority;
    private String authDate;
    private String remark;
    private String authUnitId;
    private String authDocId;
    private String occurrenceDate;
    private String finYear;
    private String rebaseUnitId;
    private String headUnitId;
    private String frmStationId;
    private String toStationId;
    private String toHeadUnitId;
    private String allocationTypeId;
    private List<UnitRebaseRequest> unitRebaseRequests;


}
