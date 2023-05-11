package com.sdd.entities.repository;


import com.sdd.entities.CdaParking;
import com.sdd.entities.CdaParkingTrans;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CdaParkingTransRepository extends JpaRepository<CdaParkingTrans, String> {


     List<CdaParkingTrans> findByFinYearIdAndBudgetHeadIdAndGinNoAndIsFlagAndAndAllocTypeId(String finYearId,String budgetHeadId,String ginNo,String isFlag,String allocationTypeId);
     List<CdaParkingTrans> findByTransactionIdAndIsFlag(String transId,String isFlag);
     List<CdaParkingTrans> findByFinYearIdAndBudgetHeadIdAndGinNoAndIsFlagAndUnitId(String finYearId,String budgetHeadId,String ginNo,String isFlag,String unitId);
     List<CdaParkingTrans> findByFinYearIdAndBudgetHeadIdAndUnitIdAndIsFlag(String finYearId,String budgetHeadId,String unitId,String isFlag);
    List<CdaParkingTrans> findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(String finYearId,String budgetHeadId,String unitId,String allocationTypeId,String isFlag);
    List<CdaParkingTrans> findByAuthGroupIdAndIsFlag(String authGroupId,String isFlag);
    List<CdaParkingTrans> findByAuthGroupIdAndBudgetHeadIdAndIsFlag(String authGroupId,String budgetHedaId,String isFlag);
    List<CdaParkingTrans> findByAuthGroupIdAndFinYearIdAndBudgetHeadIdAndIsFlag(String authGroupId,String finYearId,String budgetHeadId,String isFlag);
    List<CdaParkingTrans> findByAuthGroupIdAndFinYearIdAndBudgetHeadIdAndIsFlagAndUnitId(String authGroupId,String finYearId,String budgetHeadId,String isFlag,String unitId);

}
