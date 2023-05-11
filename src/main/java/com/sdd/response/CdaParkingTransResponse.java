package com.sdd.response;

import com.sdd.entities.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;


@Getter
@Setter

public class CdaParkingTransResponse {



    private List<CdaParkingTransSubResponse> cdaParking;

}
