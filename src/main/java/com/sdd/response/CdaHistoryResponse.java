package com.sdd.response;

import com.sdd.entities.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CdaHistoryResponse {

    List<CdaParkingHistoryDto> oldCda ;
    List<CdaParkingHistoryDto> newCda ;
    Double newDataSum;
    Double oldDataSum;
}
