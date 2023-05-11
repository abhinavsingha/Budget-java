package com.sdd.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BudgetFilterRequest {

    private String unitId;
    private String majorHead;
    private String finyearId;

    private String subHeadId;
    private String allocationType;

    private String subHeadTypeId;
}
