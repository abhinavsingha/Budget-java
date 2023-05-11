package com.sdd.response;

import com.sdd.entities.CgStation;
import com.sdd.entities.CgUnit;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Getter
@Setter

public class BudgetRevisionResponse {


    private CgUnit unit;
    private String existingAmount;



}
