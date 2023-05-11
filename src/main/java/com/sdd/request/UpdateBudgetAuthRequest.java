package com.sdd.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UpdateBudgetAuthRequest {


    private String authority;
    private String authDate;
    private String remark;
    private String authUnitId;
    private String authDocId;

    private String authorityId;
}
