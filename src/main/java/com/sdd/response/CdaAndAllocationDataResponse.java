package com.sdd.response;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;


@Getter
@Setter

public class CdaAndAllocationDataResponse {



    HashMap<String, CdaParkingTransSubResponse> subHeadData;

}
