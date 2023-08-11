package com.sdd.response;

import com.sdd.entities.CgStation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;


@Getter
@Setter

public class CgUnitResponse {


    private String unit;
    private String descr;
    private String cgUnitShort;
    private String purposeCode;
    private String cbUnit;
    private String isActive;
    private String isFlag;
    private String unitRhq;
    private String unitDhq;
    private Timestamp createdOn;
    private Timestamp updatedOn;
    private CgStation cgStation;
    private String shipType;
}
