package com.sdd.response;

import com.sdd.entities.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AuthirtyResponse {


    private BudgetHead subHead;
    private AllocationType allocationType;
    private String foundAvailable;
    private String balanceFund;
    private Authority authority;
    private FileUpload fileUpload;
    private BudgetAllocationDetails budgetAllocationsDetalis;


}
