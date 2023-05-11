package com.sdd.response;

import com.sdd.entities.CgStation;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Getter
@Setter

public class CdaParkingReportResponse {


    private String finYear;
    private String cdaName;
    private String subHead;
    private String amount;


}
