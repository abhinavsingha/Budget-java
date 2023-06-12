package com.sdd.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class FerResponse {
    private String finYear;
    private String amountIn;
    private String allocationType;
    private String upToDate;
    private List<FerSubResponse> FerDetails;
}
