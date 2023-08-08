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

    List<BudgetAllocation> findByToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(String unitId, String finYearId, String allocationTypeIdR, String s, String s1, String approved);


    List<BudgetAllocation> findByToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatusOrderByCreatedOnAsc(String unitId, String finYearId, String allocationTypeIdR, String s, String s1, String approved);



    List<BudgetAllocation> findByAuthGroupIdAndIsFlagAndToUnit(
            String unitId, String isFlag, String toUnit);

    List<BudgetAllocation> findByAuthGroupIdAndToUnit(
            String unitId, String toUnit);

    List<BudgetAllocation> findByAuthGroupIdAndToUnitOrderByCreatedOnAsc(
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

    List<BudgetAllocation> findByToUnitAndFromUnitAndFinYearAndSubHeadAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(
            String unitId,
            String frmUnitId,
            String finYearId,
            String budgetCodeId,
            String allocationType,
            String isRevision,
            String isFlag,
            String status);


    BudgetAllocation findByAllocationId(String transactionId);
    List<BudgetAllocation> findBySubHeadAndFinYearAndAllocationTypeIdAndIsBudgetRevision(
            String subHeadId, String finYear, String allocationTypeId, String isREbision);

    List<BudgetAllocation> findBySubHeadAndFromUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevision(
            String subHeadId,String frmUnit, String finYear, String allocationTypeId, String isRevision);
    List<BudgetAllocation> findBySubHeadAndFromUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(
            String subHeadId,String frmUnit, String finYear, String allocationTypeId, String isRevision,String isFlag,String status);

    List<BudgetAllocation> findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(
            String subHeadId,String toUnit, String finYear, String allocationTypeId, String isRevision, String isFlag);

    List<BudgetAllocation> findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(
            String subHeadId,String toUnit, String finYear, String allocationTypeId, String isRevision,String isFlag,String status);


    @Query(
            value =
                    "select SUB_HEAD from budgetallocation where FIN_YEAR=:finYearId and ALLOCATION_TYPE_ID=:allocationTypeId and FROM_UNIT=:frmUnit  group by SUB_HEAD",
            nativeQuery = true)
    List<String> findSubHead(String finYearId, String allocationTypeId,String frmUnit);



    List<BudgetAllocation> findByAuthGroupId(String authGroupId);


    List<BudgetAllocation> findByAuthGroupIdAndIsFlag(String authGroupId, String s);

    List<BudgetAllocation> findByAuthGroupIdAndSubHead(String authGroupId, String subHeadId);

    @Query(
            value =
                    "select SUB_HEAD from BudgetAllocation where AUTH_GROUP_ID=:authGroupId  group by SUB_HEAD",
            nativeQuery = true)
    List<String> findSubHeadByAuthGroupIds(String authGroupId);

    List<BudgetAllocation> findByFromUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(String frmUnit, String finYearId, String allocationType, String s, String s1, String approved);
}
