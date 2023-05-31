package com.sdd.response;
import lombok.Getter;
import lombok.Setter;


import java.util.Date;
import java.util.List;

@Getter
@Setter
public class UnitRebaseReportResponce {
    private String unitName;
    private Date dateOfRebase;
    private String fromStation;
    private String toStation;
    private List<UnitRebaseSubReportResponce> list;

}
