package com.sdd.response;

import com.sdd.entities.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;


@Getter
@Setter

public class BudgetRecioptDemoResponse {





    private List<BudgetRecioptSubDemoResponse> data;
    private AllocationType allocationType;

}
