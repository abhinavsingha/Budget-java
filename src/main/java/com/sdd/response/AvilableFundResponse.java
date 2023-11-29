package com.sdd.response;

import com.sdd.entities.*;
import com.sdd.request.CdaFilterData;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter

public class AvilableFundResponse {

   String fundAvailable;
   List<CdaFilterData> cdaParkingTrans;
//   String fundallocated;
   AmountUnit amountUnit;
   String previousAllocation;
   String cbAllocationAMount;
   String unallocatedAmount;
   String unitName;


   String expenditure;

}
