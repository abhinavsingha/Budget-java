package com.sdd.response;


import com.sdd.entities.HrData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GwtUserDataResponse {



    private String status;
    private String message;
    private String production;
    private HrData response;

}

