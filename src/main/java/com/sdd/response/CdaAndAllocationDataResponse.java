package com.sdd.response;

import com.sdd.entities.BudgetAllocation;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;


@Getter
@Setter

public class CdaAndAllocationDataResponse {



    HashMap<String, CdaParkingTransSubResponse> subHeadData;
    List<BudgetAllocation> budgetAllocationData;


}
