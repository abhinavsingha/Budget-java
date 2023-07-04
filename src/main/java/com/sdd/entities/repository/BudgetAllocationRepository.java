package com.sdd.entities.repository;

import com.sdd.entities.AllocationType;
import com.sdd.entities.BudgetAllocation;
import com.sdd.entities.BudgetAllocationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface BudgetAllocationRepository extends JpaRepository<BudgetAllocation, Long> {

    List<BudgetAllocation> findByToUnitAndIsFlagAndIsBudgetRevision(String toUnit, String isFlag, String isRebison);

    List<BudgetAllocation> findByToUnitAndSubHeadAndIsFlagAndIsBudgetRevision(
            String toUnit, String budgetHeadId, String isFlag, String isRevison);

    List<BudgetAllocation> findByAuthGroupIdAndIsFlagAndIsBudgetRevision(String toUnit, String isFlag ,String isRevison);

    List<BudgetAllocation> findByToUnitAndFinYearAndIsFlagAndIsBudgetRevision(
            String toUnit, String finYear, String isFalg, String isRevision);
    List<BudgetAllocation> findByToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevision(
            String toUnit, String finYear, String allocationTypeId, String isRevision);

    List<BudgetAllocation> findByAuthGroupIdAndIsFlagAndToUnitAndIsBudgetRevision(
            String unitId, String isFlag, String toUnit, String isRevison);

    List<BudgetAllocation> findByAuthGroupIdAndIsFlagAndToUnit(
            String unitId, String isFlag, String toUnit);

    List<BudgetAllocation> findByAuthGroupIdAndToUnit(
            String unitId, String toUnit);

    List<BudgetAllocation> findByAuthGroupIdAndToUnitAndIsFlag(
            String unitId, String toUnit, String isDelete);


    List<BudgetAllocation> findByAuthGroupIdAndToUnitAndIsBudgetRevisionAndIsFlag(
            String unitId, String toUnit, String isRivison, String isDelete);

    List<BudgetAllocation> findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(
            String unitId,
            String finYearId,
            String budgetCodeId,
            String allocationType,
            String approved,
            String isFlag,
            String isRevision);
    List<BudgetAllocation> findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndIsBudgetRevision(
            String unitId,
            String finYearId,
            String budgetCodeId,
            String allocationType,
            String isRevision);


    List<BudgetAllocation> findByFromUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevision(
            String frmUnit, String finYear, String allocationTypeId, String isRevision);

    List<BudgetAllocation> findByToUnitAndFromUnitAndFinYearAndSubHeadAndAllocationTypeIdAndIsBudgetRevision(
            String unitId,
            String frmUnitId,
            String finYearId,
            String budgetCodeId,
            String allocationType,
            String isRevision);


    BudgetAllocation findByAllocationIdAndIsFlagAndIsBudgetRevision(String transactionId, String s, String d);
    BudgetAllocation findByAllocationId(String transactionId);
    List<BudgetAllocation> findBySubHeadAndFinYearAndAllocationTypeIdAndIsBudgetRevision(
            String subHeadId, String finYear, String allocationTypeId, String isREbision);

    List<BudgetAllocation> findBySubHeadAndFromUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevision(
            String subHeadId,String frmUnit, String finYear, String allocationTypeId, String isRevision);

    List<BudgetAllocation> findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevision(
            String subHeadId,String toUnit, String finYear, String allocationTypeId, String isRevision);
    List<BudgetAllocation> findBySubHeadAndAllocationTypeIdAndIsFlagAndIsBudgetRevision(
            String subHeadId, String allocationType, String isFalg, String isRevison);

    @Query(
            value =
                    "select SUB_HEAD from budgetallocation where FIN_YEAR=:finYearId and ALLOCATION_TYPE_ID=:allocationTypeId and FROM_UNIT=:frmUnit  group by SUB_HEAD",
            nativeQuery = true)
    List<String> findSubHead(String finYearId, String allocationTypeId,String frmUnit);

    List<BudgetAllocation> findByAuthGroupIdAndSubHeadAndToUnit(String authGId,String subHead,String toUnit);

    List<BudgetAllocation> findByAuthGroupIdAndSubHead(String authGId,String subHead);
    List<BudgetAllocation> findByAuthGroupIdAndIsFlagOrderBySubHeadAsc(String authGId,String isFlag);

    List<BudgetAllocation> findByAuthGroupId(String authGroupId);

    @Query(
            value =
                    "select SUB_HEAD from budgetallocation where AUTH_GROUP_ID=:authGroupId  group by SUB_HEAD",
            nativeQuery = true)
    List<String> findSubHeadByAuthGroupIds(String authGroupId);

    List<BudgetAllocation> findByAuthGroupIdAndIsFlag(String authGroupId, String s);
}
