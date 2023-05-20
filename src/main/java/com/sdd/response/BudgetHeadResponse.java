package com.sdd.response;

import com.sdd.entities.AmountUnit;
import com.sdd.entities.CdaParkingTrans;
import com.sdd.request.CdaFilterData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;


@Getter
@Setter

public class BudgetHeadResponse {

    private String budgetCodeId;
    private String codeSubHeadId;
    private String majorHead;
    private String minorHead;
    private String subHeadDescr;
    private String subheadShort;
    private String isActive;
    private String remark;
    private String budgetHeadId;


    private String totalAmount;
    private AmountUnit amountUnit;
    List<CdaFilterData> cdaParkingTrans;

}
