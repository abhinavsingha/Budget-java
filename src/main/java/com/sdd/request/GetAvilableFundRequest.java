package com.sdd.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GetAvilableFundRequest {


    private String finYearId;
    private String subHeadId;
    private String allocationTypeId;
}
