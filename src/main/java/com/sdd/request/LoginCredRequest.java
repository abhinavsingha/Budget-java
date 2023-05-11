package com.sdd.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LoginCredRequest {

    private String userName;
    private String userRoleId;
    private String userUnitId;

}
