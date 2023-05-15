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

    List<BudgetAllocation> findByAuthGroupIdAndIsFlagAndToUnitAndIsBudgetRevision(
            String unitId, String isDelete, String toUnit, String isRevison);

    List<BudgetAllocation> findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(
            String unitId,
            String finYearId,
            String budgetCodeId,
            String allocationType,
            String approved,
            String isFlag,
            String isRevision);



    List<BudgetAllocation> findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndIsFlagAndIsBudgetRevision(
            String unitId, String finYearId, String budgetCodeId, String allocationType, String IsFlag, String isRevison);

    BudgetAllocation findByAllocationIdAndIsFlagAndIsBudgetRevision(String transactionId, String s, String d);

    List<BudgetAllocation> findBySubHeadAndFinYearAndIsFlagAndIsBudgetRevision(
            String subHeadId, String finYear, String isFalg, String isREbision);

    List<BudgetAllocation> findBySubHeadAndAllocationTypeIdAndIsFlagAndIsBudgetRevision(
            String subHeadId, String allocationType, String isFalg, String isRevison);

    @Query(
            value =
                    "select SUB_HEAD from budgetallocation where FIN_YEAR=:finYearId and ALLOCATION_TYPE_ID=:allocationTypeId  group by SUB_HEAD",
            nativeQuery = true)
    List<String> findSubHead(String finYearId, String allocationTypeId);
}
