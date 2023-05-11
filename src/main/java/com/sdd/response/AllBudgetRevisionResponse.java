package com.sdd.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sdd.entities.Authority;
import com.sdd.entities.BudgetFinancialYear;
import com.sdd.entities.CgUnit;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AllBudgetRevisionResponse {


    private List<Authority> authList;
    private List<BudgetRecioptDemoResponse> budgetData;
    private HashMap<String, BudgetRecioptDemoResponse> loda;

}
