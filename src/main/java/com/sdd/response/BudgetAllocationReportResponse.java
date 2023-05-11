package com.sdd.response;

import com.sdd.entities.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter

public class BudgetAllocationReportResponse {

    private String AuthorityUnit;
    private String Authority;
    private String Date;
    private String path;


    private ArrayList<BudgetAllocationSubReport> list;


}
