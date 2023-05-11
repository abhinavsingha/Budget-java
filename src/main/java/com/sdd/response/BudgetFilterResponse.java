package com.sdd.response;

import com.sdd.entities.BudgetHead;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class BudgetFilterResponse {

    private List<BudgetHeadResponse> subHeads;


}
