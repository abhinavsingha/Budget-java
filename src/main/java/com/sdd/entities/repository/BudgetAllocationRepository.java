package com.sdd.entities.repository;

import com.sdd.entities.AllocationType;
import com.sdd.entities.BudgetAllocation;
import com.sdd.entities.BudgetAllocationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface BudgetAllocationRepository extends JpaRepository<BudgetAllocation, Long> {

  List<BudgetAllocation> findByToUnitAndIsFlag(String toUnit, String isFlag);

  List<BudgetAllocation> findByToUnitAndSubHeadAndIsFlag(
      String toUnit, String budgetHeadId, String isFlag);

  List<BudgetAllocation> findByAuthGroupIdAndIsFlag(String toUnit, String isFlag);

  List<BudgetAllocation> findByToUnitAndFinYearAndIsFlag(
      String toUnit, String finYear, String isFalg);

  List<BudgetAllocation> findByAuthGroupIdAndIsFlagAndToUnit(
      String unitId, String isDelete, String toUnit);

  List<BudgetAllocation> findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlag(
      String unitId,
      String finYearId,
      String budgetCodeId,
      String allocationType,
      String approved,
      String IsFlag);

  List<BudgetAllocation>
      findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(
          String unitId,
          String finYearId,
          String budgetCodeId,
          String allocationType,
          String approved,
          String IsFlag,
          String isBudgetRevision);

  List<BudgetAllocation> findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndIsFlag(
      String unitId, String finYearId, String budgetCodeId, String allocationType, String IsFlag);

  BudgetAllocation findByAllocationIdAndIsFlag(String transactionId, String s);

  List<BudgetAllocation> findBySubHeadAndFinYearAndIsFlag(
      String subHeadId, String finYear, String isFalg);

  List<BudgetAllocation> findBySubHeadAndAllocationTypeIdAndIsFlag(
      String subHeadId, String allocationType, String isFalg);

  @Query(
      value =
          "select SUB_HEAD from budgetallocation where FIN_YEAR=:finYearId and ALLOCATION_TYPE_ID=:allocationTypeId  group by SUB_HEAD",
      nativeQuery = true)
  List<String> findSubHead(String finYearId, String allocationTypeId);
}
