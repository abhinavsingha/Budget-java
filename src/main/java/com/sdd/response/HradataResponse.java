package com.sdd.response;

import com.sdd.entities.Role;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.List;


@Getter
@Setter
public class HradataResponse {


    private String pid;
    private String pno;
    private String cadre;
    private String fullName;
    private String rank;
    private String offEmail;
    private String mobileNo;
    private String unit;
    private String isActive;
    private String unitDate;
    private String unitId;
    private String toDate;
    private String fromDate;
    private Timestamp adminCreatedOn;
    private List<Role> role;

}
