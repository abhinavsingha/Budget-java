package com.sdd.request;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;


@Getter
@Setter
public class AuthRequest {


    private String authority;
    private String authDate;
    private String remark;
    private String authUnitId;
    private String authDocId;

    private String authorityId;

    private String authGroupId;
}
