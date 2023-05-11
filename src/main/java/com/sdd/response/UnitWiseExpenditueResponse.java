package com.sdd.response;

import com.sdd.entities.CgStation;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;


@Getter
@Setter

public class UnitWiseExpenditueResponse {


    List<String> unitWise;
    List<String> allocatedUnit;
    List<String> expenditureUnit;

}
