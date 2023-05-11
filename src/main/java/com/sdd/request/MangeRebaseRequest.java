package com.sdd.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class MangeRebaseRequest {

    private String authority;
    private String authDate;
    private String remark;
    private String authUnitId;
    private String authDocId;

    private String authorityId;
    private List<UnitRebaseRequest> unitRebaseRequests;

}
