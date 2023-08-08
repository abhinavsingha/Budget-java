package com.sdd.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sdd.entities.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.List;


@Getter
@Setter
public class BudgetAllocationResponse {

    private List<BudgetAllocationSubResponse> budgetResponseist;
    private List<BudgetAllocationSubResponse> oldBudgetRevision;
    private List<Authority> authList;


}
