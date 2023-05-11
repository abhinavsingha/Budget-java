package com.sdd.response;

import com.sdd.entities.BudgetHead;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;


@Getter
@Setter
public class BudgetResponseWithToken {


    private ArrayList<BudgetHead> subHead;


}
