package com.sdd.request;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
public class UnitRebaseSaveReq {

    private String budgetFinanciaYearId;
    private String toUnitId;
    private String headUnitId;
    private String toStationId;
    private String fromStationId;
    private String authority;
    private String authDate;
    private String remark;
    private String authUnitId;
    private String authDocId;
    private String authorityId;
    private String occurrenceDate;


}
