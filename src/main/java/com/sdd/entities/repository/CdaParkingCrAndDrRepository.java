package com.sdd.entities.repository;


import com.sdd.entities.AllocationType;
import com.sdd.entities.CdaParkingCrAndDr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CdaParkingCrAndDrRepository extends JpaRepository<CdaParkingCrAndDr, String> {


    List<CdaParkingCrAndDr> findByAuthGroupIdAndBudgetHeadIdAndIsFlag(String authGroupId, String budgetHedaid, String s);
    List<CdaParkingCrAndDr> findByTransactionIdAndIsFlag(String authGroupId,  String s);
    CdaParkingCrAndDr findByCdaCrdrIdAndIsFlag(String authGroupId,  String s);

    CdaParkingCrAndDr findByFinYearIdAndBudgetHeadIdAndGinNoAndIsFlagAndAndAllocTypeIdAndUnitId(String finYearId, String budgetHeadID, String andGinNo,String  isflag,String  allocation,String unitId);
}
