package com.sdd.response;

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
    private List<Authority> authList;


}
