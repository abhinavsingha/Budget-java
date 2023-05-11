package com.sdd.response;

import com.sdd.entities.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter

public class AvilableFundResponse {

   String fundAvailable;
   String fundallocated;
   AmountUnit amountUnit;
   String previousAllocation;
   String unallocatedAmount;
   String unitName;


   String expenditure;

}
