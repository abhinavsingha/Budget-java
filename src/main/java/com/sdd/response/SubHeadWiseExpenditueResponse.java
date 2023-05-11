package com.sdd.response;

import com.sdd.entities.CgStation;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;


@Getter
@Setter

public class SubHeadWiseExpenditueResponse {


    List<String> subhead;
    List<String> allocatedSubHead;
    List<String> expenditureSubHead;
}
