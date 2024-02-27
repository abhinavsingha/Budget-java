package com.sdd.entities.repository;


import com.sdd.entities.AllocationType;
import com.sdd.entities.CdaParkingCrAndDr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CdaParkingCrAndDrRepository extends JpaRepository<CdaParkingCrAndDr, String> {


    List<CdaParkingCrAndDr> findByAuthGroupIdAndBudgetHeadIdAndIsFlagAndIsRevision(String authGroupId, String budgetHedaid, String s,  Integer isRevision);
    List<CdaParkingCrAndDr> findByAuthGroupIdAndBudgetHeadId(String authGroupId, String budgetHedaid);
    List<CdaParkingCrAndDr> findByAuthGroupId(String authGroupId);
    List<CdaParkingCrAndDr> findByTransactionIdAndIsFlagAndIsRevision(String authGroupId,  String s,  Integer isRevision);
    List<CdaParkingCrAndDr> findByTransactionIdAndIsFlag(String transId,String isFlag);
    List<CdaParkingCrAndDr> findByTransactionId(String transId);
    CdaParkingCrAndDr findByTransactionIdAndGinNoAndIsFlagAndIsRevision(String authGroupId, String ginNo,  String s,  Integer isRevision);
    CdaParkingCrAndDr findByCdaCrdrIdAndIsFlagAndIsRevision(String authGroupId,  String s,  Integer isRevision);

    List<CdaParkingCrAndDr> findByFinYearIdAndBudgetHeadIdAndGinNoAndIsFlagAndAndAllocTypeIdAndUnitIdAndIsRevision(String finYearId, String budgetHeadID, String andGinNo,String  isflag,String  allocation,String unitId,  Integer isRevision);
    List<CdaParkingCrAndDr> findByFinYearIdAndBudgetHeadIdAndIsFlagAndAndAllocTypeIdAndUnitIdAndIsRevision(String finYearId, String budgetHeadID,String  isflag,String  allocation,String unitId,  Integer isRevision);
    List<CdaParkingCrAndDr> findByFinYearIdAndIsFlagAndAndAllocTypeIdAndUnitIdAndIsRevision(String finYearId, String  isflag,String  allocation,String unitId,  Integer isRevision);

    List<CdaParkingCrAndDr> findByFinYearIdAndBudgetHeadIdAndAllocTypeIdAndUnitId(String finYear, String budgetHeadId, String allocationTypeId, String unit);
}
