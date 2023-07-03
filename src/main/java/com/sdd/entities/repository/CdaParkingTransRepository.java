package com.sdd.entities.repository;


import com.sdd.entities.CdaParking;
import com.sdd.entities.CdaParkingTrans;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CdaParkingTransRepository extends JpaRepository<CdaParkingTrans, String> {


    List<CdaParkingTrans> findByFinYearIdAndBudgetHeadIdAndGinNoAndIsFlagAndAndAllocTypeId(String finYearId, String budgetHeadId, String ginNo, String isFlag, String allocationTypeId);

    List<CdaParkingTrans> findByFinYearIdAndBudgetHeadIdAndGinNoAndIsFlagAndAndAllocTypeIdAndUnitId(String finYearId, String budgetHeadId, String ginNo, String isFlag, String allocationTypeId, String unitId);

    List<CdaParkingTrans> findByFinYearIdAndBudgetHeadIdAndGinNoAndIsFlagAndAndAllocTypeIdAndUnitIdIn(String finYearId, String budgetHeadId, String ginNo, String isFlag, String allocationTypeId, List<String> unitId);

    List<CdaParkingTrans> findByTransactionIdAndIsFlag(String transId, String isFlag);

    List<CdaParkingTrans> findByTransactionId(String transId);

    List<CdaParkingTrans> findByFinYearIdAndBudgetHeadIdAndUnitIdAndIsFlag(String finYearId, String budgetHeadId, String unitId, String isFlag);

    List<CdaParkingTrans> findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(String finYearId, String budgetHeadId, String unitId, String allocationTypeId, String isFlag);

    List<CdaParkingTrans> findByAuthGroupIdAndIsFlag(String authGroupId, String isFlag);

    CdaParkingTrans findByCdaParkingIdAndIsFlag(String cdaParkingId, String isFlag);

    CdaParkingTrans findByCdaParkingId(String cdaParkingId);

    List<CdaParkingTrans> findByAuthGroupIdAndBudgetHeadIdAndIsFlag(String authGroupId, String budgetHedaId, String isFlag);

    List<CdaParkingTrans> findByAuthGroupIdAndTransactionIdAndIsFlag(String authGroupId, String transId, String isFlag);

    List<CdaParkingTrans> findByFinYearIdAndUnitIdAndAllocTypeIdAndIsFlag(String serialNo, String headunitid, String allocTypeId, String s);
}
